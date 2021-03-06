package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.dto.ObrasListado;
import pe.edu.pucp.teleticket.dto.SedesCompra;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/obras")
public class ClienteObrasController {

    @Value("${aplication.domain}")
    private String DOMINIO;

    private final int obrasPaginas =12;

    @Autowired
    PersonaRepository personaRepository;


    @Autowired
    ObraRepository obraRepository;

    @Autowired
    FuncionRepository funcionRepository;

    @Autowired
    FotoObraRepository fotoObraRepository;

    @Autowired
    CalificacionObraRepository calificacionObraRepository;

    @GetMapping({"/", "", "/lista"})
    public String listarObras(Model model, @RequestParam("pag") Optional<String> pag,
                              @RequestParam("busqueda") Optional<String> optionalBusqueda) {
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta = busqueda.isBlank()? "/obras?" : "/obras?busqueda=" +busqueda +"&";

        int pagina=0;
        try{
            pagina = pag.isEmpty() ? 1 : Integer.parseInt(pag.get());
        } catch (Exception e){
            return "redirect:/obras";
        }

        Integer cantidadObrasCliente = obraRepository.contarListaObrasCliente(busqueda)==null? 0: obraRepository.contarListaObrasCliente(busqueda);
        int paginas = (int) Math.ceil((float) cantidadObrasCliente / obrasPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        pagina = pagina < 1 ? 1 : pagina;
        Pageable lista;
        if (pagina == 0) {
            lista = PageRequest.of(0, obrasPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, obrasPaginas);

        }
        List<ObrasListado> listaObras = obraRepository.listadoObrasCliente(busqueda, lista);
        model.addAttribute("listaObras", listaObras);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);
        return "cliente/obras/lista";
    }

    @GetMapping("/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("fecha") Optional<String> optfecha, HttpSession session){
        int id=0;
        LocalDate fecha;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/obras";
        }

        Optional<Obra> optionalObra =obraRepository.findById(id);
        if(optionalObra.isEmpty()){return "redirect:/obras";}

        List<LocalDate> fechas = new ArrayList<>();
        List<String> strFechas = funcionRepository.listaFechasDeObra(id);
        strFechas.forEach((f)->fechas.add(LocalDate.parse(f)));

        if (fechas.size()<1){return "redirect:/obras";}

        try{
            fecha = optfecha.isPresent()? LocalDate.parse(optfecha.get()) : fechas.get(0);
        } catch (Exception e){
            return "redirect:/obras";
        }

        if (!fechas.contains(fecha)){return "redirect:/obras";}

        List<SedesCompra> sedesCompraList = funcionRepository.listaSedesPorObraFecha(id,fecha);

        Cliente clienteSes = (Cliente) session.getAttribute("usuario");
        if (clienteSes!=null){
            for(SedesCompra sedeCompra: sedesCompraList){
                sedeCompra.setFuncionesCompra(funcionRepository.listaFuncionesCompraPorObraFechaSedeIdcliente(id,fecha,sedeCompra.getId(),clienteSes.getId()));
            }
        }else{
            for(SedesCompra sedeCompra: sedesCompraList){
                sedeCompra.setFuncionesCompra(funcionRepository.listaFuncionesCompraPorObraFechaSede(id,fecha,sedeCompra.getId()));
            }
        }

        model.addAttribute("fechas", fechas);
        model.addAttribute("sedes", sedesCompraList);
        model.addAttribute("obra", optionalObra.get());
        model.addAttribute("fotos", fotoObraRepository.findAllIdByIdObras(id));
        model.addAttribute("puntaje", calificacionObraRepository.getPuntajeByIdobra(id));
        model.addAttribute("dominio", DOMINIO);
        return "cliente/obras/obra";
    }




}
