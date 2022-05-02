package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operador/obras")
public class OperadorObrasController {

    private final int funcionesPaginas = 5;

    @Autowired
    PersonaRepository personaRepository;
    @Autowired
    PersonalRepository personalRepository;
    @Autowired
    GeneroRepository generoRepository;
    @Autowired
    ObraRepository obraRepository;
    @Autowired
    FuncionRepository funcionRepository;

    @GetMapping({"/","","/lista"})
    public String listarObras(){
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

        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("obra",optionalObra.get());
        model.addAttribute("salas", funcionRepository.findAllByObra(obra,lista));
        return "/admin/sedes/gestionsede";
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
