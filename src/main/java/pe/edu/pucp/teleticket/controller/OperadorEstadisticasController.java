package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.pucp.teleticket.dto.FuncionEstadisticas;
import pe.edu.pucp.teleticket.dto.ObraEstadisticas;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operador/estadisticas")
public class OperadorEstadisticasController {

    @Value("${aplication.domain}")
    private String DOMINIO;

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

    @Autowired
    FuncionRepository funcionRepository;

    @Autowired
    CalificacionObraRepository calificacionObraRepository;

    @GetMapping({"/", ""})
    public String  listaActoresConEstrellas(Model model) {
        List<PersonasListado> directores= personaRepository.top5Directores();
        List<PersonasListado> actores= personaRepository.top5Actores();
        Integer obras = obraRepository.contarListaObrasCliente("");

        Integer totalTickets= historialRepository.ticketsTotale();
        float venta=historialRepository.totalVenta();

        model.addAttribute("listaObras", obraRepository.listarObrasConFunciones());
        model.addAttribute("directores",directores);
        model.addAttribute("actores",actores);
        model.addAttribute("totalCliente",clienteRepository.count());
        model.addAttribute("totalObras",obras);
        model.addAttribute("totalAD",personaRepository.count());
        model.addAttribute("totalTickets",totalTickets);
        model.addAttribute("ventas",venta);
        model.addAttribute("totalSedes", sedeRepository.countAllByEstadoEqualsIgnoreCase("Disponible"));
        model.addAttribute("dominio", DOMINIO);
        return "operador/estadisticas/estadisticas";
    }

    @ResponseBody
    @GetMapping("/obra/{id}")
    public ResponseEntity<HashMap<String, Object>> estadisticasPorId(@PathVariable("id") String idStr) {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            int id = Integer.parseInt(idStr);
            Optional<Obra> optionalObra = obraRepository.findById(id);
            if (optionalObra.isPresent()) {
                Obra o = optionalObra.get();

                float puntuacion = calificacionObraRepository.getPuntajeByIdobra(id).orElse((float) 0);
                Optional<FuncionEstadisticas> masVista = funcionRepository.hallarFuncionMasVistaPorIdobra(id);
                Optional<FuncionEstadisticas> menosVista = funcionRepository.hallarFuncionMenosVistaPorIdobra(id);
                Optional<FuncionEstadisticas> mejorCalificada = funcionRepository.hallarFuncionMejorCalificadaPorIdobra(id);
                Optional<Float> recaudacion = obraRepository.obtenerRecaudaciontotalPorIdobra(id);

                ObraEstadisticas oEstadisticas = new ObraEstadisticas();
                oEstadisticas.setId(id);
                oEstadisticas.setTitulo(o.getTitulo());
                oEstadisticas.setFotoprincipal(o.getFotoprincipal());
                oEstadisticas.setPuntuacion(puntuacion);
                oEstadisticas.setRecaudacionTotal(recaudacion.orElse((float) 0));
                oEstadisticas.setTotalFunciones(funcionRepository.contarFuncionesPorIdobra(id));
                oEstadisticas.setRealizadas(funcionRepository.contarRealizadasPorIdobra(id));
                oEstadisticas.setVigentes(funcionRepository.contarVigentesPorIdobra(id));
                oEstadisticas.setCanceladas(funcionRepository.contarCanceladasPorIdobra(id));
                oEstadisticas.setFuncionMasVista(masVista.orElse(null));
                oEstadisticas.setFuncionMenosVista(menosVista.orElse(null));
                oEstadisticas.setFuncionMejorCalificada(mejorCalificada.orElse(null));
                responseJson.put("result", "success");
                responseJson.put("obra", oEstadisticas);
            return ResponseEntity.ok(responseJson);
            } else {
                responseJson.put("msg", "Obra no encontrada");
            }
        } catch (NumberFormatException e) {
            responseJson.put("msg", "el ID debe ser un número entero positivo");
        }
        responseJson.put("result", "failure");
        return ResponseEntity.badRequest().body(responseJson);
    }
}
