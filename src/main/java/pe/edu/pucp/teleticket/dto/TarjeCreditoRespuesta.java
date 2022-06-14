package pe.edu.pucp.teleticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarjeCreditoRespuesta {
    private String estado;
    private String msg;
    private Informacion informacion;
}
