package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Historial;
import pe.edu.pucp.teleticket.repository.HistorialRepository;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RequestMapping("/cliente/tickets")
@Controller
public class ClienteTicketsController {

    private final int historialPaginas = 3;

    @Autowired
    HistorialRepository historialRepository;

    @GetMapping({"/","","/vigentes"})
    public String listarVigentes(Model model, @RequestParam("pag") Optional<Integer> pag, HttpSession session){
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        Cliente clienteSes = (Cliente) session.getAttribute("usuario");
        int paginas = (int) Math.ceil((float)historialRepository.contarComprasVigentes(clienteSes.getId())/historialPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista = PageRequest.of(pagina-1, historialPaginas);
        List<Historial> comprasVigentes = historialRepository.findComprasVigentes(clienteSes.getId(), lista);

        model.addAttribute("comprasVigentes",comprasVigentes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);

        return "/cliente/tickets/vigentes";
    }

    @GetMapping("/asistidas")
    public String listarAsistidas(Model model, @RequestParam("pag") Optional<Integer> pag, HttpSession session){
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        Cliente clienteSes = (Cliente) session.getAttribute("usuario");
        int paginas = (int) Math.ceil((float)historialRepository.contarComprasAsistidas(clienteSes.getId())/historialPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista = PageRequest.of(pagina-1, historialPaginas);
        List<Historial> comprasVigentes = historialRepository.findComprasAsistidas(clienteSes.getId(), lista);

        model.addAttribute("comprasVigentes",comprasVigentes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);

        return "/cliente/tickets/vigentes";
    }
}
