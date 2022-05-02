package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/operadores")
public class AdminOperadoresController {
    @GetMapping({"/","","/lista"})
    public String listarSedes(){
        return "admin/operadores/lista";
    }
}
