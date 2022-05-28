package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "historialcompras")
public class Historial implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idhistorial")
    private int id;

    @Column(name = "idclientes")
    private int idclientes;

    @Column(name = "idfunciones")
    private int idfunciones;

    @Column(name = "idcompra")
    private String idcompra;

    @Column(name = "total")
    private double total;

    @Column(name = "numtickets")
    private int numtickets;

    @Column(name = "fechacompra")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechacompra;

    @Column(name = "fechalimite")
    private LocalDateTime fechalimite;

    @Lob
    @Column(name = "estado")
    private String estado;

}