package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.ClienteListado;
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
    public String listarClientes(Model model, @RequestParam("pag") Optional<String> pagString, @RequestParam("busqueda") Optional<String> optionalBusqueda) {
        int pagina;
        String busqueda= optionalBusqueda.isPresent()? optionalBusqueda.get().trim() :  "";
        try {
            if (pagString.isEmpty() || pagString.get().isEmpty()) {
                pagina = 0;
            } else {
                pagina = Integer.parseInt(pagString.get());
            }
        } catch (Exception e) {
            return "redirect:/admin/clientes";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) clienteRepository.count() / clientePaginas);
        pagina = pagina > paginas ? paginas : pagina;

        String rutaPaginado = "/admin/clientes";
        if (busqueda.isBlank()) {
            rutaPaginado += "?";
        } else {
            rutaPaginado += "?busqueda="+busqueda + "&";
        }


        Pageable lista = PageRequest.of(pagina - 1, clientePaginas);
        List<ClienteListado> listaClientes = clienteRepository.listarClientes(busqueda.toLowerCase(),lista);
        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", rutaPaginado);
        model.addAttribute("busqueda", busqueda);

        return "admin/clientes/lista";
    }

    @GetMapping("/{filtro}")
    public String listarClientesFiltro(Model model, @RequestParam("pag") Optional<String> pagString,
                                       @PathVariable("filtro") Optional<String> filtro, @RequestParam("busqueda") Optional<String> optionalBusqueda) {
        int pagina;
        String busqueda= optionalBusqueda.isPresent()? optionalBusqueda.get().trim() :  "";
        try {
            if (pagString.isEmpty() || pagString.get().isEmpty()) {
                pagina = 0;
            } else {
                pagina = Integer.parseInt(pagString.get());
            }
        } catch (Exception e) {
            return "redirect:/admin/clientes";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) clienteRepository.count() / clientePaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista = PageRequest.of(pagina - 1, clientePaginas);
        List<ClienteListado> listaClientes = new ArrayList<>();
        String rutaPaginado= "/admin/clientes/";

        switch (filtro.get()) {
            case "concompras":
                rutaPaginado+= "concompras";
                listaClientes = clienteRepository.listarClientesConCompras(busqueda, lista);
                model.addAttribute("filtro", "Con compras");
                break;
            case "concriticas":
                rutaPaginado+= "concriticas";
                listaClientes = clienteRepository.listarClientesConCriticas(busqueda, lista);
                model.addAttribute("filtro", "Con críticas");
                break;
            case "sinhistorial":
                rutaPaginado+= "sinhistorial";
                model.addAttribute("ruta", "/admin/clientes/sinhistorial?");
                listaClientes = clienteRepository.listarClientesSinHistorial(busqueda, lista);
                model.addAttribute("filtro", "Sin historial");
                break;
            default:
                return "redirect:/admin/clientes";
        }
        if(busqueda.isBlank()){
            rutaPaginado+="?";
        } else{
            rutaPaginado+= "?busqueda=" + busqueda +"&";
        }

        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", rutaPaginado);
        model.addAttribute("busqueda", busqueda);

        return "admin/clientes/lista";
    }
}
