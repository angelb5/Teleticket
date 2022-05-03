package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operador")
public class OperadorHomeController {
    @GetMapping({"/",""})
    public String menuAdmin(){
        return "redirect:/operador/obras";
    }
}
