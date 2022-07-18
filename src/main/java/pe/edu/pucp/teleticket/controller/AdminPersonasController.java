package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.entity.Fotossede;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Persona;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.FuncionRepository;
import pe.edu.pucp.teleticket.repository.PersonaRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/admin/personas")
public class AdminPersonasController {


    private final Integer personasPaginas=8;

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
    FuncionRepository funcionRepository;

    @GetMapping({"/","","/lista"})
    public String listarPersonas(@RequestParam("busqueda") Optional<String> optionalBusqueda, Model model,
                                 @RequestParam("pag") Optional<String> pag){

        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta = busqueda.isBlank()? "/admin/personas?" : "/admin/personas?busqueda=" +busqueda +"&";
        int pagina=0;
        try{
            pagina = pag.isEmpty() ? 1 : Integer.parseInt(pag.get());
        } catch (Exception e){
            return "redirect:/admin/personas";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) personaRepository.countPersonasListado(busqueda) / personasPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista;
        if (pagina == 0) {
            lista = PageRequest.of(0, personasPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, personasPaginas);

        }
        List<PersonasListado> personas = personaRepository.findPersonasListado(busqueda, lista);
        model.addAttribute("personas", personas);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);
        return "admin/personas/lista";
    }

    @GetMapping("/borrar")
    public String borrarPersona(@RequestParam("id") Optional<String> optionalid, RedirectAttributes redirectAttributes){
        Integer id=0;
        try{
            id= Integer.parseInt(optionalid.get());
        }catch (Exception e){
            return "redirect:/admin/personas";
        }

        Optional<Integer> optionalParticipaciones = personaRepository.partipacionesPersona(id);

        if(optionalParticipaciones.isEmpty()){
            redirectAttributes.addFlashAttribute("msg", "No se ha encontrado la persona ha borrar");
            return "redirect:/admin/personas";
        }
        if(optionalParticipaciones.get()!=0){
            redirectAttributes.addFlashAttribute("msg", "No se ha podido borrar la persona debido a que tiene relación con obras");
            return "redirect:/admin/personas";
        }

        personaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("msg", "Se ha borrado con éxito la persona");

        return "redirect:/admin/personas";
    }

    @GetMapping("/nuevo")
    public String nuevaPersona(@ModelAttribute("persona") Persona persona){
        return "admin/personas/form";
    }

    @GetMapping("/editar")
    public String editarPersona(@RequestParam("id") Optional<String> optionalId, @ModelAttribute("persona") Persona persona, Model model){
        Integer id=0;
        try{
            id= Integer.parseInt(optionalId.get());
        } catch (Exception e){
            return "redirect:/admin/personas";
        }
        Optional<Persona> optionalPersona = personaRepository.buscarPersona(id);
        if(optionalPersona.isEmpty()){
            return "redirect:/admin/personas";
        }
        model.addAttribute("persona", optionalPersona.get());
        return "admin/personas/form";
    }

    @PostMapping("/guardar")
    public String guardarPersona(@ModelAttribute("persona") @Valid Persona persona,
                                 BindingResult bindingResult, @RequestParam("archivo") MultipartFile archivo,
                                 Model model, RedirectAttributes redirectAttributes){
        if (archivo.isEmpty()) {
            model.addAttribute("error", 1);
            model.addAttribute("msg", "Debe subir un archivo");
            return "admin/personas/form";
        }
        if (!verificarFoto(archivo)) {
            model.addAttribute("error", 1);
            model.addAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            return "admin/personas/form";
        }
        String fotoNombre = archivo.getOriginalFilename();
        if (fotoNombre.contains("..")) {
            model.addAttribute("error", 1);
            model.addAttribute("msg", "No se permiten '..' en el archivo");
            return "admin/personas/form";
        }
        if (bindingResult.hasErrors()) {
            return "admin/personas/form";
        }
        try {
            persona.setFoto(archivo.getBytes());
            personaRepository.save(persona);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", 1);
            model.addAttribute("msg", "Ocurrió un error al guardar la persona");
            return "admin/personas/form";
        }

        redirectAttributes.addFlashAttribute("msg","Se añadió la persona exitosamente");
        return "redirect:/admin/personas";
    }

    @PostMapping("/actualizar")
    public String actualizarPersona(@ModelAttribute("persona") @Valid Persona persona,BindingResult bindingResult ,RedirectAttributes redirectAttributes, Model model){
        Optional<Persona> optionalPersona = personaRepository.buscarPersona(persona.getId());
        if(optionalPersona.isEmpty()){
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg","Ocurrio un error al borrar a la persona");
            return "redirect:/admin/personas";
        }
        if(bindingResult.hasErrors()){
            return "admin/personas/form";
        }

        List<Funcion> funcionesVigentes = funcionRepository.listarVigentesPorIdpersona(persona.getId());
        if(funcionesVigentes.size()>0 && persona.getEstado().equals("No disponible")){
            model.addAttribute("funcionesVigentes", funcionesVigentes);
            return "admin/personas/form";
        }

        personaRepository.actualizarPersona(persona.getId(),persona.getNombre(),persona.getEstado());
        redirectAttributes.addFlashAttribute("msg","Se actualizó la persona exitosamente");
        return  "redirect:/admin/personas";
    }

    @PostMapping("/cambiarfoto")
    public String cambiarFotoPersona(@RequestParam("imagen") MultipartFile archivo, @RequestParam("id") String idString,
                                     RedirectAttributes redirectAttributes){
        Integer id=0;
        try{
            id=Integer.parseInt(idString);
        } catch (Exception e){
            e.printStackTrace();
            return "redirect:/admin/personas";
        }
        Optional<Persona> optionalPersona = personaRepository.buscarPersona(id);
        if(optionalPersona.isEmpty()){
            return "redirect:/admin/personas";
        }
        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "Debe subir un archivo");
            redirectAttributes.addFlashAttribute("error",1);
            return "redirect:/admin/personas/editar?id="+id;
        }
        if (!verificarFoto(archivo)) {
            redirectAttributes.addFlashAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            redirectAttributes.addFlashAttribute("error",1);
            return "redirect:/admin/personas/editar?id="+id;
        }
        String fotoNombre = archivo.getOriginalFilename();
        if (fotoNombre.contains("..")) {
            redirectAttributes.addFlashAttribute("msg", "No se permiten '..' en el archivo");
            redirectAttributes.addFlashAttribute("error",1);
            return "redirect:/admin/personas/editar?id="+id;
        }

        Persona persona = optionalPersona.get();
        try {
            persona.setFoto(archivo.getBytes());
            personaRepository.save(persona);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("msg", "Ocurrió un error al guardar la imagen");
            redirectAttributes.addFlashAttribute("error",1);
            return "redirect:/admin/personas/editar?id="+id;
        }
        redirectAttributes.addFlashAttribute("msg","Se cambio la imagen exitosamente");
        return "redirect:/admin/personas/editar?id="+id;
    }


}
