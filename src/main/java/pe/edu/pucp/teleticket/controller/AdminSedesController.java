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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/sedes")
public class AdminSedesController {

    private final int sedesPaginas = 6;
    private final int salasPaginas = 5;

    @Autowired
    FuncionRepository funcionRepository;
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    SalaRepository salaRepository;
    @Autowired
    FotoSedeRepository fotoSedeRepository;

    @GetMapping({"/", "", "/lista"})
    public String listarSedes(Model model, @RequestParam("pag") Optional<Integer> pag) {
        int pagina = pag.isEmpty() ? 1 : pag.get();
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) sedeRepository.count() / sedesPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista;
        if (pagina == 0) {
            lista = PageRequest.of(0, sedesPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, sedesPaginas);

        }
        List<Sede> listaSedes = sedeRepository.findAllByOrderByIdAsc(lista);
        model.addAttribute("listaSedes", listaSedes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
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
        sede = optionalSede.get();
        model.addAttribute("sede", sede);
        return "admin/sedes/form";
    }

    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute("sede") @Valid Sede sede, BindingResult bindingResult, @RequestParam("foto") MultipartFile foto, Model model) {
        if (foto.isEmpty()) {
            model.addAttribute("msg", "Debe subir un archivo");
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
        Sede sedeCreada = sedeRepository.save(sede);
        try {
            fotosede.setFoto(foto.getBytes());
            fotosede.setIdsedes(sedeCreada.getId());
            fotoSedeRepository.save(fotosede);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Ocurrió un error al subir el archivo");
            return "admin/sedes/form";
        }
        sede.setFotoprincipal(fotosede.getId());
        sedeRepository.save(sede);


        sede.setFotoprincipal(fotosede.getId());

        return "redirect:/admin/sedes/gestion/" + sede.getId();
    }

    @PostMapping("/actualizar")
    public String actualizarSede(@ModelAttribute("sede") @Valid Sede sede, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/sedes/form";
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
        return "/admin/sedes/gestionsede";
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
        if (optionalSede.isEmpty()) {
            return "redirect:/admin/sedes";
        }
        sala.setSede(new Sede());
        sala.getSede().setId(idSede);
        return "/admin/sedes/salas/form";
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
        if (optionalSala.isEmpty()) {
            return "redirect:/admin/sedes";
        }
        sala = optionalSala.get();
        model.addAttribute("sala", sala);
        model.addAttribute("idSede", sala.getSede().getId());
        return "/admin/sedes/salas/form";
    }

    @PostMapping("/gestion/sala/guardar")
    public String guardarSala(@ModelAttribute("sala") @Valid Sala sala, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            return "/admin/sedes/salas/form";
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
                    return "/admin/sedes/salas/form";
                }else{
                    long maxStockByIdsala = funcionRepository.getMaxStockByIdsala(sala.getId());
                    if(maxStockByIdsala>sala.getAforo()){
                        FieldError fAforoError = new FieldError("aforo", "aforo", "El aforo de la sala no puede ser menor que "+ maxStockByIdsala + " ya que hay una función asociada");
                        bindingResult.addError(fAforoError);
                        return "/admin/sedes/salas/form";
                    }
                }
            }
            msg = "Sala editada exitosamente";
        }
        redirectAttributes.addFlashAttribute("msg", msg);
        salaRepository.save(sala);
        return "redirect:/admin/sedes/gestion/" + sala.getSede().getId();
    }

    @PostMapping("/gestion/{idSedeString}/imagenprincipal")
    public String imagenPrincipal(@PathVariable("idSedeString") String idSedeString, @RequestParam("fotoid") String fotoidString) {
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
        return "redirect:/admin/sedes/gestion/" + sedeId;
    }

    @PostMapping("/gestion/imagenborrar")
    public String borrarImagen(@RequestParam("fotoid") String fotoidString) {
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
        if (optionalSede.isEmpty() || (optionalSede.get().getFotoprincipal() == fotoid)) {
            return "redirect:/admin/sedes";
        }


        fotoSedeRepository.deleteById(fotoid);
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
            redirectAttributes.addFlashAttribute("msg", "Debe subir un archivo");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }
        String fotoNombre = foto.getOriginalFilename();
        if (fotoNombre.contains("..")) {
            redirectAttributes.addFlashAttribute("msg", "No se permiten '..' en el archivo");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }

        Fotossede fotossede = new Fotossede();
        try {
            fotossede.setFoto(foto.getBytes());
            fotossede.setIdsedes(sedeId);
        } catch (IOException e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Hubo un error al cargar el archivp");
            return "redirect:/admin/sedes/gestion/" + sedeId;
        }
        fotoSedeRepository.save(fotossede);


        return "redirect:/admin/sedes/gestion/" + sedeId;
    }


}
