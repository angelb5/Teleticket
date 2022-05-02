package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.pucp.teleticket.entity.Sala;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.SalaRepository;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/sedes")
public class AdminSedesController {

    private final int sedesPaginas = 6;
    private final int salasPaginas = 5;


    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    SalaRepository salaRepository;

    @GetMapping({"/","","/lista"})
    public String listarSedes(Model model, @RequestParam("pag") Optional<Integer> pag){
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas = (int) Math.ceil((float)sedeRepository.count()/sedesPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista;
        if(pagina==0){
            lista= PageRequest.of(0, sedesPaginas);
        } else {
            lista= PageRequest.of(pagina-1, sedesPaginas);

        }
        List<Sede> listaSedes = sedeRepository.findAllByOrderByIdAsc(lista);
        model.addAttribute("listaSedes",listaSedes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        return "admin/sedes/lista";
    }

    @GetMapping("/gestion/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("pag") Optional<Integer> pag){
        int id=0;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede =sedeRepository.findById(id);
        if(optionalSede.isEmpty()){return "redirect:/admin/sedes";}

        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        Sede sede = new Sede();
        sede.setId(id);
        int paginas = (int) Math.ceil((float)salaRepository.countAllBySede(sede)/salasPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista;
        if(pagina==0){
            lista= PageRequest.of(0, salasPaginas);
        } else {
            lista= PageRequest.of(pagina-1, salasPaginas);

        }

        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("sede",optionalSede.get());
        model.addAttribute("salas", salaRepository.findAllBySede(sede,lista));
        return "/admin/sedes/gestionsede";
    }

    @GetMapping("/gestion/{idPath}/nuevasala")
    public String nuevaSala(@PathVariable("idPath") String idPath, Model model){
        int id=0;
        try{
            id= Integer.parseInt(idPath);
        } catch (Exception e){
            return "redirect:/admin/sedes";
        }
        Optional<Sede> optionalSede =sedeRepository.findById(id);
        if(optionalSede.isEmpty()){return "redirect:/admin/sedes";}
        model.addAttribute("id",id);

        return "/admin/sedes/salas/form";
    }
}
