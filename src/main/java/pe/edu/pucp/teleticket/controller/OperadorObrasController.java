package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.entity.Obra;

@Controller
@RequestMapping("/operador/obras")
public class OperadorObrasController {

    @GetMapping("/nueva")
    public String nuevoProductoFrm(Model model, @ModelAttribute("obra") Obra obra) {
        return "operador/obras/nuevaFrm";
    }


}
