package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.Operador;
import pe.edu.pucp.teleticket.repository.OperadorRepository;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin/operadores")
public class AdminOperadoresController {

    private final int operadoresPaginas=8;

    @Autowired
    OperadorRepository operadorRepository;


    @GetMapping({"/","","/lista"})
    public String listarSedes(@RequestParam("busqueda") Optional<String> optionalBusqueda, Model model,
                              @RequestParam("pag") Optional<String> pagString){
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim(): "";
        int pagina;
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
        int paginas = (int) Math.ceil((float) operadorRepository.contarListaOperadores(busqueda.toLowerCase()) / operadoresPaginas);
        paginas = paginas==0? 1 : paginas;
        pagina = pagina > paginas ? paginas : pagina;

        Pageable lista = PageRequest.of(pagina - 1, operadoresPaginas);

        model.addAttribute("operadores", operadorRepository.listarOperadores(busqueda.toLowerCase(), lista));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", "/admin/operadores?");
        model.addAttribute("index", (pagina-1)*operadoresPaginas+1);


        return "admin/operadores/lista";
    }

    @GetMapping("/borrar")
    public String borrarOperador(RedirectAttributes redirectAttributes, @RequestParam("id") Optional<String> idOptional){
        int id=0;
        try{
            id=Integer.parseInt(idOptional.get());
        } catch (Exception e){
            e.printStackTrace();
            return "redirect:/admin/operadores";
        }
        Optional<Operador> optionalOperador = operadorRepository.findById(id);
        if(optionalOperador.isEmpty()){
            redirectAttributes.addFlashAttribute("msg", "El operador ha eliminar no fue encontrado");
            return "redirect:/admin/operadores";
        }
        redirectAttributes.addFlashAttribute("msg", "El operador "+optionalOperador.get().getNombre()+ " Se ha eliminado");
        operadorRepository.delete(optionalOperador.get());
        return "redirect:/admin/operadores";
    }

    @GetMapping("/nuevo")
    public String nuevoOperador(@ModelAttribute Operador operador){
        return "/admin/operadores/form";
    }

    @PostMapping("/guardar")
    public String guardarOperador(@ModelAttribute @Valid Operador operador, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "/admin/operadores/form";
        }

        operadorRepository.save(operador);
        redirectAttributes.addFlashAttribute("msg", "Se ha enviado el correo a la direccion indicada. Avisar al nuevo operador " +
                "que ingrese al link enviado para que termine de crear la cuenta");

        return "redirect:/admin/operadores";
    }

}
