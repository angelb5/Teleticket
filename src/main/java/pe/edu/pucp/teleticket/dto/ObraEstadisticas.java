package pe.edu.pucp.teleticket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ObraEstadisticas {

    int id;

    String titulo;

    int fotoprincipal;

    float calificacion;

    float recaudacionTotal;

    int totalFunciones;

    int vigentes;

    int realizadas;

    int canceladas;

    FuncionEstadisticas funcionMasVista;

    FuncionEstadisticas funcionMenosVista;

    FuncionEstadisticas funcionMejorCalificada;

}
