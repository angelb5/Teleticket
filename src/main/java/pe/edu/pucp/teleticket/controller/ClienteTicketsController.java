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

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/cliente/tickets")
@Controller
public class ClienteTicketsController {

    private final int historialPaginas = 3;

    @Autowired
    HistorialRepository historialRepository;

    @Autowired
    CalificacionPersonaRepository cpRepository;

    @Autowired
    CalificacionObraRepository coRepository;


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

        return "/cliente/tickets/vigentes";
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

        return "/cliente/tickets/asistidas";
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

        return "/cliente/tickets/vigente";
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
            calificacionActores.add(cpRepository.findEstrellasDireccionCliente(compra.getFuncion().getId(), clienteSes.getId(), p.getId()));
        }

        model.addAttribute("ticket", compra);
        model.addAttribute("calificacionObra",calificacionObra);
        model.addAttribute("calificacionDirectores", calificacionDirectores);
        model.addAttribute("calificacionActores", calificacionActores);

        return "/cliente/tickets/asistida";
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

        return "redirect:/cliente/tickets";
    }

    @PostMapping("/calificarobra")
    public String calificarObra(int idobras, int idfunciones, int estrellas, HttpSession session){
        System.out.println(idobras);
        System.out.println(idfunciones);
        System.out.println(estrellas);
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

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
}
