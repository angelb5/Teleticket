package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.pucp.teleticket.entity.Persona;
import pe.edu.pucp.teleticket.repository.PersonaRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class PersonaFotosController {

    @Autowired
    PersonaRepository personaRepository;

    @GetMapping("/image/persona/{id}")
    public void mostrarImagen(@PathVariable("id") int id, HttpServletResponse response) throws ServletException, IOException {
        Optional<Persona> optionalPersona = personaRepository.findById(id);
        if(optionalPersona.isPresent()){
            Persona persona =  optionalPersona.get();

            byte[] imagenComoBytes = persona.getFoto();
            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(imagenComoBytes);
            response.getOutputStream().close();
        }
    }

}
