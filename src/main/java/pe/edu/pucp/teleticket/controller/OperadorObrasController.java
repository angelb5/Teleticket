package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.entity.Genero;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.entity.Persona;
import pe.edu.pucp.teleticket.repository.GeneroRepository;
import pe.edu.pucp.teleticket.repository.PersonaRepository;
import pe.edu.pucp.teleticket.repository.PersonalRepository;

import java.util.List;

@Controller
@RequestMapping("/operador/obras")
public class OperadorObrasController {

    @Autowired
    PersonaRepository personaRepository;
    @Autowired
    PersonalRepository personalRepository;
    @Autowired
    GeneroRepository generoRepository;

    @GetMapping({"/","","/lista"})
    public String listarObras(){
        return "operador/obras/lista";
    }

    @GetMapping("/gestion")
    public String gestionObra(){
        return "operador/obras/gestionobra";
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
