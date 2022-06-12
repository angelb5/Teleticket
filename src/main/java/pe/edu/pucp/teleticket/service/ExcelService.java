package pe.edu.pucp.teleticket.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Historial;
import pe.edu.pucp.teleticket.repository.FuncionRepository;
import pe.edu.pucp.teleticket.repository.HistorialRepository;
import pe.edu.pucp.teleticket.repository.ObraRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelService {

    @Autowired
    FuncionRepository funcionRepository;
    @Autowired
    HistorialRepository historialRepository;

    public ByteArrayInputStream exportarReportePorId(int id){
        Optional<Funcion> optFuncion = funcionRepository.findById(id);
        if(optFuncion.isEmpty() || optFuncion.get().getEstadoRVC().equals("Cancelada")){
            return  null;
        }
        try(Workbook workbook = new XSSFWorkbook()){
            Funcion funcion = optFuncion.get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Sheet sheet = workbook.createSheet("Reporte");

            Font defaultFont= workbook.createFont();
            defaultFont.setFontHeightInPoints((short)11);
            defaultFont.setFontName("Arial");
            defaultFont.setColor(IndexedColors.BLACK.getIndex());
            defaultFont.setBold(false);
            defaultFont.setItalic(false);

            Font bold = workbook.createFont();
            bold.setFontHeightInPoints((short)11);
            bold.setFontName("Arial");
            bold.setColor(IndexedColors.BLACK.getIndex());
            bold.setBold(true);
            bold.setItalic(false);

            Font titulo = workbook.createFont();
            titulo.setFontHeightInPoints((short)14);
            titulo.setFontName("Arial");
            titulo.setColor(IndexedColors.BLACK.getIndex());
            titulo.setBold(true);
            titulo.setItalic(false);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(bold);

            CellStyle boldCellStyle = workbook.createCellStyle();
            boldCellStyle.setFont(bold);

            CellStyle tituloCellStyle = workbook.createCellStyle();
            tituloCellStyle.setFont(titulo);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            //Titulos
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Teleticket Perú");
            cell.setCellStyle(tituloCellStyle);

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("Reporte de función");
            cell.setCellStyle(tituloCellStyle);

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Fecha y hora de creación del reporte");
            cell = row.createCell(1);
            cell.setCellValue(LocalDateTime.now().format(formatter));
            cell.setCellStyle(dataCellStyle);

            //Datos de la funcion
            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellValue("Obra");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getObra().getTitulo());
            cell.setCellStyle(dataCellStyle);

            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellValue("Sede");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getSala().getSede().getNombre());
            cell.setCellStyle(dataCellStyle);

            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell.setCellValue("Sala");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getSala().getNumero());
            cell.setCellStyle(dataCellStyle);

            row = sheet.createRow(7);
            cell = row.createCell(0);
            cell.setCellValue("Estado");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getEstadoRVC());
            cell.setCellStyle(dataCellStyle);

            row = sheet.createRow(8);
            cell = row.createCell(0);
            cell.setCellValue("Aforo");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getStock());
            cell.setCellStyle(dataCellStyle);

            row = sheet.createRow(9);
            cell = row.createCell(0);
            cell.setCellValue("Fecha");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getFecha().toString());
            cell.setCellStyle(dataCellStyle);

            row = sheet.createRow(10);
            cell = row.createCell(0);
            cell.setCellValue("Hora de inicio");
            cell.setCellStyle(boldCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(funcion.getInicio().toString());
            cell.setCellStyle(dataCellStyle);

            List<Historial> compras = historialRepository.findComprasByIdfunciones(id);

            if(compras.size()<1){
                row = sheet.createRow(12);
                cell = row.createCell(0);
                if(funcion.getEstadoRVC().equals("Vigente")){
                    cell.setCellValue("Esta función aun no tiene compras");
                }else{
                    cell.setCellValue("Esta función no tuvo compras");
                }
            }else{

                float total=0;
                int ticketstotal=0;
                for(Historial compra :compras){
                    total+=compra.getTotal();
                    ticketstotal+=compra.getNumtickets();
                }

                //Datos de compras
                row = sheet.createRow(12);
                cell = row.createCell(0);
                cell.setCellValue("Total de compras");
                cell.setCellStyle(boldCellStyle);
                cell = row.createCell(1);
                cell.setCellValue(compras.size());
                cell.setCellStyle(dataCellStyle);

                row = sheet.createRow(13);
                cell = row.createCell(0);
                cell.setCellValue("Total de tickets vendidos");
                cell.setCellStyle(boldCellStyle);
                cell = row.createCell(1);
                cell.setCellValue(ticketstotal);
                cell.setCellStyle(dataCellStyle);

                row = sheet.createRow(14);
                cell = row.createCell(0);
                cell.setCellValue("Porcentaje de asistencia");
                cell.setCellStyle(boldCellStyle);
                cell = row.createCell(1);
                cell.setCellValue(String.format("%.2f", (float) ticketstotal*100/funcion.getStock()) + "%");
                cell.setCellStyle(dataCellStyle);

                row = sheet.createRow(15);
                cell = row.createCell(0);
                cell.setCellValue("Recaudación total");
                cell.setCellStyle(boldCellStyle);
                cell = row.createCell(1);
                cell.setCellValue("S/ " + total);
                cell.setCellStyle(dataCellStyle);

                //Tabla de compras
                row = sheet.createRow(17);
                cell = row.createCell(0);
                cell.setCellValue("Compras");
                cell.setCellStyle(boldCellStyle);

                row = sheet.createRow(18);
                cell = row.createCell(0);
                cell.setCellValue("ID de compra");
                cell.setCellStyle(headerCellStyle);

                cell = row.createCell(1);
                cell.setCellValue("Número de tickets");
                cell.setCellStyle(headerCellStyle);

                cell = row.createCell(2);
                cell.setCellValue("Fecha de compra");
                cell.setCellStyle(headerCellStyle);

                cell = row.createCell(3);
                cell.setCellValue("Total de compra");
                cell.setCellStyle(headerCellStyle);


                int i = 19;

                // Creating data rows for each customer
                for(Historial compra : compras) {
                    Row dataRow = sheet.createRow(i);
                    cell = dataRow.createCell(0);
                    cell.setCellValue(compra.getIdcompra());

                    cell = dataRow.createCell(1);
                    cell.setCellValue(compra.getNumtickets());
                    cell.setCellStyle(dataCellStyle);

                    cell= dataRow.createCell(2);
                    cell.setCellValue(compra.getFechacompra().format(formatter));
                    cell.setCellStyle(dataCellStyle);

                    cell = dataRow.createCell(3);
                    cell.setCellValue("S/ "+compra.getTotal());
                    cell.setCellStyle(dataCellStyle);
                    i++;
                }

            }

            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
