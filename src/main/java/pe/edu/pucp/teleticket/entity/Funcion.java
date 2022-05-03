package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "funciones")
public class Funcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfunciones", nullable = false)
    private int id;

    @NotNull(message = "Obra no puede estar vacía")
    @ManyToOne
    @JoinColumn(name = "idobras", nullable = false)
    private Obra obra;

    @ManyToOne
    @JoinColumn(name = "idsalas")
    @NotNull(message = "Sala no puede estar vacía")
    private Sala sala;

    @NotNull(message = "Fecha no puede estar vacía")
    @Column(name = "fecha", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio no puede estar vacía")
    @Column(name = "inicio", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime inicio;

    @Column(name = "fin", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime fin;

    @Min(value=10, message = "Valor mínimo 10.00")
    @Max(value = 500, message = "Valor máximo 500.00")
    @Column(name = "costo")
    private float costo;

    @Min(value=10, message = "Valor mínimo 10")
    @Column(name = "stock")
    private int stock;

    @Min(value=1, message = "Valor mínimo 1")
    @Max(value = 10, message = "Valor máximo 10")
    @Column(name = "maxreservas", nullable = false)
    private int maxreservas;

    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

    public String getEstadoRVC(){
        if (!this.getEstado().equals("Cancelada")){
            if (this.getFecha().isAfter(LocalDate.now())){
                return "Vigente";
            }else{
                return "Realizada";
            }
        }else{
            return "Cancelada";
        }
    }

}