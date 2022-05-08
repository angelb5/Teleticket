package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.repository.ClienteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/clientes")
public class AdminClientesController {

    private final int clientePaginas = 5;

    @Autowired
    ClienteRepository clienteRepository;

    @GetMapping({"/", "", "/lista"})
    public String listarClientes(Model model, @RequestParam("pag") Optional<String> pagString, @ModelAttribute("query") String query) {
        int pagina;
        try {
            if (pagString.isEmpty() || pagString.get().isEmpty()) {
                pagina = 0;
            } else {
                pagina = Integer.parseInt(pagString.get());
            }
            if(query==null){
                query="";
            }
        } catch (Exception e) {
            return "redirect:/admin/clientes";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) clienteRepository.count() / clientePaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista = PageRequest.of(pagina - 1, clientePaginas);
        List<Cliente> listaClientes = clienteRepository.findAllByOrderByIdAsc(lista);
        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);

        return "admin/clientes/lista";
    }

    @GetMapping("/{filtro}")
    public String listarClientesFiltro(Model model, @RequestParam("pag") Optional<String> pagString,
                                       @PathVariable("filtro") Optional<String> filtro, @ModelAttribute("query") String query) {
        int pagina;
        try {
            if (pagString.isEmpty() || pagString.get().isEmpty()) {
                pagina = 0;
            } else {
                pagina = Integer.parseInt(pagString.get());
            }
            if(query==null){
                query="";
            }

        } catch (Exception e) {
            return "redirect:/admin/clientes";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) clienteRepository.count() / clientePaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista = PageRequest.of(pagina - 1, clientePaginas);
        List<Cliente> listaClientes = new ArrayList<>();
        switch (filtro.get()){
            case "concompras":
                listaClientes = clienteRepository.listarClientesConCompras("",lista);
                model.addAttribute("filtro", "Con compras");
                break;
            case "concriticas":
                listaClientes = clienteRepository.listarClientesConCriticas("",lista);
                model.addAttribute("filtro", "Con críticas");
                break;
            case "sinhistorial":
                listaClientes = clienteRepository.listarClientesSinHistorial("",lista);
                model.addAttribute("filtro", "Sin historial");
                break;
            default:
                return "redirect:/admin/clientes";
        }

        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);

        return "admin/clientes/lista";
    }
}
