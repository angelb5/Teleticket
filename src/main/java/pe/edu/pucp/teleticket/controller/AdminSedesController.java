package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.Fotossede;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Sala;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.FotoSedeRepository;
import pe.edu.pucp.teleticket.repository.FuncionRepository;
import pe.edu.pucp.teleticket.repository.SalaRepository;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/admin/sedes")
public class AdminSedesController {

    private final int sedesPaginas = 12;
    private final int salasPaginas = 5;
    private final List<String> formatos= Arrays.asList("media/png","media/jpeg", "image/jpeg", "image/png");

    private boolean verificarFoto(MultipartFile file){
        if(formatos.contains(file.getContentType().toLowerCase(Locale.ROOT))){
            return true;
        }
        return false;
    }

    @Autowired
    FuncionRepository funcionRepository;
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    SalaRepository salaRepository;
    @Autowired
    FotoSedeRepository fotoSedeRepository;

    @GetMapping({"/", "", "/lista"})
    public String listarSedes(Model model, @RequestParam("pag") Optional<String> pag,
                              @RequestParam("busqueda") Optional<String> optionalBusqueda) {

        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta = busqueda.isBlank()? "/admin/sedes?" : "/admin/sedes?busqueda=" +busqueda +"&";
        int pagina=0;
        try{
            pagina = pag.isEmpty() ? 1 : Integer.parseInt(pag.get());
        } catch (Exception e){
            return "redirect:/admin/sedes";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) sedeRepository.contarListarSedesBusqueda(busqueda) / sedesPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista;
        if (pagina == 0) {
            lista = PageRequest.of(0, sedesPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, sedesPaginas);

        }
        List<Sede> listaSedes = sedeRepository.listarSedesBusqueda(busqueda, lista);
        model.addAttribute("listaSedes", listaSedes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);
        return "admin/sedes/lista";
    }

    @GetMapping("/nueva")
    public String crearSede(@ModelAttribute("sede") Sede sede) {
        return "admin/sedes/form";
    }

