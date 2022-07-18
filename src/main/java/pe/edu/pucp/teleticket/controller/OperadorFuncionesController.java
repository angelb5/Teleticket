package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.dto.*;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;
import pe.edu.pucp.teleticket.service.EmailService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operador/funciones")
public class OperadorFuncionesController {

    @Value("${aplication.domain}")
    private String DOMINIO;

    private final int funcionesPaginas =6;

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
    PersonaRepository personaRepository;

    @Autowired
    EmailService emailService;

    @GetMapping({"/", "", "/lista"})
    public String listarFunciones(Model model, @RequestParam("pag") Optional<Integer> pag,
                                  @RequestParam("obra") Optional<Integer> idobra, @RequestParam("mes") Optional<String> optMes,
                                  @RequestParam("sede") Optional<Integer> idsede) {
        String mes = optMes.orElse("");
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas =0;
        Pageable lista;
        String ruta = "/operador/funciones?mes="+mes+"&";

        List<SedeFiltro> listaSedes= sedeRepository.listarSedesConFunciones();
        List<ObraFiltro> listaObras = obraRepository.listarObrasConFunciones();
        List<String> meses = funcionRepository.listarMesesConFunciones();
        List<FuncionOperadorDto> listaFunciones = new ArrayList<>();

        if(idobra.isEmpty() && idsede.isEmpty()){
            paginas = (int) Math.ceil((float) funcionRepository.contarFuncionesPorMes(mes) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            listaFunciones = funcionRepository.listarFuncionesPorMes(mes,lista);
        }else if(idobra.isPresent() && idsede.isEmpty()){
            paginas = (int) Math.ceil((float) funcionRepository.contarFuncionesPorMesAndIdobra(mes, idobra.get()) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            listaFunciones = funcionRepository.listarFuncionesPorMesAndIdobra(mes,idobra.get(),lista);
            ruta+="obra="+idobra.get()+"&";
        }else if(idobra.isEmpty()){
            paginas = (int) Math.ceil((float) funcionRepository.contarFuncionesPorMesAndIdsede(mes, idsede.get()) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            listaFunciones = funcionRepository.listarFuncionesPorMesAndIdsede(mes,idsede.get(),lista);
            ruta+="sede="+idsede.get()+"&";
        }else {
            paginas = (int) Math.ceil((float) funcionRepository.contarFuncionesPorMesAndIdobraAndIdsede(mes, idobra.get(), idsede.get()) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            listaFunciones = funcionRepository.listarFuncionesPorMesAndIdObraAndIdsede(mes,idobra.get(),idsede.get(),lista);
            ruta+="obra="+idobra.get()+"&sede="+idsede.get()+"&";
        }

        model.addAttribute("idobra", idobra);
        model.addAttribute("idsede", idsede);
        model.addAttribute("mes", mes);

        model.addAttribute("listaSedes", listaSedes);
        model.addAttribute("listaObras", listaObras);
        model.addAttribute("meses", meses);
        model.addAttribute("listaFunciones", listaFunciones);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);

        return "operador/funciones/lista";
    }

    @GetMapping("/nueva")
    public String nuevaFuncion(@RequestParam("idobra") int idobra, Model model, @ModelAttribute("funcion") Funcion funcion) {
        Optional<Obra> optionalObra = obraRepository.findById(idobra);
        if(optionalObra.isEmpty() || !personaRepository.listarPersonalNoDisponiblePorIdobra(idobra).isEmpty()){
            return "redirect:/operador/";
        }
        List<Sede> sedeList = sedeRepository.findSedesDisponibles();
        funcion.setObra(optionalObra.get());
        model.addAttribute("sedeList", sedeList);
        model.addAttribute("dominio", DOMINIO);
        return "operador/funciones/nuevaFrm";
    }

    @GetMapping("/editar")
    public String editarFuncion(@RequestParam("idfuncion") int idfuncion, Model model,
                               @ModelAttribute("funcion") Funcion funcion) {
        Optional<Funcion> optionalFuncion = funcionRepository.findById(idfuncion);
        if(optionalFuncion.isEmpty()){return "redirect:/operador/";}
        funcion = optionalFuncion.get();
        //if(funcion.getEstado().equals("Cancelada") || funcion.getEstadoRVC().equals("Realizada")){return "redirect:/operador/";}
        model.addAttribute("funcion", funcion);
        return "operador/funciones/editaFrm";
    }

    @PostMapping("/registrar")
    public String registarFuncion(@ModelAttribute("funcion") @Valid Funcion funcion, BindingResult bindingResult,
                                   RedirectAttributes attr, Model model){
        int idobra = funcion.getObra().getId();
        Optional<Obra> optionalObra = obraRepository.findById(idobra);

        if(idobra==0 || optionalObra.isEmpty() || !personaRepository.listarPersonalNoDisponiblePorIdobra(idobra).isEmpty()) {
            return "redirect:/operador/";
        }

        obraRepository.actualizarDestacadas();
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
        obraRepository.actualizarDestacadas();
        //PENDIENTE
        //Al momento de cancelar una función se debe realizar la devolucion del dinero

        attr.addFlashAttribute("msg", "La función ha sido cancelada");
        return "redirect:/operador/obras/gestion/" + funcion.getObra().getId();
    }

    @ResponseBody
    @GetMapping("/sala/{id}")
    public ResponseEntity<HashMap<String, Object>> aforoSalaPorId(@PathVariable("id") String idStr) {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            int id = Integer.parseInt(idStr);
            Optional<Sala> optionalSala = salaRepository.findById(id);
            if (optionalSala.isPresent() && optionalSala.get().getEstado().equals("Disponible")) {
                responseJson.put("result", "success");
                responseJson.put("aforo", optionalSala.get().getAforo());
                return ResponseEntity.ok(responseJson);
            } else {
                responseJson.put("msg", "Sala no encontrada");
            }
        } catch (NumberFormatException e) {
            responseJson.put("msg", "El ID debe ser un número entero positivo");
        }
        responseJson.put("result", "failure");
        return ResponseEntity.badRequest().body(responseJson);
    }
}
