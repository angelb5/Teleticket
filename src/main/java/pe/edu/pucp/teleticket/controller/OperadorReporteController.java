package pe.edu.pucp.teleticket.controller;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import pe.edu.pucp.teleticket.repository.ObraRepository;
import pe.edu.pucp.teleticket.service.ExcelService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;


@Controller
@RequestMapping("/operador/reporte")
public class OperadorReporteController {

    @Autowired
    ObraRepository obraRepository;
    @Autowired
    ExcelService excelService;

    @GetMapping("/{idPath}")
    public void descargarXlsx(Model model, @PathVariable("idPath") String idPath, HttpServletResponse response) throws IOException {
        int id = 0;
        try{
            id =  Integer.parseInt(idPath);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=Reporte"+id+".xlsx");
            ByteArrayInputStream stream = excelService.exportarReportePorId(id);
            IOUtils.copy(stream, response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
