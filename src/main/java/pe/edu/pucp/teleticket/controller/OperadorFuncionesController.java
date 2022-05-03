package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.entity.Sala;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.FuncionRepository;
import pe.edu.pucp.teleticket.repository.ObraRepository;
import pe.edu.pucp.teleticket.repository.SalaRepository;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operador/funciones")
public class OperadorFuncionesController {

    @Autowired
    ObraRepository obraRepository;
    @Autowired
    SedeRepository sedeRepository;

    @Autowired
    SalaRepository salaRepository;
    @Autowired
    FuncionRepository funcionRepository;

    @GetMapping("/nueva")
    public String nuevaFuncion(@RequestParam("idobra") int idobra, Model model, @ModelAttribute("funcion") Funcion funcion) {
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

    @GetMapping("/editar")
    public String editarFuncion(@RequestParam("idfuncion") int idfuncion, Model model,
                               @ModelAttribute("funcion") Funcion funcion) {
        Optional<Funcion> optionalFuncion = funcionRepository.findById(idfuncion);
        if(optionalFuncion.isEmpty()){return "redirect:/operador/";}
        funcion = optionalFuncion.get();
        if(funcion.getEstado().equals("Cancelada") || funcion.getEstadoRVC().equals("Realizada")){return "redirect:/operador/";}
        model.addAttribute("funcion", funcion);
        return "operador/funciones/editaFrm";
    }

    @PostMapping("/registrar")
    public String registaraFuncion(@ModelAttribute("funcion") @Valid Funcion funcion, BindingResult bindingResult,
                                   RedirectAttributes attr, Model model){
        int idobra = funcion.getObra().getId();
        Optional<Obra> optionalObra = obraRepository.findById(idobra);
        List<Sede> sedeList = sedeRepository.findAll();
        if(idobra==0 || optionalObra.isEmpty() ) {
            return "redirect:/operador/";
        }else if(bindingResult.hasErrors()){
            model.addAttribute("sedeList", sedeList);
            return "operador/funciones/nuevaFrm";
        }else{
            LocalTime minTime = LocalTime.parse( "11:00:00" );
            LocalTime maxTime = LocalTime.parse( "23:00:00" );
            boolean horaError = funcion.getInicio().isAfter(maxTime) || funcion.getInicio().isBefore(minTime);
            if(horaError){
                FieldError fHoraerror = new FieldError("inicio", "inicio", "La hora de inicio debe estar entre 11:00 y 23:00");
                bindingResult.addError(fHoraerror);
            }

            LocalDate minDate = LocalDate.now().plusDays(1);
            LocalDate maxDate = LocalDate.now().plusMonths(4);
            boolean fechaError = funcion.getFecha().isAfter(maxDate) || funcion.getFecha().isBefore(minDate);
            if(fechaError){
                FieldError fFechaerror = new FieldError("fecha", "fecha", "La fecha debe estar entre " +minDate+" y "+maxDate);
                bindingResult.addError(fFechaerror);
            }

            Optional<Sala> optionalSala = salaRepository.findById(funcion.getSala().getId());
            boolean salaError = optionalSala.map(sala -> sala.getEstado().equals("No disponible")).orElse(true);
            boolean aforoError = false;
            if(salaError){
                FieldError fSalaError = new FieldError("sala", "sala", "Debe eligir una sala válida");
                bindingResult.addError(fSalaError);
            }else{
                aforoError = funcion.getStock() > optionalSala.get().getAforo();
                if(aforoError){
                    FieldError fAforoError = new FieldError("stock", "stock", "El aforo de la función no puede ser mayor que el aforo de la sala");
                    bindingResult.addError(fAforoError);
                }
            }

            if(horaError || fechaError || aforoError || salaError){
                model.addAttribute("sedeList", sedeList);
                return "operador/funciones/nuevaFrm";
            }else{
                LocalTime fin = funcion.getInicio().plusMinutes(optionalObra.get().getDuracion());
                funcion.setFin(fin);
                List<Funcion> funcionesConflictoList = funcionRepository.findFuncionesEnConflicto(funcion.getFecha(),funcion.getInicio(),fin,funcion.getObra().getId(), funcion.getSala().getId());
                if(funcionesConflictoList.size()>0){
                    model.addAttribute("funcionesConflictoList", funcionesConflictoList);
                    model.addAttribute("sedeList", sedeList);
                    return "operador/funciones/nuevaFrm";
                }else{
                    funcion.setEstado("Activa");
                    funcionRepository.save(funcion);
                    attr.addFlashAttribute("msg", "Se ha creado la función con éxito");
                    return "redirect:/operador/obras/gestion/" + funcion.getObra().getId();
                }

            }
        }
    }

    @PostMapping("/guardar")
    public String guardarFuncion(@ModelAttribute("funcion") @Valid Funcion funcion, BindingResult bindingResult,
                                   RedirectAttributes attr, Model model){
        int idfuncion = funcion.getId();
        Optional<Funcion> optionalFuncion = funcionRepository.findById(idfuncion);
        if(idfuncion==0 || optionalFuncion.isEmpty() ) {return "redirect:/operador/";}
        Funcion funcionDB = optionalFuncion.get();
        funcion.setSala(funcionDB.getSala());
        funcion.setObra(funcionDB.getObra());
        funcion.setEstado(funcionDB.getEstado());
        funcion.setStock(funcionDB.getStock());
        if(funcion.getEstado().equals("Cancelada") || funcionDB.getEstadoRVC().equals("Realizada")){return "redirect:/operador/";}
        if(bindingResult.hasFieldErrors("fecha") || bindingResult.hasFieldErrors("maxreservas") ||
                bindingResult.hasFieldErrors("inicio") || bindingResult.hasFieldErrors("costo")){
            model.addAttribute("funcion", funcion);
            return "operador/funciones/editaFrm";
        }else{
            Optional<Obra> optionalObra = obraRepository.findById( funcion.getObra().getId());
            LocalTime minTime = LocalTime.parse( "11:00:00" );
            LocalTime maxTime = LocalTime.parse( "23:00:00" );
            boolean horaError = funcion.getInicio().isAfter(maxTime) || funcion.getInicio().isBefore(minTime);
            if(horaError){
                FieldError fHoraerror = new FieldError("inicio", "inicio", "La hora de inicio debe estar entre 11:00 y 23:00");
                bindingResult.addError(fHoraerror);
            }

            LocalDate minDate = LocalDate.now().plusDays(1);
            LocalDate maxDate = LocalDate.now().plusMonths(4);
            boolean fechaError = funcion.getFecha().isAfter(maxDate) || funcion.getFecha().isBefore(minDate);
            if(fechaError){
                FieldError fFechaerror = new FieldError("fecha", "fecha", "La fecha debe estar entre " +minDate+" y "+maxDate);
                bindingResult.addError(fFechaerror);
            }

            Optional<Sala> optionalSala = salaRepository.findById(funcion.getSala().getId());
            boolean salaError = optionalSala.map(sala -> sala.getEstado().equals("No disponible")).orElse(true);
            boolean aforoError = false;
            if(salaError){
                FieldError fSalaError = new FieldError("sala", "sala", "Debe eligir una sala válida");
                bindingResult.addError(fSalaError);
            }else{
                aforoError = funcion.getStock() > optionalSala.get().getAforo();
                if(aforoError){
                    FieldError fAforoError = new FieldError("stock", "stock", "El aforo de la función no puede ser mayor que el aforo de la sala");
                    bindingResult.addError(fAforoError);
                }
            }

            if(horaError || fechaError || aforoError || salaError){
                model.addAttribute("funcion", funcion);
                return "operador/funciones/editaFrm";
            }else{
                LocalTime fin = funcion.getInicio().plusMinutes(optionalObra.get().getDuracion());
                List<Funcion> funcionesConflictoList = funcionRepository.findFuncionesEnConflictoId(funcion.getFecha(),funcion.getInicio(),fin,funcion.getObra().getId(), funcion.getSala().getId(), funcion.getId());
                if(funcionesConflictoList.size()>0){
                    model.addAttribute("funcionesConflictoList", funcionesConflictoList);
                    model.addAttribute("funcion", funcion);
                    return "operador/funciones/editaFrm";
                }else{
                    funcion.setFin(fin);
                    funcionRepository.save(funcion);
                    attr.addFlashAttribute("msg", "Se ha modificado la función con éxito");
                    return "redirect:/operador/obras/gestion/" + funcion.getObra().getId();
                }

            }
        }
    }

    @PostMapping("/cancelar")
    public String cancelarFuncion(@RequestParam("id") int id, RedirectAttributes attr){
        Optional<Funcion> optionalFuncion = funcionRepository.findById(id);
        if(id==0 || optionalFuncion.isEmpty() ) {return "redirect:/operador/";}
        Funcion funcion = optionalFuncion.get();
        funcion.setEstado("Cancelada");
        funcionRepository.save(funcion);

        //PENDIENTE
        //Al momento de cancelar una función se deben cancelar las compras asociadas

        attr.addFlashAttribute("msg", "La función ha sido cancelada");
        return "redirect:/operador/obras/gestion/" + funcion.getObra().getId();
    }
}
