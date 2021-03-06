package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.repository.ClienteRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/cliente")
@Controller
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;

    private final List<String> formatos= Arrays.asList("media/png","media/jpeg", "image/jpeg", "image/png");
    private final LocalDate NACIMIENTO_MIN = LocalDate.parse("1903-01-01");

    @GetMapping("/miperfil")
    public String miPerfil(Model model, HttpSession session){
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        model.addAttribute("cliente", cliente);
        return "cliente/miperfil";
    }

    @GetMapping("/imagen")
    public void mostrarImagen(HttpSession session, HttpServletResponse response)throws ServletException, IOException {
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        Optional<Cliente> optClienteDB = clienteRepository.findById(cliente.getId());
        byte[] foto = optClienteDB.get().getFoto();
        if(foto == null) {
            foto = this.getClass().getClassLoader().getResourceAsStream("static/img/default-profile.png").readAllBytes();
        }
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(foto);
        response.getOutputStream().close();
    }

    @PostMapping("/actualizar")
    public String actualizarCliente(@ModelAttribute("cliente") @Valid Cliente cliente, BindingResult bindingResult,
                                    RedirectAttributes attr, Model model, HttpSession session){
        boolean nacimientoHasErrors = false;

        if(!bindingResult.hasFieldErrors("nacimiento")){
            LocalDate maxDate = LocalDate.now().minusYears(13);
            if(cliente.getNacimiento().isAfter(maxDate)){
                FieldError nacimientoerror = new FieldError("nacimiento", "nacimiento", "Debes tener por lo menos 13 a??os para registrarte");
                bindingResult.addError(nacimientoerror);
                nacimientoHasErrors=true;
            } else if(cliente.getNacimiento().isBefore(NACIMIENTO_MIN)){
                FieldError nacimientoerror = new FieldError("nacimiento", "nacimiento", "La fecha debe ser mayor que " +NACIMIENTO_MIN);
                bindingResult.addError(nacimientoerror);
                nacimientoHasErrors=true;
            }
        }

        if(bindingResult.hasFieldErrors("nacimiento") || bindingResult.hasFieldErrors("direccion") ||
                bindingResult.hasFieldErrors("celular") || nacimientoHasErrors){
            return "cliente/miperfil";
        }else{
            cliente.setDireccion(cliente.getDireccion().trim());
            Cliente clienteSes = (Cliente) session.getAttribute("usuario");
            clienteRepository.updateCliente(cliente.getNacimiento(), cliente.getCelular(), cliente.getDireccion(), clienteSes.getId());
            Cliente clienteDB = clienteRepository.findByCorreo(clienteSes.getCorreo());
            session.setAttribute("usuario", clienteDB);
            attr.addFlashAttribute("msg", "Se ha actualizado la informaci??n");
            return "redirect:/cliente/miperfil";
        }
    }

    @PostMapping("/cambiarimagen")
    public String cambiarImagen(@RequestParam("foto") MultipartFile foto, RedirectAttributes attr, HttpSession session){

        Cliente clienteSes = (Cliente) session.getAttribute("usuario");

        if (foto.isEmpty()) {
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "Debe subir un archivo");
            return "redirect:/cliente/miperfil";
        }
        if (!verificarFoto(foto)) {
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "Debe subir una imagen, no se acepta otros archivos");
            return "redirect:/cliente/miperfil";
        }
        String fotoNombre = foto.getOriginalFilename();

        if (fotoNombre.contains("..")) {
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "No se permiten '..' en el archivo");
            return "redirect:/cliente/miperfil";
        }

        try {
            byte[] fotoB = foto.getBytes();
            clienteRepository.updateFoto(fotoB,clienteSes.getId());
            attr.addFlashAttribute("msg","Se ha actualizado la foto de perfil");
            return "redirect:/cliente/miperfil";
        } catch (IOException e){
            e.printStackTrace();
            attr.addFlashAttribute("error",1);
            attr.addFlashAttribute("msg", "Hubo un error al cargar el archivo");
            return "redirect:/cliente/miperfil";
        }
    }

    @PostMapping("/cambiarcontrasena")
    public String cambiarContrasena(@RequestParam("pwactual") String contrasenaactual, @RequestParam("contrasena") String contrasena,
                                    @RequestParam("confcontrasena") String confcontrasena,
                                    RedirectAttributes attr, Model model, HttpSession session){
        Cliente clienteSes = (Cliente) session.getAttribute("usuario");

        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if(!b.matches(contrasenaactual, clienteRepository.getContrasenaById(clienteSes.getId()))){
            attr.addFlashAttribute("contrasenaerrores", "La contrase??a actual es incorrecta");
            return "redirect:/cliente/miperfil";
        }

        boolean contrasenaHasErrors = false;

        if(contrasena.length()<8 || contrasena.length()>72){
            attr.addFlashAttribute("contrasenaerrores", "La contrase??a debe contener entre 8 y 72 caracteres");
            contrasenaHasErrors = true;
        }else if(!validarContrasena(contrasena)){
            attr.addFlashAttribute("contrasenaerrores", "La contrase??a debe contener caracteres en may??sculas y min??sculas y n??meros");
            contrasenaHasErrors = true;
        } else if(!contrasena.equals(confcontrasena)){
            attr.addFlashAttribute("contrasenaerrores", "Las contrase??as no coinciden");
            contrasenaHasErrors = true;
        }

        if(contrasenaHasErrors){
            return "redirect:/cliente/miperfil";
        }else{
            contrasena = new BCryptPasswordEncoder().encode(contrasena);
            clienteRepository.updateContrasena(contrasena,clienteSes.getId());

            attr.addFlashAttribute("msg", "Se ha actualizado la contrase??a");
            return "redirect:/cliente/miperfil";
        }
    }

    private static boolean validarContrasena(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z??-??])(?=.*[A-Z])"
                + "(?=\\S+$).{8,72}$";

        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private boolean verificarFoto(MultipartFile file){
        if(formatos.contains(file.getContentType().toLowerCase(Locale.ROOT))){
            return true;
        }
        return false;
    }
}
