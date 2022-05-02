package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idobras", nullable = false)
    private Obra obra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsalas", nullable = false)
    private Sala sala;

    @Column(name = "fecha", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @Column(name = "inicio", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime inicio;

    @Column(name = "fin", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime fin;

    @Column(name = "costo", nullable = false, length = 45)
    private String costo;

    @Column(name = "stock", nullable = false, length = 45)
    private String stock;

    @Column(name = "maxreservas", nullable = false)
    private Integer maxreservas;

    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

}