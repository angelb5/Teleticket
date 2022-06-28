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
@RequestMapping("/admin")
public class AdminHomeController {

    @Autowired
    SedeRepository sedeRepository;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    HistorialRepository historialRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    OperadorRepository operadorRepository;

    @GetMapping({"/","","/menu"})
    public String menuAdmin(Model model){

        List<PersonasListado> directores= personaRepository.top5Directores();
        List<PersonasListado> actores= personaRepository.top5Actores();

        Integer totalTickets= historialRepository.ticketsTotale();
        float venta=historialRepository.totalVenta();

        model.addAttribute("directores",directores);
        model.addAttribute("actores",actores);
        model.addAttribute("totalOperadores", operadorRepository.count());
        model.addAttribute("totalCliente",clienteRepository.count());
        model.addAttribute("totalAD",personaRepository.count());
        model.addAttribute("ADDisponibles",personaRepository.countAllByEstadoEqualsIgnoreCase("Disponible"));
        model.addAttribute("ADNoDisponles", personaRepository.countAllByEstadoEqualsIgnoreCase("No disponible"));
        model.addAttribute("totalTickets",totalTickets);
        model.addAttribute("ventas",venta);
        model.addAttribute("totalSedes", sedeRepository.count());
        model.addAttribute("sedesDisponibles", sedeRepository.countAllByEstadoEqualsIgnoreCase("Disponible"));
        model.addAttribute("sedesMantenimiento", sedeRepository.countAllByEstadoEqualsIgnoreCase("Mantenimiento"));
        model.addAttribute("sedesClausuradas", sedeRepository.countAllByEstadoEqualsIgnoreCase("Clausurada"));
        model.addAttribute("sedesPreferidas", sedeRepository.listarSedesPreferidas());
        return "admin/menu";
    }
}
