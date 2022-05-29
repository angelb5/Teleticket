package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.repository.CalificacionPersonaRepository;

@Controller
@RequestMapping("/personas")
public class PersonasController {

    @Autowired
    CalificacionPersonaRepository calificacionPersonaRepository;

    @GetMapping({"/","","/lista"})
    public String listarPersonas(){
        return "personas/lista";
    }

    @GetMapping({"/{idPath}"})
    public String califPersonas(Model model, @PathVariable("idPath") String idPath){

        // para las calificaciones y los roles
        model.addAttribute("roles", calificacionPersonaRepository.roles);
        model.addAttribute("estrellas", calificacionPersonaRepository.calificaciones);

        return "redirect:/personas";
    }
}