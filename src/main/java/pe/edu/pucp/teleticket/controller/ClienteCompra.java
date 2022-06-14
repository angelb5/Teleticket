package pe.edu.pucp.teleticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.teleticket.dao.TarjetaCreditoDao;
import pe.edu.pucp.teleticket.dto.TarjetaCredito;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/compra")
public class ClienteCompra {

    private boolean stringIsNumeric(String string){
        if (Pattern.matches("[a-zA-Z]+", string) == false) {
            return true;
        }
        return false;
    }

    private List<String> meses = new ArrayList<>();

    private List<String> listarMeses(){
        if(!this.meses.isEmpty()){
            return  this.meses;
        }
        List<String> meses = new ArrayList<>();
        for(int i=1;i<13;i++){
            if(i<10){
                meses.add("0"+String.valueOf(i));}
            else {
                meses.add(String.valueOf(i));}
            }

        this.meses=meses;
        return meses;
    }

    private List<String> listarAnos(){
        List<String> anos = new ArrayList<>();
        int actual=Calendar. getInstance(). get(Calendar. YEAR);
        int fin= actual+13;
        for(int i=actual;i<fin;i++){
            anos.add(String.valueOf(i));
        }
        return anos;
    }

    @GetMapping("/tarjeta")
    public String pagoTarjeta(Model model){
        model.addAttribute("anos", listarAnos());
        model.addAttribute("meses", listarMeses());
        HashMap<String,String> errores = new HashMap<>();
        model.addAttribute("errores", errores);

        return "cliente/compra/formTarjeta";
    }

    @PostMapping("/tarjeta")
    public String verificarTarjeta(
            @RequestParam("numero") String numero,
            @RequestParam("mes") String mes,
            @RequestParam("ano") String ano,
            @RequestParam("cvv") String cvv,
            Model model
    ){
        boolean errorExiste = false;
        HashMap<String,String> errores = new HashMap<>();
        if(!stringIsNumeric(numero) || numero.length()!=16){
            errores.put("numero","El formato no es correcto");
            errorExiste=true;
        }
        if(!stringIsNumeric(mes) || !stringIsNumeric(ano)){
            errorExiste=true;
            errores.put("fecha","La fecha no tiene el formato correcto");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(ano+"-"+mes+"-01",formatter);
        if(date.isBefore(LocalDate.now())){
            errorExiste=true;
            errores.put("tarjeta","La tarjeta de encuentra caducada");
        }
        if(!stringIsNumeric(cvv) || cvv.length()!=3){
            errorExiste=true;
            errores.put("cvv","No tiene el formato correcto");
        }
        if(errorExiste){
            model.addAttribute("errores",errores);
            model.addAttribute("anos", listarAnos());
            model.addAttribute("meses", listarMeses());
            model.addAttribute("cvv",cvv);
            model.addAttribute("numero",numero);
            model.addAttribute("mes",mes);
            model.addAttribute("ano",ano);
            return "cliente/compra/formTarjeta";
        }
        TarjetaCreditoDao tarjetaCreditoDao = new TarjetaCreditoDao();
        TarjetaCredito tarjetaCredito = new TarjetaCredito(numero,ano+"/"+mes,cvv);
        try{
        boolean validarTarjeta = tarjetaCreditoDao.validarTarjeta(tarjetaCredito);
            if(!validarTarjeta){
                model.addAttribute("anos", listarAnos());
                model.addAttribute("meses", listarMeses());
                model.addAttribute("cvv",cvv);
                model.addAttribute("numero",numero);
                model.addAttribute("mes",mes);
                model.addAttribute("ano",ano);
                errores.put("tarjeta","La tarjeta ingresada no es válida");
                model.addAttribute("errores",errores);
                return "cliente/compra/formTarjeta";
            }
        } catch (Exception e){
            model.addAttribute("anos", listarAnos());
            model.addAttribute("meses", listarMeses());
            model.addAttribute("cvv",cvv);
            model.addAttribute("numero",numero);
            model.addAttribute("mes",mes);
            model.addAttribute("ano",ano);
            errores.put("tarjeta","Hubo un error y no se pudo procesar la tarjeta");
            model.addAttribute("errores",errores);
            return "cliente/compra/formTarjeta";
        }

        //De aquí a abajo nada sirve, se debe cambiar.
        model.addAttribute("anos", listarAnos());
        model.addAttribute("meses", listarMeses());
        model.addAttribute("cvv",cvv);
        model.addAttribute("mes",mes);
        model.addAttribute("ano",ano);
        model.addAttribute("numero",numero);
        errores.put("tarjeta","Todo bien, solo que no se como funciona la compra xd");
        model.addAttribute("errores",errores);


        return "cliente/compra/formTarjeta";
    }
}
