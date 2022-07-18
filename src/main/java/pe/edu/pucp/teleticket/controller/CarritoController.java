package pe.edu.pucp.teleticket.controller;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.dao.TarjetaCreditoDao;
import pe.edu.pucp.teleticket.dto.FuncionesCompra;
import pe.edu.pucp.teleticket.dto.TarjetaCredito;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Historial;
import pe.edu.pucp.teleticket.repository.FuncionRepository;
import pe.edu.pucp.teleticket.repository.HistorialRepository;
import pe.edu.pucp.teleticket.service.EmailService;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    FuncionRepository funcionRepository;

    @Autowired
    HistorialRepository historialRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TarjetaCreditoDao tarjetaCreditoDao;

    private final int MIN = 8;

    private boolean stringIsNumeric(String string){
        if (Pattern.matches("[a-zA-Z]+", string) == false) {
            return true;
        }
        return false;
    }

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
            FuncionesCompra fc = funcionRepository.getFuncionesCompraPorIdfuncionIdclientes(reserva.getIdfunciones(), clienteSes.getId());
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
        return "cliente/carrito/carrito";
    }

    @PostMapping("/anadir")
    public String anadirCarrito(Historial historial, RedirectAttributes attr, HttpSession session, Model model){
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        Optional<FuncionesCompra> optFuncionCompra = Optional.ofNullable(funcionRepository.getFuncionesCompraPorIdfuncionIdclientes(historial.getIdfunciones(), clienteSes.getId()));
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
                FuncionesCompra fc = funcionRepository.getFuncionesCompraPorIdfuncionIdclientes(reserva.getIdfunciones(), clienteSes.getId());
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
            return "cliente/carrito/carrito";
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
    public String comprarCarrito(@RequestParam("numero") String numero, @RequestParam("mes") String mes,
                                 @RequestParam("ano") String ano, @RequestParam("ccv") String ccv, HttpSession session, RedirectAttributes attr){

        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){return "redirect:/";}

        String errores="";

        if(numero.isEmpty() || mes.isEmpty() || ano.isEmpty() || ccv.isEmpty()){
            errores = "Todos los campos son necesarios";
            attr.addFlashAttribute("errores",errores);
            return "redirect:/carrito";
        }
        numero = numero.replaceAll("\\s","");

        //Validacion de la tarjeta

        boolean errorExiste = false;

        if(!stringIsNumeric(numero) || numero.length() != 16){
            errores = "El formato del número no es correcto";
            errorExiste=true;
        }
        if(!stringIsNumeric(mes) || !stringIsNumeric(ano)){
            errorExiste=true;
            errores = "La fecha no tiene el formato correcto";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(ano + "-" + mes + "-01", formatter);
            if(date.isBefore(LocalDate.now())){
                errorExiste=true;
                errores = "La tarjeta se encuentra caducada";
            }
        }catch (Exception e){
            errorExiste=true;
            errores = "La fecha no tiene el formato correcto";
        }

        if(!stringIsNumeric(ccv) || ccv.length()<3 || ccv.length()>4){
            errorExiste=true;
            errores = "El CCV no tiene el formato correcto";
        }
        if(errorExiste){
            attr.addFlashAttribute("errores",errores);
            return "redirect:/carrito";
        }

        TarjetaCreditoDao tarjetaCreditoDao = new TarjetaCreditoDao();
        TarjetaCredito tarjetaCredito = new TarjetaCredito(numero,mes+"/"+ano,ccv);
        try{
            boolean validarTarjeta = tarjetaCreditoDao.validarTarjeta(tarjetaCredito);
            if(!validarTarjeta){
                errores = "La tarjeta ingresada no es válida";
                attr.addFlashAttribute("errores",errores);
                return "redirect:/carrito";
            }
        } catch (Exception e){
            e.printStackTrace();
            errores = "Hubo un error y no se pudo procesar la tarjeta";
            attr.addFlashAttribute("errores",errores);
            return "redirect:/carrito";
        }


        List<Historial> carrito = historialRepository.findReservasByIdclientes(clienteSes.getId());
        for (Historial item : carrito){
            Funcion fc = funcionRepository.findById(item.getIdfunciones()).get();
            item.setTotal(fc.getCosto()*item.getNumtickets());
            item.setEstado("Comprado");
            item.setIdcompra(RandomString.make(8).toUpperCase());
            item.setFechacompra(LocalDateTime.now());
        }
        historialRepository.saveAll(carrito);
        for (Historial item : carrito){
            try{
                emailService.correoResumenCompra(clienteSes,item.getIdcompra());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        session.setAttribute("carrito", historialRepository.findReservasByIdclientes(clienteSes.getId()));
        attr.addFlashAttribute("compraexitosa","true");
        return "redirect:/cliente/tickets";
    }


    @ResponseBody
    @GetMapping("/getnumtickets/{idfuncion}")
    public ResponseEntity<HashMap<String, Object>> anadirCarrito(@PathVariable("idfuncion") int idfuncion, RedirectAttributes attr, HttpSession session, Model model){
        HashMap<String, Object> responseJson = new HashMap<>();
        Object object = session.getAttribute("usuario");
        if(!(object instanceof Cliente clienteSes)){
            responseJson.put("result", "failure");
            responseJson.put("msg", "No es un cliente");
            return ResponseEntity.badRequest().body(responseJson);
        }

        if(historialRepository.findCompraByIdclientesAndIdfunciones(clienteSes.getId(),idfuncion).isPresent()){
            responseJson.put("result", "success");
            responseJson.put("estado", "Comprado");
            responseJson.put("msg", "Ya tienes tickets para esta función");
            return ResponseEntity.ok(responseJson);
        }

        Optional<Historial> optReserva = historialRepository.findReservaByIdclientesAndIdfunciones(clienteSes.getId(), idfuncion);
        if(optReserva.isPresent()){
            Historial reserva = optReserva.get();
            responseJson.put("result", "success");
            responseJson.put("estado", "Reserva");
            responseJson.put("numtickets", reserva.getNumtickets());
            return ResponseEntity.ok(responseJson);
        }

        responseJson.put("result", "success");
        responseJson.put("msg", "No hay compras ni reservas para ");
        return ResponseEntity.ok(responseJson);
    }


}
