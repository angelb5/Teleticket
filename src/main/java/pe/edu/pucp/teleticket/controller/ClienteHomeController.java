package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


public class ClienteHomeController {

    @GetMapping({"/",""})
    public String mostrarIndex(){
        return "/index";
    }
}
