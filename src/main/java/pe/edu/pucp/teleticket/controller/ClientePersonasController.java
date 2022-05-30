package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.entity.Persona;
import pe.edu.pucp.teleticket.repository.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/personas")
public class ClientePersonasController {
    private final int personasPaginas =6;
    private final int funcionesPaginas = 5;
    private final int maximoDestacados = 8;

    @Autowired
    PersonaRepository personaRepository;


    @GetMapping({"/","","/lista"})
    public String listarPersonas(Model model, @RequestParam("pag") Optional<String> pag, @RequestParam("busqueda") Optional<String> optionalBusqueda){
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta =  busqueda.isBlank()? "/personas?" : "/personas?busqueda=" +busqueda +"&";

        int pagina=0;
        try{
            pagina = pag.isEmpty() ? 1 : Integer.parseInt(pag.get());
        } catch (Exception e){
            return "redirect:/actores";
        }


        pagina = pagina<1? 1 : pagina;
        int paginas = (int) Math.ceil((float)personaRepository.countPersonasListadoCliente(busqueda)/personasPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista ;
        if (pagina == 0) {
            lista = PageRequest.of(0, personasPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, personasPaginas);

        }
        List<PersonasListado> listaActores = personaRepository.listadoPersonascliente(  busqueda, lista);

        model.addAttribute("listaActores",listaActores);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);

        return "cliente/personas/lista";
    }



}


