package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.entity.Fotosobra;
import pe.edu.pucp.teleticket.entity.Fotossede;
import pe.edu.pucp.teleticket.repository.FotoObraRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Controller
public class ObrasFotosController {

    @Autowired
    FotoObraRepository fotoObraRepository;

    @GetMapping("/image/obra/{id}")
    public void mostrarImagen(@PathVariable("id") int id, HttpServletResponse response) throws ServletException, IOException {
        Optional<Fotosobra> optionalFotosobra = fotoObraRepository.findById(id);
        if(optionalFotosobra.isPresent()){
            Fotosobra fotosobra =  optionalFotosobra.get();

            byte[] imagenComoBytes = fotosobra.getFoto();

            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(imagenComoBytes);
            response.getOutputStream().close();
        }
    }
}
