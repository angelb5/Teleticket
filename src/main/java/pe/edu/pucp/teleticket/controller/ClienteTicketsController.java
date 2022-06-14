package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.CalificacionObraRepository;
import pe.edu.pucp.teleticket.repository.CalificacionPersonaRepository;
import pe.edu.pucp.teleticket.repository.HistorialRepository;
import pe.edu.pucp.teleticket.service.EmailService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/cliente/tickets")
@Controller
public class ClienteTicketsController {

    private final int historialPaginas = 3;

    private final String url = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&margin=7&data=";

    @Autowired
    HistorialRepository historialRepository;

    @Autowired
    CalificacionPersonaRepository cpRepository;

    @Autowired
    CalificacionObraRepository coRepository;

    @Autowired
    EmailService emailService;

    @GetMapping({"/","","/vigentes"})
    public String listarVigentes(Model model, @RequestParam("pag") Optional<Integer> pag, HttpSession session){
        int pagina = pag.isEmpty()? 1 : pag.get();

        Cliente clienteSes = (Cliente) session.getAttribute("usuario");
        int paginas = (int) Math.ceil((float)historialRepository.contarComprasVigentes(clienteSes.getId())/historialPaginas);
        pagina = pagina>paginas? paginas : pagina;
        pagina = pagina<1? 1 : pagina;
        Pageable lista = PageRequest.of(pagina-1, historialPaginas);
        List<Historial> comprasVigentes = historialRepository.findComprasVigentes(clienteSes.getId(), lista);

        model.addAttribute("comprasVigentes",comprasVigentes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);

        return "cliente/tickets/vigentes";
    }

    @GetMapping("/califica")
    public String listarAsistidas(Model model, @RequestParam("pag") Optional<Integer> pag, HttpSession session){
        int pagina = pag.isEmpty()? 1 : pag.get();

        Cliente clienteSes = (Cliente) session.getAttribute("usuario");
        int paginas = (int) Math.ceil((float)historialRepository.contarComprasAsistidas(clienteSes.getId())/historialPaginas);
        pagina = pagina>paginas? paginas : pagina;
        pagina = pagina<1? 1 : pagina;
        Pageable lista = PageRequest.of(pagina-1, historialPaginas);
        List<Historial> comprasAsistidas = historialRepository.findComprasAsistidas(clienteSes.getId(), lista);

        model.addAttribute("comprasAsistidas",comprasAsistidas);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);

