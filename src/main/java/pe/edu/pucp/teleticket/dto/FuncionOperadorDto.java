package pe.edu.pucp.teleticket.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface FuncionOperadorDto {
    int getId();

    String getObra();

    Integer getFoto();

    LocalDate getFecha();

    LocalTime getHora();

    String getSede();

    int getEstado();

}
