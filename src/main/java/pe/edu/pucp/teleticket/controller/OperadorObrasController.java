package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/operador/obras")
public class OperadorObrasController {

    private final int obrasPaginas = 8;
    private final int funcionesPaginas = 5;

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
    GeneroRepository generoRepository;
    @Autowired
    ObraRepository obraRepository;
    @Autowired
    FuncionRepository funcionRepository;
    @Autowired
    FotoObraRepository fotoObraRepository;

    @GetMapping({"/","","/lista"})
    public String listarObras(Model model, @RequestParam("pag") Optional<Integer> pag, @RequestParam("busqueda") Optional<String> optionalBusqueda){
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta =  busqueda.isBlank()? "/operador/obras?" : "operador/obras?busqueda=" +busqueda +"&";

        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas = (int) Math.ceil((float)obraRepository.contarListaObrasBusqueda(busqueda)/obrasPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista = PageRequest.of(pagina-1, obrasPaginas);
        List<Obra> listaObras = obraRepository.listarObrasBusqueda(busqueda, lista);

        model.addAttribute("listaObras",listaObras);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);
        return "operador/obras/lista";
    }

    @GetMapping("/gestion/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("pag") Optional<Integer> pag){
        int id=0;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/operador/obras";
        }
        Optional<Obra> optionalObra =obraRepository.findById(id);
        if(optionalObra.isEmpty()){return "redirect:/operador/obras";}

        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        Obra obra = new Obra();
        obra.setId(id);
        int paginas = (int) Math.ceil((float)funcionRepository.countAllByObra(obra)/funcionesPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista;
        if(pagina==0){
            lista= PageRequest.of(0, funcionesPaginas);
        } else {
            lista= PageRequest.of(pagina-1, funcionesPaginas);
        }

        List<Funcion> funcionList = funcionRepository.findAllByObraOrderByFechaAsc(obra,lista);

        model.addAttribute("fotos", fotoObraRepository.findAllIdByIdObras(id));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("obra",optionalObra.get());
        model.addAttribute("funciones", funcionList);
        model.addAttribute("ruta", "/operador/obras/gestion/"+id+"?");
        return "/operador/obras/gestionobra";
    }

    @GetMapping("/nueva")
    public String nuevaObra(Model model, @ModelAttribute("obra") Obra obra) {
        List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
        List<Genero> generoList = generoRepository.findAll();
        model.addAttribute("personaList", personaList);
        model.addAttribute("generoList", generoList);
        return "/operador/obras/form";
    }

    @GetMapping("/gestion/{idPath}/editar")
    public String editarObra(Model model, @ModelAttribute("obra") Obra obra,@PathVariable("idPath") String idObraString) {
        int idObra=0;
        Optional<Obra> optionalObra;
        try{
            idObra=Integer.parseInt(idObraString);
            optionalObra = obraRepository.findById(idObra);
        } catch (Exception e){
            e.printStackTrace();
            return "redirect:/operador/obras";
        }
        if(optionalObra.isEmpty()){return "redirect:/operador/obras";}
        model.addAttribute("obra", optionalObra.get());
        List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
        List<Genero> generoList = generoRepository.findAll();
        model.addAttribute("personaList", personaList);
        model.addAttribute("generoList", generoList);
        return "/operador/obras/form";
    }

    @PostMapping(value = "/guardar")
    public String guardarObra(Model model, @ModelAttribute("obra") @Valid Obra obra, BindingResult bindingResult,
                              @RequestParam("foto") MultipartFile foto) {


        if (foto.isEmpty()) {
            List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
            List<Genero> generoList = generoRepository.findAll();
            model.addAttribute("personaList", personaList);
            model.addAttribute("generoList", generoList);
            model.addAttribute("msg", "Debe subir un archivo");

            return "/operador/obras/form";
        }
        if (!verificarFoto(foto)) {
            List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
            List<Genero> generoList = generoRepository.findAll();
            model.addAttribute("personaList", personaList);
            model.addAttribute("generoList", generoList);
            model.addAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            return "/operador/obras/form";
        }
        String fotoNombre = foto.getOriginalFilename();
        if (fotoNombre.contains("..")) {
            List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
            List<Genero> generoList = generoRepository.findAll();
            model.addAttribute("personaList", personaList);
            model.addAttribute("generoList", generoList);
            model.addAttribute("msg", "No se permiten '..' en el archivo");
            return "/operador/obras/form";
        }
        if(bindingResult.hasErrors()){
            List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
            List<Genero> generoList = generoRepository.findAll();
            model.addAttribute("personaList", personaList);
            model.addAttribute("generoList", generoList);
            return "/operador/obras/form";
        }

        Fotosobra fotoObra = new Fotosobra();
        Obra obraCreada =obraRepository.save(obra);
        try {
            fotoObra.setFoto(foto.getBytes());
            fotoObra.setIdobras(obraCreada.getId());
            fotoObraRepository.save(fotoObra);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Ocurrió un error al subir el archivo");
            fotoObraRepository.deleteById(obraCreada.getId());
            return "/operador/obras/form";
        }
        obraCreada.setFotoprincipal(fotoObra.getId());
        obraRepository.save(obraCreada);

        return "redirect:/operador/obras/gestion/"+obraCreada.getId();
    }

