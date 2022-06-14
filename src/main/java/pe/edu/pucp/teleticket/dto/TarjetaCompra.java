package pe.edu.pucp.teleticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarjetaCompra {

    private Integer idcompra;
    private TarjetaCredito origen;
    private double cantidad;

}
