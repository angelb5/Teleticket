package pe.edu.pucp.teleticket.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
public class HomeController {
    @GetMapping({"/",""})
    public String mostrarIndex(Authentication auth, Model model){
        String rol = "";
        if(auth!=null) {
            for (GrantedAuthority role : auth.getAuthorities()) {
                rol = role.getAuthority();
                break;
            }
        }
        if(rol.equals("administrador")){
            return "redirect:/admin";
        }
        if (rol.equals("operador")) {
            return "redirect:/operador";
        }
        model.addAttribute("ldt", LocalDateTime.now());
        return "index";
    }
}
