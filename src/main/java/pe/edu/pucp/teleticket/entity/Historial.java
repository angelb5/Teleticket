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

    @ManyToOne
    @JoinColumn(name = "idfunciones", updatable = false,insertable = false)
    private Funcion funcion;

    @Column(name = "idcompra")
    private String idcompra;

    @Column(name = "total")
    private double total;

    @Column(name = "numtickets")
    private int numtickets;

    @Column(name = "fechacompra")
    private LocalDateTime fechacompra;

    @Column(name = "fechalimite")
    private LocalDateTime fechalimite;

    @Lob
    @Column(name = "estado")
    private String estado;

    public Historial(int id, int idclientes, int idfunciones, int numtickets, LocalDateTime fechalimite, String estado) {
        this.id = id;
        this.idclientes = idclientes;
        this.idfunciones = idfunciones;
        this.numtickets = numtickets;
        this.fechalimite = fechalimite;
        this.estado = estado;
    }

    public Historial(int id, int idclientes, Funcion funcion, String idcompra, double total, int numtickets, LocalDateTime fechacompra) {
        this.id = id;
        this.idclientes = idclientes;
        this.funcion = funcion;
        this.idcompra = idcompra;
        this.total = total;
        this.numtickets = numtickets;
        this.fechacompra = fechacompra;
    }

    public Historial() {
    }
}