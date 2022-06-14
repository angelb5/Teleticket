package pe.edu.pucp.teleticket.dto;

import java.sql.Blob;

public interface CalificacionPersonaDTO {
    Blob getFoto();
    String getNombre();
    Integer getEstrellas();

}
