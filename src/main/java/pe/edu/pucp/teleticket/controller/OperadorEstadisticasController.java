package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.repository.*;

import java.util.List;

@Controller
@RequestMapping("/operador/estadisticas")
public class OperadorEstadisticasController {

    @Autowired
    CalificacionPersonaRepository calificacionPersonaRepository;
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    ObraRepository obraRepository;
    @Autowired
    PersonaRepository personaRepository;
    @Autowired
    HistorialRepository historialRepository;

    @GetMapping({"/", ""})
    public String  listaActoresConEstrellas(Model model) {
        Integer totalSedes= sedeRepository.totalSedesDisponibles();
        List<PersonasListado> directores= personaRepository.top5Directores();
        List<PersonasListado> actores= personaRepository.top5Actores();
        Integer clientes= clienteRepository.totalCliente();
        Integer obras = obraRepository.contarListaObrasCliente("");
        Integer totalActoresDirectores= personaRepository.contarPersonas();

        Integer totalTickets= historialRepository.ticketsTotale();
        float venta=historialRepository.totalVenta();
        model.addAttribute("directores",directores);
        model.addAttribute("actores",actores);
        model.addAttribute("totalCliente",clientes);
        model.addAttribute("totalObras",obras);
        model.addAttribute("totalAD",totalActoresDirectores);
        model.addAttribute("totalTickets",totalTickets);
        model.addAttribute("ventas",venta);
        model.addAttribute("totalSedes", totalSedes);
        return "operador/estadisticas/estadisticas";

    }
}
