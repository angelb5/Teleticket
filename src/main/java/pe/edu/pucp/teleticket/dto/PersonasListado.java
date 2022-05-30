package pe.edu.pucp.teleticket.dto;

import java.util.Optional;

public interface PersonasListado {

    int getId();

    String getNombre();

    Optional<Float> getPactuacion();

    Optional<Float> getPdireccion();

    String getEstado();

    Integer getObrasdirector();

    Integer getObrasactor();

    String getPtitulo();

}