    @PostMapping("/actualizar")
    public String actualizarObra(Model model, @ModelAttribute("obra") @Valid Obra obra, BindingResult bindingResult) {
        Optional<Obra> optionalObra = obraRepository.findById(obra.getId());
        if(optionalObra.isEmpty()){return "redirect:/operador/obras";}

        if(bindingResult.hasErrors()){
            List<Persona> personaList = personaRepository.findAllByEstadoEquals("Disponible");
            List<Genero> generoList = generoRepository.findAll();
            model.addAttribute("personaList", personaList);
            model.addAttribute("generoList", generoList);
            return "/operador/obras/form";
        }
        obraRepository.save(obra);
        return "redirect:/operador/obras/gestion/"+obra.getId();
    }

    @PostMapping("/gestion/{idObraString}/imagenprincipal")
    public String imagenPrincipal(@PathVariable("idObraString") String idObraString, @RequestParam("fotoid") String fotoidString, RedirectAttributes redirectAttributes) {
        int idObra;
        int idFoto;
        try{
            idObra= Integer.parseInt(idObraString);
            idFoto= Integer.parseInt(fotoidString);
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:/operador/obras";
        }
        Optional<Fotosobra> optionalFotosobra = fotoObraRepository.findById(idFoto);
        Optional<Obra> optionalObra = obraRepository.findById(idObra);
        if(optionalObra.isEmpty() || optionalFotosobra.isEmpty() ||
                (optionalFotosobra.get().getIdobras() != optionalObra.get().getId())){
            return "redirect:/operador/obras";
        }
        Obra obra = optionalObra.get();
        obra.setFotoprincipal(optionalFotosobra.get().getId());
        obraRepository.save(obra);
        redirectAttributes.addFlashAttribute("msg", "Se cambio la imagen principal exitosamente");
        return "redirect:/operador/obras/gestion/"+idObra;
    }
    @PostMapping("/gestion/imagenborrar")
    public String borrarImagen(@RequestParam("fotoid") String fotoidString, RedirectAttributes redirectAttributes) {
        int idFoto;
        try{
            idFoto = Integer.parseInt(fotoidString);
        } catch (Exception e){
            e.printStackTrace();
            return "redirect:/operador/obras/";
        }
        Optional<Fotosobra> optionalFotosobra = fotoObraRepository.findById(idFoto);
        if(optionalFotosobra.isEmpty()){
            return "redirect:/operador/obras/";
        }
        Fotosobra foto =  optionalFotosobra.get();
        Optional<Obra> optionalObra = obraRepository.findById(foto.getIdobras());
        if(optionalObra.isEmpty() || optionalObra.get().getFotoprincipal()==idFoto){
            return "redirect:/operador/obras/";
        }
        fotoObraRepository.deleteById(idFoto);
        redirectAttributes.addFlashAttribute("msg", "La imagen se ha borrado exitosamente");
        return "redirect:/operador/obras/gestion/"+ foto.getIdobras();
    }

    @PostMapping("/gestion/{idObraString}/agregarimagen")
    public String agregarImagen(@PathVariable("idObraString") String idObraString, @RequestParam("foto") MultipartFile foto, RedirectAttributes redirectAttributes) {
         int idObra;
         try{
             idObra= Integer.parseInt(idObraString);
         }catch (Exception e){
             e.printStackTrace();
             return "redirect:/operador/obras";
         }
         Optional<Obra> optionalObra = obraRepository.findById(idObra);
         if(optionalObra.isEmpty()){
             return "redirect:/operador/obras";
         }
         int cantidad = fotoObraRepository.findAllIdByIdObras(idObra).size();
        if(cantidad>3){
            return "redirect:/operador/obras";
        }
        if (foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "Debe subir un archivo");
            return "redirect:/operador/Obras/gestion/" + idObra;
        }
        if (!verificarFoto(foto)) {
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            return "redirect:/operador/obras/gestion/" + idObra;
        }
        String fotoNombre = foto.getOriginalFilename();

        if (fotoNombre.contains("..")) {
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "No se permiten '..' en el archivo");
            return "redirect:/operador/obras/gestion/" + idObra;
        }

        Fotosobra fotoobra = new Fotosobra();

        try {
            fotoobra.setFoto(foto.getBytes());
            fotoobra.setIdobras(idObra);
        } catch (IOException e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "Hubo un error al cargar el archivo");
            return "redirect:/operador/obras/gestion/" + idObra;
        }
        redirectAttributes.addFlashAttribute("msg","Se ha guardado la imagen exitosamente");
        fotoObraRepository.save(fotoobra);
        return "redirect:/operador/obras/gestion/" + idObra;


    }
}
