package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.dto.FuncionesCompra;
import pe.edu.pucp.teleticket.dto.SedeFiltro;
import pe.edu.pucp.teleticket.dto.SedesCompra;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Historial;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.repository.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/obras")
public class ClienteObrasController {
    private final int obrasPaginas =6;
    private final int funcionesPaginas = 5;
    private final int maximoDestacados = 8;

    private final List<String> formatos= Arrays.asList("media/png","media/jpeg", "image/jpeg", "image/png");

    private boolean verificarFoto(MultipartFile file){
        if(formatos.contains(file.getContentType().toLowerCase(Locale.ROOT))){
            return true;
        }
        return false;
    }

    @Autowired
    PersonaRepository personaRepository;


    @Autowired
    ObraRepository obraRepository;

    @Autowired
    FuncionRepository funcionRepository;

    @Autowired
    FotoObraRepository fotoObraRepository;

    @Autowired
    CalificacionObraRepository calificacionObraRepository;

    @GetMapping("/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("fecha") Optional<String> optfecha){
        int id=0;
        LocalDate fecha;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/obras";
        }

        Optional<Obra> optionalObra =obraRepository.findById(id);
        if(optionalObra.isEmpty()){return "redirect:/obras";}

        List<LocalDate> fechas = new ArrayList<>();
        List<String> strFechas = funcionRepository.listaFechasDeObra(id);
        strFechas.forEach((f)->fechas.add(LocalDate.parse(f)));

        if (fechas.size()<1){return "redirect:/obras";}

        try{
            fecha = optfecha.isPresent()? LocalDate.parse(optfecha.get()) : fechas.get(0);
        } catch (Exception e){
            return "redirect:/obras";
        }

        if (!fechas.contains(fecha)){return "redirect:/obras";}

        List<SedesCompra> sedesCompraList = funcionRepository.listaSedesPorObraFecha(id,fecha);
        for(SedesCompra sedeCompra: sedesCompraList){
            sedeCompra.setFuncionesCompra(funcionRepository.listaFuncionesCompraPorObraFechaSede(id,fecha,sedeCompra.getId()));
        }

        model.addAttribute("fechas", fechas);
        model.addAttribute("sedes", sedesCompraList);
        model.addAttribute("obra", optionalObra.get());
        model.addAttribute("fotos", fotoObraRepository.findAllIdByIdObras(id));
        model.addAttribute("estrellas", calificacionObraRepository.calificaciones);

        return "/cliente/obras/obra";
    }

}