    @GetMapping("/gestion/{idPath}/editar")
    public String editarSede(@ModelAttribute("sede") Sede sede, @PathVariable("idPath") String idSedeString, Model model) {
        Integer idSede;
        try {
            idSede = Integer.parseInt(idSedeString);
        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        if (idSede < 1) {
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findById(idSede);
        if (optionalSede.isEmpty()) {
            return "redirect:/admin/sedes";
        }
        model.addAttribute("sede", optionalSede.get());
        return "admin/sedes/form";
    }

    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute("sede") @Valid Sede sede, BindingResult bindingResult, @RequestParam("foto") MultipartFile foto, Model model) {
        if (foto.isEmpty()) {
            model.addAttribute("msg", "Debe subir un archivo");
            return "admin/sedes/form";
        }
        if (!verificarFoto(foto)) {
            model.addAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            return "admin/sedes/form";
        }
        String fotoNombre = foto.getOriginalFilename();
        if (fotoNombre.contains("..")) {
            model.addAttribute("msg", "No se permiten '..' en el archivo");
            return "admin/sedes/form";
        }
        if (bindingResult.hasErrors()) {
            return "admin/sedes/form";
        }
        Fotossede fotosede = new Fotossede();
        sede.setEstado("Disponible");
        Sede sedeCreada = sedeRepository.save(sede);
        try {
            fotosede.setFoto(foto.getBytes());
            fotosede.setIdsedes(sedeCreada.getId());
            fotoSedeRepository.save(fotosede);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Ocurri?? un error al subir el archivo");
            sedeRepository.deleteById(sedeCreada.getId());
            return "admin/sedes/form";
        }
        sedeCreada.setFotoprincipal(fotosede.getId());
        sedeRepository.save(sedeCreada);

        return "redirect:/admin/sedes/gestion/" + sede.getId();
    }

    @PostMapping("/actualizar")
    public String actualizarSede(@ModelAttribute("sede") @Valid Sede sede, BindingResult bindingResult, Model model) {
        Optional<Sede> optionalSede = sedeRepository.findById(sede.getId());
        if(optionalSede.isEmpty()){return "redirect:/admin/sedes";}
        if (bindingResult.hasErrors()) {
            return "admin/sedes/form";
        }else{
            List<Funcion> funcionesVigentes = funcionRepository.getVigentesByIdsedes(sede.getId());
            if(funcionesVigentes.size()>0 && !sede.getEstado().equals("Disponible")){
                model.addAttribute("funcionesVigentes",funcionesVigentes);
                return "admin/sedes/form";
            }
        }
        sedeRepository.save(sede);
        return "redirect:/admin/sedes/gestion/" + sede.getId();
    }


    @GetMapping("/gestion/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("pag") Optional<Integer> pag) {
        int id = 0;
        try {
            id = Integer.parseInt(idPath);
        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findById(id);
        if (optionalSede.isEmpty()) {
            return "redirect:/admin/sedes";
        }

        int pagina = pag.isEmpty() ? 1 : pag.get();
        pagina = pagina < 1 ? 1 : pagina;
        Sede sede = new Sede();
        sede.setId(id);
        int paginas = (int) Math.ceil((float) salaRepository.countAllBySede(sede) / salasPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista;
        if (pagina == 0) {
            lista = PageRequest.of(0, salasPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, salasPaginas);

        }

        model.addAttribute("fotos", fotoSedeRepository.findAllIdByIdSedes(id));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("sede", optionalSede.get());
        model.addAttribute("salas", salaRepository.findAllBySede(sede, lista));
        model.addAttribute("ruta", "/admin/sedes/gestion/" + sede.getId() + "?");
        return "admin/sedes/gestionsede";
    }

    @GetMapping("/gestion/{idPath}/nuevasala")
    public String nuevaSala(@PathVariable("idPath") String idSedeString, @ModelAttribute("sala") Sala sala) {
        int idSede = 0;
        try {
            idSede = Integer.parseInt(idSedeString);
        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findById(idSede);
        if (optionalSede.isEmpty() || !optionalSede.get().getEstado().equals("Disponible")) {
            return "redirect:/admin/sedes";
        }
        sala.setSede(new Sede());
        sala.getSede().setId(idSede);
        return "admin/sedes/salas/form";
    }

    @GetMapping("/gestion/editarsala")
    public String editarSala(@RequestParam("id") String idSalaString, @ModelAttribute("sala") Sala sala, Model model) {
        int idSala = 0;
        try {
            idSala = Integer.parseInt(idSalaString);
        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }

        Optional<Sala> optionalSala = salaRepository.findById(idSala);
        if (optionalSala.isEmpty() || !optionalSala.get().getSede().getEstado().equals("Disponible")) {
            return "redirect:/admin/sedes";
        }
        model.addAttribute("sala", optionalSala.get());
        model.addAttribute("idSede", optionalSala.get().getSede().getId());
        return "admin/sedes/salas/form";
    }

    @PostMapping("/gestion/sala/guardar")
    public String guardarSala(@ModelAttribute("sala") @Valid Sala sala, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        Optional<Sede> optionalSede = sedeRepository.findById(sala.getSede().getId());
        if (optionalSede.isEmpty() || !optionalSede.get().getEstado().equals("Disponible")) {
            return "redirect:/admin/sedes";
        }

        if (bindingResult.hasErrors()) {
            return "admin/sedes/salas/form";
        }

        String msg;
        if (sala.getId() == 0) {
            msg = "Sala creada exitosamente";
        } else {
            Optional<Sala> optionalSala = salaRepository.findById(sala.getId());
            if(optionalSala.isEmpty()){return "redirect:/admin/sedes";}
            List<Funcion> funcionesVigentes = funcionRepository.getVigentesByIdsalas(sala.getId());
            if (funcionesVigentes.size()>0){
                if (sala.getEstado().equals("No disponible")){
                    model.addAttribute("funcionesVigentes",funcionesVigentes);
                    return "admin/sedes/salas/form";
                }else{
                    long maxStockByIdsala = funcionRepository.getMaxStockByIdsala(sala.getId());
                    if(maxStockByIdsala>sala.getAforo()){
                        FieldError fAforoError = new FieldError("aforo", "aforo", "El aforo de la sala no puede ser menor que "+ maxStockByIdsala + " ya que hay una funci??n asociada");
                        bindingResult.addError(fAforoError);
                        return "admin/sedes/salas/form";
                    }
                }
            }
            msg = "Sala editada exitosamente";
        }
        redirectAttributes.addFlashAttribute("msg", msg);
        salaRepository.save(sala);
        return "redirect:/admin/sedes/gestion/" + sala.getSede().getId();
    }

    @PostMapping("/gestion/{idSedeString}/eliminar")
    public String eliminarSede(@PathVariable("idSedeString") String idSedeString, RedirectAttributes redirectAttributes){
        int sedeId;
        try {
            sedeId = Integer.parseInt(idSedeString);

        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findById(sedeId);
        if (optionalSede.isEmpty()) {
            return "redirect:/admin/sedes";
        }

        List<Sala> listaSalas = salaRepository.findAllBySede(optionalSede.get());
        if(listaSalas.isEmpty()){
            fotoSedeRepository.deleteAllById(fotoSedeRepository.findAllIdByIdSedes(sedeId));
            sedeRepository.deleteById(sedeId);
            redirectAttributes.addFlashAttribute("msg","Se ha eliminado la sede exitosamente");
            return "redirect:/admin/sedes";
        } else{
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }

    }


    @PostMapping("/gestion/{idSedeString}/imagenprincipal")
    public String imagenPrincipal(@PathVariable("idSedeString") String idSedeString, @RequestParam("fotoid") String fotoidString, RedirectAttributes redirectAttributes) {
        int sedeId;
        int fotoid;
        try {
            sedeId = Integer.parseInt(idSedeString);
            fotoid = Integer.parseInt(fotoidString);

        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findById(sedeId);
        Optional<Fotossede> optionalFotossede = fotoSedeRepository.findById(fotoid);
        if (optionalFotossede.isEmpty() || optionalSede.isEmpty() || (optionalFotossede.get().getIdsedes() != sedeId)) {
            return "redirect:/admin/sedes";
        }

        Sede sede = optionalSede.get();
        sede.setFotoprincipal(fotoid);
        sedeRepository.save(sede);
        redirectAttributes.addFlashAttribute("msg", "Se ha cambiado la imagen principal exitosamente");
        return "redirect:/admin/sedes/gestion/" + sedeId;
    }

    @PostMapping("/gestion/imagenborrar")
    public String borrarImagen(@RequestParam("fotoid") String fotoidString, RedirectAttributes redirectAttributes) {
        int fotoid;
        try {
            fotoid = Integer.parseInt(fotoidString);
        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        Optional<Fotossede> optionalFotossede = fotoSedeRepository.findById(fotoid);
        if (optionalFotossede.isEmpty()) {
            return "redirect:/admin/sedes";
        }
        Fotossede fotossede = optionalFotossede.get();
        Optional<Sede> optionalSede = sedeRepository.findById(fotossede.getIdsedes());
        if (optionalSede.isEmpty() || optionalSede.get().getFotoprincipal()==null||(optionalSede.get().getFotoprincipal() == fotoid)) {
            return "redirect:/admin/sedes";
        }

        fotoSedeRepository.deleteById(fotoid);
        redirectAttributes.addFlashAttribute("msg","Se ha borrado la imagen exitosamente");
        return "redirect:/admin/sedes/gestion/" + optionalFotossede.get().getIdsedes();
    }

    @PostMapping("/gestion/{idSedeString}/agregarimagen")
    public String agregarImagen(@PathVariable("idSedeString") String idSedeString, @RequestParam("foto") MultipartFile foto, RedirectAttributes redirectAttributes) {
        int sedeId;
        try {
            sedeId = Integer.parseInt(idSedeString);
        } catch (Exception e) {
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findById(sedeId);
        if (optionalSede.isEmpty()) {
            return "redirect:/admin/sedes";
        }
        long cantidad= fotoSedeRepository.findAllIdByIdSedes(sedeId).size();
        if(cantidad>3){
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }
        if (foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "Debe subir un archivo");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }
        if (!verificarFoto(foto)) {
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }
        String fotoNombre = foto.getOriginalFilename();

        if (fotoNombre.contains("..")) {
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "No se permiten '..' en el archivo");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }



        Fotossede fotossede = new Fotossede();
        try {
            fotossede.setFoto(foto.getBytes());
            fotossede.setIdsedes(sedeId);
        } catch (IOException e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",1);
            redirectAttributes.addFlashAttribute("msg", "Hubo un error al cargar el archivo");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }
        redirectAttributes.addFlashAttribute("msg","Se ha guardado la imagen exitosamente");
        fotoSedeRepository.save(fotossede);

        return "redirect:/admin/sedes/gestion/" + sedeId;
    }


}