        return "cliente/tickets/asistidas";
    }

    @GetMapping("/vigentes/{idPath}")
    public String mostrarTicketVigente(Model model, @PathVariable("idPath") String idcompra, HttpSession session){

        Cliente clienteSes = (Cliente) session.getAttribute("usuario");

        Optional<Historial> optionalHistorial = Optional.ofNullable(historialRepository.findVigenteById(clienteSes.getId(),idcompra));
        if(optionalHistorial.isEmpty()){return "redirect:/cliente/tickets";}
        Historial compra = optionalHistorial.get();
        if(compra.getFuncion().getFecha().isEqual(LocalDate.now())){
            model.addAttribute("nocancelable", "La compra no puede cancelarse por ser el día de la función");
        }

        model.addAttribute("ticket", compra);
        model.addAttribute("qrcode", url+compra.getIdcompra());
        return "cliente/tickets/vigente";
    }

    @GetMapping("/califica/{idPath}")
    public String mostrarTicketAsistido(Model model, @PathVariable("idPath") String idcompra, HttpSession session){

        Cliente clienteSes = (Cliente) session.getAttribute("usuario");

        Optional<Historial> optionalHistorial = Optional.ofNullable(historialRepository.findAsistidaById(clienteSes.getId(),idcompra));
        if(optionalHistorial.isEmpty()){return "redirect:/cliente/tickets";}
        Historial compra = optionalHistorial.get();

        Integer calificacionObra = coRepository.findEstrellasObraCliente(compra.getFuncion().getId(), clienteSes.getId(), compra.getFuncion().getObra().getId());
        List<Integer> calificacionDirectores = new ArrayList<>();
        List<Integer> calificacionActores = new ArrayList<>();

        for(Persona p : compra.getFuncion().getObra().getDirectores()){
            calificacionDirectores.add(cpRepository.findEstrellasDireccionCliente(compra.getFuncion().getId(), clienteSes.getId(), p.getId()));
        }

        for(Persona p : compra.getFuncion().getObra().getActores()){
            calificacionActores.add(cpRepository.findEstrellasActuacionCliente(compra.getFuncion().getId(), clienteSes.getId(), p.getId()));
        }

        model.addAttribute("ticket", compra);
        model.addAttribute("qrcode", url+compra.getIdcompra());
        model.addAttribute("calificacionObra",calificacionObra);
        model.addAttribute("calificacionDirectores", calificacionDirectores);
        model.addAttribute("calificacionActores", calificacionActores);

        return "cliente/tickets/asistida";
    }

    @PostMapping("/cancelarcompra")
    public String cancelarCompra(String idcompra, RedirectAttributes attr, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        Optional<Historial> optCompra = Optional.ofNullable(historialRepository.findVigenteById(clienteSes.getId(), idcompra));
        if(optCompra.isEmpty()){
            return "redirect:/cliente/tickets";
        }
        Funcion f = optCompra.get().getFuncion();
        if(LocalDate.now().isEqual(f.getFecha())){
            return "redirect:/cliente/tickets";
        }

        historialRepository.cancelarCompra(idcompra);
        attr.addFlashAttribute("msg", "Su compra ha sido cancelada");

        try{
            emailService.correoCompraCancelada(clienteSes, idcompra);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return "redirect:/cliente/tickets";
    }

    @PostMapping("/calificarobra")
    public String calificarObra(int idobras, int idfunciones, int estrellas, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        if(estrellas>5 || estrellas<1){return "redirect:/cliente/tickets";}

        Optional<Historial> optCompra = Optional.ofNullable(historialRepository.findAsistidaByIdFuncion(clienteSes.getId(), idfunciones));
        if(optCompra.isEmpty()){return "redirect:/cliente/tickets";}

        Historial compra = optCompra.get();
        Obra o = compra.getFuncion().getObra();
        if(o.getId()!=idobras){return "redirect:/cliente/tickets";}

        Optional<Calificacionobra> optCal = Optional.ofNullable(coRepository.findCalificacionobraDB(compra.getFuncion().getId(), clienteSes.getId(), o.getId()));
        if(optCal.isPresent()){
            Calificacionobra coDB =  optCal.get();
            coDB.setEstrellas(estrellas);
            coRepository.save(coDB);

            return "redirect:/cliente/tickets/califica/"+compra.getIdcompra();
        }else{
            Calificacionobra co = new Calificacionobra();
            co.setIdclientes(clienteSes.getId());
            co.setEstrellas(estrellas);
            co.setIdobras(idobras);
            co.setIdfunciones(idfunciones);
            coRepository.save(co);
            return "redirect:/cliente/tickets/califica/"+compra.getIdcompra();
        }
    }

    @PostMapping("/calificarpersona")
    public String calificarPersona(int idpersonas, int idfunciones, int estrellas, String rol, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        if(estrellas>5 || estrellas<1){return "redirect:/cliente/tickets";}

        Optional<Historial> optCompra = Optional.ofNullable(historialRepository.findAsistidaByIdFuncion(clienteSes.getId(), idfunciones));
        if(optCompra.isEmpty()){return "redirect:/cliente/tickets";}

        Historial compra = optCompra.get();
        Obra o = compra.getFuncion().getObra();
        List<Persona> personas;
        boolean perteneceAObra = false;
        if (rol.equals("Direccion")){
            personas = o.getDirectores();
        }else{
            personas = o.getActores();
        }
        for(Persona p : personas){
            if (p.getId() == idpersonas) {
                perteneceAObra = true;
                break;
            }
        }
        if(!perteneceAObra){return "redirect:/cliente/tickets";}

        Optional<Calificacionpersona> optCal = Optional.ofNullable(cpRepository.findCalificacionpersonaDB(compra.getFuncion().getId(), clienteSes.getId(), idpersonas,rol));
        if(optCal.isPresent()){
            Calificacionpersona cpDB =  optCal.get();
            cpDB.setEstrellas(estrellas);
            cpRepository.save(cpDB);

            return "redirect:/cliente/tickets/califica/"+compra.getIdcompra();
        }else{
            Calificacionpersona cp = new Calificacionpersona();
            cp.setIdclientes(clienteSes.getId());
            cp.setEstrellas(estrellas);
            cp.setIdpersonas(idpersonas);
            cp.setRol(rol);
            cp.setIdfunciones(idfunciones);
            cpRepository.save(cp);
            return "redirect:/cliente/tickets/califica/"+compra.getIdcompra();
        }
    }

    @PostMapping("/eliminarcobra")
    public String eliminarCalificacionObra(int idobras, int idfunciones, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        Optional<Historial> optCompra = Optional.ofNullable(historialRepository.findAsistidaByIdFuncion(clienteSes.getId(), idfunciones));
        if(optCompra.isEmpty()){return "redirect:/cliente/tickets";}

        Historial compra = optCompra.get();

        Optional<Calificacionobra> optCal = Optional.ofNullable(coRepository.findCalificacionobraDB(compra.getFuncion().getId(), clienteSes.getId(), idobras));
        if(optCal.isPresent()){
            Calificacionobra coDB =  optCal.get();
            coRepository.deleteById(coDB.getId());
        }
        return "redirect:/cliente/tickets/califica/"+compra.getIdcompra();
    }

    @PostMapping("/eliminarcpersona")
    public String eliminarCalificacionPersona(int idpersonas, int idfunciones, String rol, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        Optional<Historial> optCompra = Optional.ofNullable(historialRepository.findAsistidaByIdFuncion(clienteSes.getId(), idfunciones));
        if(optCompra.isEmpty()){return "redirect:/cliente/tickets";}

        Historial compra = optCompra.get();

        Optional<Calificacionpersona> optCal = Optional.ofNullable(cpRepository.findCalificacionpersonaDB(compra.getFuncion().getId(), clienteSes.getId(), idpersonas,rol));
        if(optCal.isPresent()){
            Calificacionpersona cpDB =  optCal.get();
            cpRepository.deleteById(cpDB.getId());
        }
        return "redirect:/cliente/tickets/califica/"+compra.getIdcompra();
    }
}
