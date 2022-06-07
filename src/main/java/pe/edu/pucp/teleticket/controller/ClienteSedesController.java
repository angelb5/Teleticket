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
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.FotoSedeRepository;
import pe.edu.pucp.teleticket.repository.ObraRepository;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/sedes")
public class ClienteSedesController {

    private final int sedesPaginas = 6;

    private final int obrasPaginas = 8;

    @Autowired
    SedeRepository sedeRepository;

    @Autowired
    FotoSedeRepository fotoSedeRepository;

    @Autowired
    ObraRepository obraRepository;

    @GetMapping({"/", "", "/lista"})
    public String listarSedes(Model model, @RequestParam("pag") Optional<Integer> pag,
                              @RequestParam("busqueda") Optional<String> optionalBusqueda) {
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta = busqueda.isBlank()? "/sedes?" : "/sedes?busqueda=" +busqueda +"&";
        int pagina = pag.isEmpty() ? 1 : pag.get();
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) sedeRepository.contarListarSedesCliente(busqueda) / sedesPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista;
        if (pagina == 0) {
            lista = PageRequest.of(0, sedesPaginas);
        } else {
            lista = PageRequest.of(pagina - 1, sedesPaginas);

        }
        List<Sede> listaSedes = sedeRepository.listarSedesCliente(busqueda, lista);
        model.addAttribute("listaSedes", listaSedes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);
        return "cliente/sedes/lista";
    }

    @GetMapping("/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("pag") Optional<Integer> pag) {
        int id = 0;
        try {
            id = Integer.parseInt(idPath);
        } catch (Exception e) {
            return "redirect:/sedes";
        }
        Optional<Sede> optionalSede = sedeRepository.findDisponibleById(id);
        if (optionalSede.isEmpty()) {
            return "redirect:/sedes";
        }

        int pagina = pag.isEmpty() ? 1 : pag.get();

        Integer cantidadObras = obraRepository.contarObrasClienteByIdsede(id)==null? 0 : obraRepository.contarObrasClienteByIdsede(id);
        int paginas = (int) Math.ceil((float) cantidadObras / obrasPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        pagina = pagina < 1 ? 1 : pagina;
        Pageable lista = PageRequest.of(pagina - 1, obrasPaginas);

        model.addAttribute("fotos", fotoSedeRepository.findAllIdByIdSedes(id));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("sede", optionalSede.get());
        model.addAttribute("obras", obraRepository.listadoObraslienteByIdsede(id, lista));
        model.addAttribute("ruta", "/sedes/" + id+ "?");
        return "cliente/sedes/sede";
    }

}
