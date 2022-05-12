package pe.edu.pucp.teleticket.controller;


import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.AdminRepository;
import pe.edu.pucp.teleticket.repository.ClienteRepository;
import pe.edu.pucp.teleticket.repository.OperadorRepository;
import pe.edu.pucp.teleticket.service.EmailService;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
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

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

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
            return "redirect:";
        }
    }

    @GetMapping("/oauth2/completaregistro")
    public String registroOauth2(Model model, HttpSession session){
        Cliente cliente = (Cliente) session.getAttribute("googleCliente");
        if(session.getAttribute("usuario")!=null || cliente ==null){return "redirect:/";}
        model.addAttribute("cliente",cliente);
        return "/sesion/registro";
    }

    @GetMapping("/oauth2/redirect")
    public String redirectOauth2(Model model, OAuth2AuthenticationToken authentication, HttpSession session){
        OAuth2AuthorizedClient authorizedClient = this.getAuthorizedClient(authentication);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        Map userAttributes = Collections.emptyMap();
        String userInfoEndpointUri = authorizedClient.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        userAttributes = WebClient.builder().filter(oauth2Credentials(authorizedClient))
                    .build().get().uri(userInfoEndpointUri).retrieve().bodyToMono(Map.class).block();

        String email = (String) userAttributes.get("email");

        Optional<Cliente> optionalCliente = Optional.ofNullable(clienteRepository.findByCorreo(email));
        if(optionalCliente.isPresent()){
            Cliente cliente = optionalCliente.get();
            session.setAttribute("usuario",cliente);
            session.setAttribute("rol", "cliente");
            return "redirect:/";
        }else{
            session.setAttribute("proveedor", "Sesión de Google");

            Cliente cliente = new Cliente();

            String given_name = (String) userAttributes.get("given_name");
            String family_name = (String) userAttributes.get("family_name");
            String name = (String) userAttributes.get("name");
            String picture = (String) userAttributes.get("picture");

            if(given_name!=null && family_name!=null){
                cliente.setNombre(given_name);
                cliente.setApellido(family_name);
            }else if(name!=null){
                cliente.setNombre(name);
            }
            cliente.setCorreo(email);
            if(picture!=null){
                session.setAttribute("picture", picture);
            }

            session.setAttribute("googleCliente", cliente);
            model.addAttribute("cliente",cliente);
            return "/sesion/registro";
        }
    }

    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
    }

    private ExchangeFilterFunction oauth2Credentials(OAuth2AuthorizedClient authorizedClient) {
        return ExchangeFilterFunction.ofRequestProcessor(
                clientRequest -> {
                    ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                                    authorizedClient.getAccessToken().getTokenValue()).build();
                    return Mono.just(authorizedRequest);
                });
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

            if(session.getAttribute("proveedor")!=null){
                cliente = clienteRepository.findByCorreo(cliente.getCorreo());
                session.setAttribute("usuario", cliente);
                session.setAttribute("rol", "cliente");
                if(session.getAttribute("picture")!=null){
                    subirFotoGoogle((String) session.getAttribute("picture"), cliente.getId());
                }
            }
            return "redirect:/";
        }
    }

    private void subirFotoGoogle(String picture, int id){
        try{
            URL url = new URL(picture);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), baos);

            byte[] foto = baos.toByteArray();
            clienteRepository.updateFoto(foto, id);
        }
        catch (Exception e)
        {
            System.out.println("No se pudo subir la imagen");
            e.printStackTrace();
        }
    }

}
