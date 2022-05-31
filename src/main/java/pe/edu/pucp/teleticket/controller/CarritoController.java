package pe.edu.pucp.teleticket.controller;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.dto.FuncionesCompra;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Historial;
import pe.edu.pucp.teleticket.repository.FuncionRepository;
import pe.edu.pucp.teleticket.repository.HistorialRepository;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    FuncionRepository funcionRepository;

    @Autowired
    HistorialRepository historialRepository;

    private final int MIN = 8;

    @GetMapping({"","/"})
    public String mostrarCarrito(Model model, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        List<Historial> carrito = (List<Historial>) session.getAttribute("carrito");
        List<FuncionesCompra> funcionesCompras = new ArrayList<>();
        float total=0;
        int carritoSize = carrito.size();
        carrito = historialRepository.findReservasByIdclientes(clienteSes.getId());
        for (Historial reserva : carrito){
            FuncionesCompra fc = funcionRepository.getFuncionesCompraPorId(reserva.getIdfunciones());
            funcionesCompras.add(fc);
            total += fc.getCosto()*reserva.getNumtickets();
        }

        session.setAttribute("carrito", carrito);
        if(carrito.size()<carritoSize){
            model.addAttribute("error",1);
            model.addAttribute("msg", "Una o más de sus reservas han caducado");
        }
        model.addAttribute("funciones", funcionesCompras);
        model.addAttribute("total",total);
        return "/cliente/carrito/carrito";
    }

    @PostMapping("/anadir")
    public String anadirCarrito(Historial historial, RedirectAttributes attr, HttpSession session, Model model){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        Optional<FuncionesCompra> optFuncionCompra = Optional.ofNullable(funcionRepository.getFuncionesCompraPorId(historial.getIdfunciones()));
        Optional<Funcion> optFuncion = funcionRepository.findById(historial.getIdfunciones());
        if(optFuncion.isEmpty() || optFuncionCompra.isEmpty()){
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "No se han podido añadir los tickets al carrito");
            return "redirect:/";
        }

        Funcion funcion = optFuncion.get();
        FuncionesCompra funcionCompra = optFuncionCompra.get();
        if(funcion.getFecha().isEqual(LocalDate.now()) && funcion.getInicio().isBefore(LocalTime.now().plusMinutes(MIN))){
                attr.addFlashAttribute("error",1);
                attr.addFlashAttribute("msg", "Esta función ya no admite reservas");
                return "redirect:/";
        }

        if(historialRepository.findCompraByIdclientesAndIdfunciones(clienteSes.getId(),historial.getIdfunciones()).isPresent()){
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "Ya tienes tickets para esta función");
            return "redirect:/";
        }

        List<FuncionesCompra> historialEnConflicto = historialRepository.findHistorialEnConflicto(historial.getIdfunciones(),clienteSes.getId(),funcion.getFecha(),funcion.getInicio(),funcion.getFin());
        if(historialEnConflicto.size()>0){
            model.addAttribute("historialEnConflicto", historialEnConflicto);
            List<Historial> carrito = (List<Historial>) session.getAttribute("carrito");
            List<FuncionesCompra> funcionesCompras = new ArrayList<>();
            float total=0;
            int carritoSize = carrito.size();
            carrito = historialRepository.findReservasByIdclientes(clienteSes.getId());
            for (Historial reserva : carrito){
                FuncionesCompra fc = funcionRepository.getFuncionesCompraPorId(reserva.getIdfunciones());
                funcionesCompras.add(fc);
                total += fc.getCosto()*reserva.getNumtickets();
            }

            session.setAttribute("carrito", carrito);
            if(carrito.size()<carritoSize){
                model.addAttribute("error",1);
                model.addAttribute("msg", "Una o más de sus reservas han caducado");
            }
            model.addAttribute("funciones", funcionesCompras);
            model.addAttribute("total",total);
            return "/cliente/carrito/carrito";
        }

        if(historial.getNumtickets()>funcionCompra.getDisponible()){
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "Hubo un cambio en la disponibilidad de los tickets");
            return "redirect:/";
        }

        Optional<Historial> optReserva = historialRepository.findReservaByIdclientesAndIdfunciones(clienteSes.getId(), historial.getIdfunciones());
        if(optReserva.isPresent()){
            Historial reserva = optReserva.get();
            reserva.setNumtickets(historial.getNumtickets());
            historialRepository.save(reserva);
        }else{
            historial.setIdclientes(clienteSes.getId());
            historial.setEstado("Reserva");
            historial.setFechalimite(LocalDateTime.now().plusMinutes(MIN));
            historialRepository.save(historial);
        }

        session.setAttribute("carrito", historialRepository.findReservasByIdclientes(clienteSes.getId()));

        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarCarrito(int idfunciones, RedirectAttributes attr, HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        Optional<Historial> optReserva = historialRepository.findReservaByIdclientesAndIdfunciones(clienteSes.getId(), idfunciones);
        if(optReserva.isPresent()){
            Historial reserva = optReserva.get();
            reserva.setEstado("Cancelado");
            historialRepository.save(reserva);
        }

        session.setAttribute("carrito", historialRepository.findReservasByIdclientes(clienteSes.getId()));

        return "redirect:/carrito";
    }

    @PostMapping("/comprar")
    public String comprarCarrito(HttpSession session){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}
        List<Historial> carrito = historialRepository.findReservasByIdclientes(clienteSes.getId());
        for (Historial item : carrito){
            FuncionesCompra fc = funcionRepository.getFuncionesCompraPorId(item.getIdfunciones());
            item.setTotal(fc.getCosto()*item.getNumtickets());
            item.setEstado("Comprado");
            item.setIdcompra(RandomString.make(8).toUpperCase());
            item.setFechacompra(LocalDateTime.now());
        }
        historialRepository.saveAll(carrito);
        return "redirect:/cliente/tickets";
    }
}
