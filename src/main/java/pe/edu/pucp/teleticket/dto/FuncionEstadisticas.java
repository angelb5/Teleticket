package pe.edu.pucp.teleticket.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public interface FuncionEstadisticas {

    int getId();

    String getSala();

    String getSede();

    LocalDate getFecha();

    LocalTime getHora();

    int getVendidos();

    float getRecaudacion();

    int getCalificaciones();

    float getPuntuacion();
}
