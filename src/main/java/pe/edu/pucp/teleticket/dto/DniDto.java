package pe.edu.pucp.teleticket.dto;

import lombok.Getter;
import lombok.Setter;
import pe.edu.pucp.teleticket.entity.Dni;

@Getter
@Setter
public class DniDto {
    private String success;
    private Dni data;
}
