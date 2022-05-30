package pe.edu.pucp.teleticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Operador;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    @Value("${aplication.domain}")
    private String DOMINIO;

    private static final String FROM = "Teleticket Perú <teleticket2022@gmail.com>";

    private static final String LOGO_IMAGE = "static/img/logo-teleticket.png";

    private static final String PNG_MIME = "image/png";

    public void correoBienvenida(Cliente cliente) throws MessagingException {
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("cliente", cliente);
        context.setVariable("logo", LOGO_IMAGE);

        String process = templateEngine.process("mail/welcome-placeholder", context);
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setSubject("Bienvenid@ a Teleticket, " + cliente.getNombre());
        helper.setText(process, true);
        helper.setFrom(FROM);
        helper.setTo(cliente.getCorreo());
        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        helper.addInline("logo", clr, PNG_MIME);
        javaMailSender.send(mimeMessage);
    }

    public void correoOpRegistrado(Operador operador) throws MessagingException {
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("operador", operador);
        context.setVariable("logo", LOGO_IMAGE);

        String process = templateEngine.process("mail/operador-registrado", context);
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setSubject("Bienvenid@ a Teleticket, " + operador.getCorreo());
        helper.setText(process, true);
        helper.setFrom(FROM);
        helper.setTo(operador.getCorreo());
        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        helper.addInline("logo", clr, PNG_MIME);
        javaMailSender.send(mimeMessage);
    }

    public void correoCambioContrasena(String correo, String token) throws MessagingException {
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("token", token);
        context.setVariable("logo", LOGO_IMAGE);

        String process = templateEngine.process("mail/cambio-password", context);
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setSubject("Cambio de contraseña");
        helper.setText(process, true);
        helper.setFrom(FROM);
        helper.setTo(correo);
        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        helper.addInline("logo", clr, PNG_MIME);
        javaMailSender.send(mimeMessage);
    }

}
