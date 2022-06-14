package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;
import pe.edu.pucp.teleticket.service.EmailService;

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

    @Autowired
    HistorialRepository historialRepository;

    @Autowired
    EmailService emailService;

    @GetMapping("/nueva")
    public String nuevaFuncion(@RequestParam("idobra") int idobra, Model model, @ModelAttribute("funcion") Funcion funcion) {
        Optional<Obra> optionalObra = obraRepository.findById(idobra);
        if(optionalObra.isPresent()){
            List<Sede> sedeList = sedeRepository.findSedesDisponibles();
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
    public String registarFuncion(@ModelAttribute("funcion") @Valid Funcion funcion, BindingResult bindingResult,
                                   RedirectAttributes attr, Model model){
        int idobra = funcion.getObra().getId();
        Optional<Obra> optionalObra = obraRepository.findById(idobra);

        if(idobra==0 || optionalObra.isEmpty() ) {
            return "redirect:/operador/";
        }

        List<Sede> sedeList = sedeRepository.findSedesDisponibles();

        boolean horaHasErrors = false;
        boolean fechaHasErrors = false;
        boolean salaHasErrors = false;

        if(!bindingResult.hasFieldErrors("inicio")){
            LocalTime minTime = LocalTime.parse( "11:00:00" );
            LocalTime maxTime = LocalTime.parse( "23:00:00" );
            if(funcion.getInicio().isAfter(maxTime) || funcion.getInicio().isBefore(minTime)){
                FieldError fHoraerror = new FieldError("inicio", "inicio", "La hora de inicio debe estar entre 11:00 y 23:00");
                bindingResult.addError(fHoraerror);
                horaHasErrors = true;
            }
        }

        if(!bindingResult.hasFieldErrors("fecha")){
            LocalDate minDate = LocalDate.now().plusDays(1);
            LocalDate maxDate = LocalDate.now().plusMonths(4);
            if(funcion.getFecha().isAfter(maxDate) || funcion.getFecha().isBefore(minDate)){
                FieldError fFechaerror = new FieldError("fecha", "fecha", "La fecha debe estar entre " +minDate+" y "+maxDate);
                bindingResult.addError(fFechaerror);
                fechaHasErrors=true;
            }
        }

        if(!bindingResult.hasFieldErrors("sala")){
            Optional<Sala> optionalSala = salaRepository.findById(funcion.getSala().getId());
            if(optionalSala.isEmpty() || optionalSala.get().getEstado().equals("No disponible") || !optionalSala.get().getSede().getEstado().equals("Disponible")){
                FieldError fSalaError = new FieldError("sala", "sala", "Debe eligir una sala válida");
                bindingResult.addError(fSalaError);
                salaHasErrors = true;
            }else{
                if(funcion.getStock() > optionalSala.get().getAforo()){
                    FieldError fAforoError = new FieldError("stock", "stock", "El aforo de la función no puede ser mayor que el aforo de la sala");
                    bindingResult.addError(fAforoError);
                    salaHasErrors = true;
                }
            }
        }

        if(bindingResult.hasErrors() || horaHasErrors || fechaHasErrors || salaHasErrors){
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

        boolean horaHasErrors = false;
        boolean fechaHasErrors = false;

        if(!bindingResult.hasFieldErrors("inicio")){
            LocalTime minTime = LocalTime.parse( "11:00:00" );
            LocalTime maxTime = LocalTime.parse( "23:00:00" );
            if(funcion.getInicio().isAfter(maxTime) || funcion.getInicio().isBefore(minTime)){
                FieldError fHoraerror = new FieldError("inicio", "inicio", "La hora de inicio debe estar entre 11:00 y 23:00");
                bindingResult.addError(fHoraerror);
                horaHasErrors = true;
            }
        }

        if(!bindingResult.hasFieldErrors("fecha")){
            LocalDate minDate = LocalDate.now().plusDays(1);
            LocalDate maxDate = LocalDate.now().plusMonths(4);
            if(funcion.getFecha().isAfter(maxDate) || funcion.getFecha().isBefore(minDate)){
                FieldError fFechaerror = new FieldError("fecha", "fecha", "La fecha debe estar entre " +minDate+" y "+maxDate);
                bindingResult.addError(fFechaerror);
                fechaHasErrors=true;
            }
        }

        if(bindingResult.hasFieldErrors("fecha") || bindingResult.hasFieldErrors("maxreservas") ||
                bindingResult.hasFieldErrors("inicio") || bindingResult.hasFieldErrors("costo")
                || horaHasErrors || fechaHasErrors){
            model.addAttribute("funcion", funcion);
            return "operador/funciones/editaFrm";
        }else{
                LocalTime fin = funcion.getInicio().plusMinutes(funcion.getObra().getDuracion());
                funcion.setFin(fin);
                List<Funcion> funcionesConflictoList = funcionRepository.findFuncionesEnConflictoId(funcion.getFecha(),funcion.getInicio(),fin,funcion.getObra().getId(), funcion.getSala().getId(), funcion.getId());
                if(funcionesConflictoList.size()>0){
                    model.addAttribute("funcionesConflictoList", funcionesConflictoList);
                    model.addAttribute("funcion", funcion);
                    return "operador/funciones/editaFrm";
                }else{
                    funcionRepository.save(funcion);
                    attr.addFlashAttribute("msg", "Se ha modificado la función con éxito");
                    return "redirect:/operador/obras/gestion/" + funcion.getObra().getId();
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
        historialRepository.cancelarFuncion(id);

        for(Historial h : historialRepository.findCanceladasByIdfunciones(id)){
            try{
                emailService.correoFuncionCancelada(h);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //PENDIENTE
        //Al momento de cancelar una función se debe realizar la devolucion del dinero

        attr.addFlashAttribute("msg", "La función ha sido cancelada");
        return "redirect:/operador/obras/gestion/" + funcion.getObra().getId();
    }
}
