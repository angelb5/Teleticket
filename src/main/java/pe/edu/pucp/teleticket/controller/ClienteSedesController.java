package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.repository.SedeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/sedes")
public class ClienteSedesController {

    private final int sedesPaginas = 6;

    @Autowired
    SedeRepository sedeRepository;

    @GetMapping({"/", "", "/lista"})
    public String listarSedes(Model model, @RequestParam("pag") Optional<Integer> pag,
                              @RequestParam("busqueda") Optional<String> optionalBusqueda) {
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta = busqueda.isBlank()? "/?" : "/?busqueda=" +busqueda +"&";
        int pagina = pag.isEmpty() ? 1 : pag.get();
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
        return "/cliente/sedes/lista";
    }



}
