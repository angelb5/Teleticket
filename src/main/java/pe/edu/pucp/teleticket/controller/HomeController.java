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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
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
    public String mostrarIndex(Authentication auth, Model model){
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

        Pageable lista = PageRequest.of(0, 8);
        List<ObrasListado> listaObras = obraRepository.listadoObrasCliente("", lista);

        model.addAttribute("listaObrasDestacadas", obraRepository.listarObrasDestacadas());
        model.addAttribute("listaObras",listaObras);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd MMMM, yyyy HH:mm").toFormatter(new Locale("es", "ES"));
        model.addAttribute("ldt", formatter.format(LocalDateTime.now()));

        return "index";
    }

    @GetMapping("/todo")
    public String busquedaTodo(
            Model model,
            @RequestParam("busqueda") Optional<String> optBusqueda
    ){
        String busqueda = optBusqueda.orElse("");
        busqueda = busqueda.trim();
        //corregir listas
        Pageable lista ;
        lista = PageRequest.of(0, 8);
        List<ObrasListado> listaObras = obraRepository.listadoObrasCliente(busqueda, lista);
        List<Sede> listaSedes = sedeRepository.listarSedesCliente(busqueda, lista);
        lista = PageRequest.of(0, 4);
        List<PersonasListado> listaActores = personaRepository.listadoPersonascliente(busqueda, lista);

        model.addAttribute("listaObras", listaObras);
        model.addAttribute("listaActores", listaActores);
        model.addAttribute("listaSedes", listaSedes);
        busqueda=busqueda.replaceAll(" ", "+").toLowerCase();
        model.addAttribute("busqueda",busqueda);

        return "cliente/todo";
    }

}
