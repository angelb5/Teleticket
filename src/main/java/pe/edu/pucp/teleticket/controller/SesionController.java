package pe.edu.pucp.teleticket.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.pucp.teleticket.entity.Admin;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Operador;
import pe.edu.pucp.teleticket.repository.AdminRepository;
import pe.edu.pucp.teleticket.repository.ClienteRepository;
import pe.edu.pucp.teleticket.repository.OperadorRepository;

import javax.servlet.http.HttpSession;

@Controller
public class SesionController {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    OperadorRepository operadorRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @GetMapping("/login")
    public String loginForm(){
        return "/sesion/login";
    }

    @GetMapping("/redirectPorRol")
    public String redirectPorRol(Authentication auth, HttpSession session){
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()){
            rol = role.getAuthority();
            break;
        }
        if (rol.equals("administrador")){
            Admin admin = adminRepository.findByCorreo(auth.getName());
            session.setAttribute("usuario",admin);
            session.setAttribute("rol", "administrador");
            return "redirect:/admin";
        }else if(rol.equals("operador")){
            Operador operador = operadorRepository.findByCorreo(auth.getName());
            session.setAttribute("usuario",operador);
            session.setAttribute("rol", "operador");
            return "redirect:/operador";
        }else{
            Cliente cliente = clienteRepository.findByCorreo(auth.getName());
            session.setAttribute("usuario",cliente);
            session.setAttribute("rol", "cliente");
            return "redirect:/";
        }
    }
}
