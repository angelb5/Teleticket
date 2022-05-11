package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Fotossede;
import pe.edu.pucp.teleticket.repository.ClienteRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@RequestMapping("/cliente")
@Controller
public class ClienteController {
    @Autowired
    ClienteRepository clienteRepository;

    @GetMapping("/miperfil")
    public String miPerfilO(Model model, HttpSession session){
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        model.addAttribute("cliente", cliente);
        return "cliente/miperfil";
    }

    @GetMapping("/imagen")
    public void mostrarImagen(HttpSession session, HttpServletResponse response)throws ServletException, IOException {
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        byte[] foto = cliente.getFoto();
        if(foto == null) {
            foto = this.getClass().getClassLoader().getResourceAsStream("static/img/default-profile.png").readAllBytes();
        }
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(foto);
        response.getOutputStream().close();
    }

}
