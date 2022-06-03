package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.dto.SedesCompra;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.entity.Persona;
import pe.edu.pucp.teleticket.repository.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/personas")
public class ClientePersonasController {
    private final int personasPaginas =6;
    private final int funcionesPaginas = 5;
    private final int maximoDestacados = 8;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    ObraRepository obraRepository;

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

    @GetMapping("/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath){
        int id=0;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/personas";
        }

        Optional<PersonasListado> optPersona = Optional.ofNullable(personaRepository.findPersonasclienteById(id));
        if(optPersona.isEmpty()){return "redirect:/personas";}

        model.addAttribute("persona", optPersona.get());
        model.addAttribute("actuaciones", obraRepository.findActuacionesByIdpersona(id));
        model.addAttribute("direcciones", obraRepository.findDireccionesByIdpersona(id));

        return "/cliente/personas/persona";
    }



}


