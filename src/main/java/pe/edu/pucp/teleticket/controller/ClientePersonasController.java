package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente/personas")
public class ClientePersonasController {
    @GetMapping({"/","","/lista"})
    public String listarPersonas(){
        return "cliente/personas/lista";
    }
}
