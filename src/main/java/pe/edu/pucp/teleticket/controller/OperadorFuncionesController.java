package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.ObraRepository;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operador/funciones")
public class OperadorFuncionesController {


    @Autowired
    ObraRepository obraRepository;
    @Autowired
    SedeRepository sedeRepository;

    @GetMapping("/nueva")
    public String nuevaObra(@RequestParam("idobra") int idobra, Model model, @ModelAttribute("funcion") Funcion funcion) {
        System.out.println(idobra);
        Optional<Obra> optionalObra = obraRepository.findById(idobra);
        if(optionalObra.isPresent()){
            List<Sede> sedeList = sedeRepository.findAll();
            funcion.setObra(optionalObra.get());
            model.addAttribute("sedeList", sedeList);
            return "operador/funciones/nuevaFrm";
        }else{
            return "redirect:/operador/";
        }

    }
}
