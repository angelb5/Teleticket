package pe.edu.pucp.teleticket.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.AdminRepository;
import pe.edu.pucp.teleticket.repository.ClienteRepository;
import pe.edu.pucp.teleticket.repository.OperadorRepository;
import pe.edu.pucp.teleticket.service.EmailService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SesionController {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    OperadorRepository operadorRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String loginForm(){
        return "/sesion/login";
    }

    @GetMapping("registro")
    public String registroForm(@ModelAttribute("cliente") Cliente cliente){
        return "/sesion/registro";
    }

    @GetMapping("/redirectPorRol")
    public String redirectPorRol(Authentication auth, HttpSession session){
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()){
            rol = role.getAuthority();
            break;
        }
        if (rol.equals("administrador")){
            Admin admin = adminRepository.findByCorreo(auth.getName());
            session.setAttribute("usuario",admin);
            session.setAttribute("rol", "administrador");
            return "redirect:/admin";
        }else if(rol.equals("operador")){
            Operador operador = operadorRepository.findByCorreo(auth.getName());
            session.setAttribute("usuario",operador);
            session.setAttribute("rol", "operador");
            return "redirect:/operador";
        }else{
            Cliente cliente = clienteRepository.findByCorreo(auth.getName());
            session.setAttribute("usuario",cliente);
            session.setAttribute("rol", "cliente");
            return "redirect:/";
        }
    }

    private static boolean validarContrasena(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-zÀ-ȕ])(?=.*[A-Z])"
                + "(?=\\S+$).{8,72}$";

        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }

    @PostMapping("/registrar")
    public String guardarFuncion(@ModelAttribute("cliente") @Valid Cliente cliente,  BindingResult bindingResult,
                                 @RequestParam("contrasena") String contrasena,
                                 @RequestParam("confcontrasena") String confcontrasena,
                                 RedirectAttributes attr, Model model, HttpSession session){
        boolean contrasenaHasErrors = false;
        boolean correoHasErrors = false;
        boolean dniHasErrors = false;
        boolean nacimientoHasErrors = false;

        if(contrasena.length()<8 || contrasena.length()>72){
            ObjectError contrasenaError = new ObjectError("globalError", "La contraseña debe contener entre 8 y 72 caracteres");
            bindingResult.addError(contrasenaError);
            contrasenaHasErrors = true;
        }else if(!validarContrasena(contrasena)){
            ObjectError contrasenaError = new ObjectError("globalError", "La contraseña debe contener caracteres en mayúsculas y minúsculas y números");
            bindingResult.addError(contrasenaError);
            contrasenaHasErrors = true;
        } else if(!contrasena.equals(confcontrasena)){
            ObjectError contrasenaError = new ObjectError( "globalError", "Las contraseñas no coinciden");
            bindingResult.addError(contrasenaError);
            contrasenaHasErrors = true;
        }

        if(!bindingResult.hasFieldErrors("correo")){
            if(clienteRepository.findByCorreo(cliente.getCorreo())!=null){
                FieldError correoError = new FieldError("correo", "correo", "Ya existe un usuario con este correo");
                bindingResult.addError(correoError);
                correoHasErrors = true;
            }
        }

        if(!bindingResult.hasFieldErrors("dni")){
            if(clienteRepository.findByDni(cliente.getDni())!=null){
                FieldError dniError = new FieldError("dni", "dni", "Ya existe un usuario con este DNI");
                bindingResult.addError(dniError);
                dniHasErrors = true;
            }
        }

        if(!bindingResult.hasFieldErrors("nacimiento")){
            LocalDate minDate = LocalDate.now().minusYears(120);
            LocalDate maxDate = LocalDate.now().minusYears(13);
            if(cliente.getNacimiento().isAfter(maxDate)){
                FieldError nacimientoerror = new FieldError("nacimiento", "nacimiento", "Debes tener por lo menos 13 años para registrarte");
                bindingResult.addError(nacimientoerror);
                nacimientoHasErrors=true;
            } else if(cliente.getNacimiento().isBefore(minDate)){
                FieldError nacimientoerror = new FieldError("nacimiento", "nacimiento", "La fecha debe ser mayor que " +minDate);
                bindingResult.addError(nacimientoerror);
                nacimientoHasErrors=true;
            }
        }

        if(bindingResult.hasErrors() || contrasenaHasErrors || correoHasErrors || dniHasErrors || nacimientoHasErrors){
            return "/sesion/registro";
        }else{
            cliente.setNombre(cliente.getNombre().trim());
            cliente.setApellido(cliente.getApellido().trim());
            cliente.setDireccion(cliente.getDireccion().trim());
            contrasena = new BCryptPasswordEncoder().encode(contrasena);
            clienteRepository.registrarUsuario(cliente.getDni(), cliente.getNombre(),cliente.getApellido(),cliente.getCorreo(), contrasena, cliente.getCelular(), cliente.getNacimiento(), cliente.getDireccion());
            try {
                emailService.correoBienvenida(cliente);
            }catch (Exception e){
                e.printStackTrace();
            }
            attr.addFlashAttribute("msg", "¡Te has registrado exitosamente!");
            return "redirect:/";
        }
    }

}
