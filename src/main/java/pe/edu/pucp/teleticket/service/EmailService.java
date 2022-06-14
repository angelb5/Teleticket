package pe.edu.pucp.teleticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Historial;
import pe.edu.pucp.teleticket.entity.Operador;
import pe.edu.pucp.teleticket.repository.ClienteRepository;
import pe.edu.pucp.teleticket.repository.HistorialRepository;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;

@EnableAsync
@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    @Value("${aplication.domain}")
    private String DOMINIO;

    @Autowired
    HistorialRepository historialRepository;

    @Autowired
    ClienteRepository clienteRepository;

    private static final String FROM = "Teleticket Perú <teleticket2022@gmail.com>";

    private static final String LOGO_IMAGE = "static/img/logo-teleticket.png";

    private static final String PNG_MIME = "image/png";

    private final String qrurl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&margin=7&data=";

    @Async
    public void correoBienvenida(Cliente cliente) throws MessagingException {
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("cliente", cliente);
        context.setVariable("logo", LOGO_IMAGE);

        String process = templateEngine.process("mail/bienvenida-cliente", context);
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

    @Async
    public void correoResumenCompra(Cliente cliente, String idcompra) throws MessagingException {
        Historial ticket = historialRepository.findVigenteById(cliente.getId(), idcompra);
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("cliente", cliente);
        context.setVariable("ticket",ticket);
        context.setVariable("logo", LOGO_IMAGE);
        context.setVariable("qrcode", qrurl+idcompra);

        String process = templateEngine.process("mail/resumen-compra", context);
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setSubject("Compra exitosa - Teleticket Perú");
        helper.setText(process, true);
        helper.setFrom(FROM);
        helper.setTo(cliente.getCorreo());
        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        helper.addInline("logo", clr, PNG_MIME);
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void correoCompraCancelada(Cliente cliente, String idcompra) throws MessagingException {
        Historial ticket = historialRepository.findByIdclientesIdcompra(cliente.getId(), idcompra);
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("cliente", cliente);
        context.setVariable("ticket",ticket);
        context.setVariable("logo", LOGO_IMAGE);

        String process = templateEngine.process("mail/compra-cancelada", context);
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setSubject("Compra cancelada - Teleticket Perú");
        helper.setText(process, true);
        helper.setFrom(FROM);
        helper.setTo(cliente.getCorreo());
        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        helper.addInline("logo", clr, PNG_MIME);
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void correoFuncionCancelada(Historial ticket) throws MessagingException {
        Cliente cliente = clienteRepository.findById(ticket.getIdclientes()).get();
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("cliente", cliente);
        context.setVariable("ticket",ticket);
        context.setVariable("logo", LOGO_IMAGE);

        String process = templateEngine.process("mail/func-cancelada-op", context);
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setSubject("Lo sentimos, se ha cancelado tu función - Teleticket Perú");
        helper.setText(process, true);
        helper.setFrom(FROM);
        helper.setTo(cliente.getCorreo());
        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        helper.addInline("logo", clr, PNG_MIME);
        javaMailSender.send(mimeMessage);
    }

}
