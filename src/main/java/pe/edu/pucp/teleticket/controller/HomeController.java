package pe.edu.pucp.teleticket.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    @GetMapping({"/",""})
    public String mostrarIndex(){
        return "index";
    }
}
