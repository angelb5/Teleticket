package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operador/obras")
public class OperadorObrasController {

    private final int obrasPaginas = 6;
    private final int funcionesPaginas = 5;

    @Autowired
    PersonaRepository personaRepository;
    @Autowired
    GeneroRepository generoRepository;
    @Autowired
    ObraRepository obraRepository;
    @Autowired
    FuncionRepository funcionRepository;
    @Autowired
    FotoObraRepository fotoObraRepository;

    @GetMapping({"/","","/lista"})
    public String listarObras(Model model, @RequestParam("pag") Optional<Integer> pag){
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas = (int) Math.ceil((float)obraRepository.count()/obrasPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista;
        if(pagina==0){
            lista= PageRequest.of(0, obrasPaginas);
        } else {
            lista= PageRequest.of(pagina-1, obrasPaginas);

        }
        List<Obra> listaObras = obraRepository.findAllByOrderByIdAsc(lista);
        model.addAttribute("listaObras",listaObras);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        return "operador/obras/lista";
    }

    @GetMapping("/gestion/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("pag") Optional<Integer> pag){
        int id=0;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/operador/obras";
        }
        Optional<Obra> optionalObra =obraRepository.findById(id);
        if(optionalObra.isEmpty()){return "redirect:/operador/obras";}

        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        Obra obra = new Obra();
        obra.setId(id);
        int paginas = (int) Math.ceil((float)funcionRepository.countAllByObra(obra)/funcionesPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista;
        if(pagina==0){
            lista= PageRequest.of(0, funcionesPaginas);
        } else {
            lista= PageRequest.of(pagina-1, funcionesPaginas);
        }

        List<Funcion> funcionList = funcionRepository.findAllByObraOrderByFechaAsc(obra,lista);


        List<Persona> directores = personaRepository.findDirectoresByIdObra(id);
        List<Persona> actores = personaRepository.findActoresByIdObra(id);

        model.addAttribute("fotos", fotoObraRepository.findAllIdByIdObras(id));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("obra",optionalObra.get());
        model.addAttribute("directores", directores);
        model.addAttribute("actores", actores);
        model.addAttribute("funciones", funcionList);
        return "/operador/obras/gestionobra";
    }

    @GetMapping("/nueva")
    public String nuevaObra(Model model, @ModelAttribute("obra") Obra obra) {
        List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
        List<Genero> generoList = generoRepository.findAll();
        model.addAttribute("personaList", personaList);
        model.addAttribute("generoList", generoList);
        return "operador/obras/nuevaFrm";
    }





}
