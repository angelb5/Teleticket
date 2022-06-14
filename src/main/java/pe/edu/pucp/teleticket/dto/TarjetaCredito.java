package pe.edu.pucp.teleticket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
public class TarjetaCredito {
    public TarjetaCredito(String numero, String fechaexpiracion, String cvv){
        this.numero=numero;
        this.fechaexpiracion=fechaexpiracion;
        this.cvv=cvv;
    }
    private String numero;
    private String fechaexpiracion;
    private String cvv;
}
