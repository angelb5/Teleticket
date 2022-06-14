package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.dto.CalificacionPersonaDTO;
import pe.edu.pucp.teleticket.repository.CalificacionPersonaRepository;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import java.util.List;

@Controller
@RequestMapping("estadisticas")
public class OperadorEstadisticasController {

    @Autowired
    CalificacionPersonaRepository calificacionPersonaRepository;
    @Autowired
    SedeRepository sedeRepository;
    @GetMapping(value = "/listar")
    public String  listaActoresConEstrellas(Model model) {
        List<CalificacionPersonaDTO> calificacionPersonaDTOList = calificacionPersonaRepository.encontrarEstrellasActor();
        Integer totalSedes= sedeRepository.totalSedesDisponibles();
        model.addAttribute("listaCalificacionActores",calificacionPersonaDTOList);
        model.addAttribute("totalSedes", totalSedes);
        return "/operador/funciones/estadisticas/personaslista";

    }
}
