package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.pucp.teleticket.entity.Fotossede;
import pe.edu.pucp.teleticket.repository.FotoSedeRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class SedesFotosController {

    @Autowired
    FotoSedeRepository fotoSedeRepository;

    @GetMapping("/image/sede/{id}")
    public void mostrarImagen(@PathVariable("id") int id, HttpServletResponse response) throws ServletException, IOException {
        Optional<Fotossede> optionalFotossede = fotoSedeRepository.findById(id);
        if(optionalFotossede.isPresent()){
            Fotossede fotosede =  optionalFotossede.get();

            byte[] imagenComoBytes = fotosede.getFoto();

            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(imagenComoBytes);
            response.getOutputStream().close();
        }
    }
}
