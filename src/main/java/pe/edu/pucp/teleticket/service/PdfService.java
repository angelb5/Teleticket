package pe.edu.pucp.teleticket.service;

import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Historial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class PdfService {

    @Value("${aplication.domain}")
    private String DOMINIO;

    private static final String LOGO_IMAGE = "static/img/logo-teleticket.png";

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd MMMM, yyyy").toFormatter(new Locale("es", "ES"));

    private final String url = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&margin=7&data=";

    private final TemplateEngine templateEngine;

    public ByteArrayInputStream exportarPdfPorId(Cliente cliente, Historial ticket) throws DocumentException, IOException {
        Context context = new Context();
        context.setVariable("dominio", DOMINIO);
        context.setVariable("cliente", cliente);
        context.setVariable("ticket",ticket);
        context.setVariable("fecha",formatter.format(ticket.getFuncion().getFecha()));
        context.setVariable("logo", LOGO_IMAGE);
        context.setVariable("qrcode", url+ticket.getIdcompra());
        String process = templateEngine.process("mail/resumen-compra-forpdf", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(process);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
