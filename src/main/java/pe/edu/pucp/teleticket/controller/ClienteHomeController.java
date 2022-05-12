package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteHomeController {

    @GetMapping({"/",""})
    public String menuAdmin(){
        return "/index";
    }
}