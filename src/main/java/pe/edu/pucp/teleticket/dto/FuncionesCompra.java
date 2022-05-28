package pe.edu.pucp.teleticket.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public interface FuncionesCompra extends Serializable {
    int getId();

    int getIdobras();

    int getFotoprincipal();

    int getDuracion();

    String getTitulo();

    String getNombresala();

    String getNombresede();

    LocalDate getFecha();

    LocalTime getInicio();

    float getCosto();

    int getDisponible();

}
