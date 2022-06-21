package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.pucp.teleticket.dto.*;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    private final int personasPaginas =6;
    private final int obrasPaginas =8;
    private final int funcionesPaginas = 5;
    private final int maximoDestacados = 8;
    private final int sedesPaginas = 4;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    ObraRepository obraRepository;

    @Autowired
    FuncionRepository funcionRepository;

    @Autowired
    FotoObraRepository fotoObraRepository;

    @Autowired
    SedeRepository sedeRepository;

    @Autowired
    FotoSedeRepository fotoSedeRepository;

    @GetMapping({"/",""})
    public String mostrarIndex(Authentication auth, Model model, @RequestParam("pag") Optional<String> pag, @RequestParam("busqueda") Optional<String> optionalBusqueda){
        String rol = "";
        if(auth!=null) {
            for (GrantedAuthority role : auth.getAuthorities()) {
                rol = role.getAuthority();
                break;
            }
        }
        if(rol.equals("administrador")){
            return "redirect:/admin";
        }
        if (rol.equals("operador")) {
            return "redirect:/operador";
        }
        model.addAttribute("ldt", LocalDateTime.now());

        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta =  busqueda.isBlank()? "/?" : "/?busqueda=" +busqueda +"&";

        int pagina=0;
        try{
            pagina = pag.isEmpty() ? 1 : Integer.parseInt(pag.get());
        } catch (Exception e){
            return "index";
        }
        pagina = pagina<1? 1 : pagina;

        Integer cantidadObrasCliente = obraRepository.contarListaObrasCliente(busqueda)==null? 0: obraRepository.contarListaObrasCliente(busqueda);
        int paginas_obras = (int) Math.ceil((float) cantidadObrasCliente / obrasPaginas);
        pagina = pagina > paginas_obras ? paginas_obras : pagina;
        pagina = pagina < 1 ? 1 : pagina;
        Pageable lista_obras;
        if (pagina == 0) {
            lista_obras = PageRequest.of(0, obrasPaginas);
        } else {
            lista_obras = PageRequest.of(pagina - 1, obrasPaginas);
        }

        int paginas_personas = (int) Math.ceil((float)personaRepository.countPersonasListadoCliente(busqueda)/personasPaginas);
        pagina = pagina>paginas_personas? paginas_personas : pagina;
        Pageable lista_personas ;
        if (pagina == 0) {
            lista_personas = PageRequest.of(0, personasPaginas);
        } else {
            lista_personas = PageRequest.of(pagina - 1, personasPaginas);
        }

        int paginas_sedes = (int) Math.ceil((float) sedeRepository.contarListarSedesCliente(busqueda) / sedesPaginas);
        pagina = pagina > paginas_sedes ? paginas_sedes : pagina;
        Pageable lista_sedes;
        if (pagina == 0) {
            lista_sedes = PageRequest.of(0, sedesPaginas);
        } else {
            lista_sedes = PageRequest.of(pagina - 1, sedesPaginas);
        }

        List<ObrasListado> listaObras = obraRepository.listadoObrasliente(busqueda, lista_obras);
        List<PersonasListado> listaActores = personaRepository.listadoPersonascliente(busqueda, lista_personas);
        List<Sede> listaSedes = sedeRepository.listarSedesCliente(busqueda, lista_sedes);

        model.addAttribute("listaObras", listaObras);
        model.addAttribute("listaActores", listaActores);
        model.addAttribute("listaSedes", listaSedes);
        model.addAttribute("ruta", ruta);

        return "index";
    }

    @GetMapping("/todo")
    public String busquedaTodo(
            Model model,
            @RequestParam("busqueda") String busqueda
    ){
        busqueda = busqueda.trim();
        int pagina=0;
        Pageable lista ;
        lista = PageRequest.of(0, 6);

        List<ObrasListado> listaObras = obraRepository.listadoObrasliente(busqueda, lista);
        List<PersonasListado> listaActores = personaRepository.listadoPersonascliente(busqueda, lista);
        List<Sede> listaSedes = sedeRepository.listarSedesCliente(busqueda, lista);

        model.addAttribute("listaObras", listaObras);
        model.addAttribute("listaActores", listaActores);
        model.addAttribute("listaSedes", listaSedes);
        busqueda=busqueda.replaceAll(" ", "+").toLowerCase();
        model.addAttribute("busqueda",busqueda);


        return "/cliente/todo";
    }

}
