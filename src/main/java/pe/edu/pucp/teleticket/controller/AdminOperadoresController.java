package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.pucp.teleticket.repository.OperadorRepository;

import java.util.Optional;

@Controller
@RequestMapping("/admin/operadores")
public class AdminOperadoresController {

    private final int operadoresPaginas=8;

    @Autowired
    OperadorRepository operadorRepository;


    @GetMapping({"/","","/lista"})
    public String listarSedes(@ModelAttribute("nombreOperador") String nombreOperador, Model model,
                              @RequestParam("pag") Optional<String> pagString){
        int pagina;
        try {
            if (pagString.isEmpty() || pagString.get().isEmpty()) {
                pagina = 0;
            } else {
                pagina = Integer.parseInt(pagString.get());
            }
            if(nombreOperador==null){
                nombreOperador="";
            }
        } catch (Exception e) {
            return "redirect:/admin/clientes";
        }
        pagina = pagina < 1 ? 1 : pagina;
        int paginas = (int) Math.ceil((float) operadorRepository.contarListaOperadores(nombreOperador) / operadoresPaginas);
        pagina = pagina > paginas ? paginas : pagina;
        Pageable lista = PageRequest.of(pagina - 1, operadoresPaginas);

        model.addAttribute("operadores", operadorRepository.listarOperadores(nombreOperador, lista));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", "/admin/operadores");


        return "admin/operadores/lista";
    }
}
