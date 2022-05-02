package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/clientes")
public class AdminClientesController {

    private final int clientePaginas = 5;

    @Autowired
    ClienteRepository clienteRepository;

    @GetMapping({"/","","/lista"})
    public String listarClientes(Model model, @RequestParam("pag") Optional<Integer> pag){
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas = (int) Math.ceil ((float) clienteRepository.count()/clientePaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista= PageRequest.of(pagina-1, clientePaginas);
        List<Cliente> listaClientes = clienteRepository.findAllByOrderByIdAsc(lista);
        model.addAttribute("listaClientes",listaClientes);
        model.addAttribute("pag",pagina);
        model.addAttribute("paginas", paginas);

        return "admin/clientes/lista";
    }
}
