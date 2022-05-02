package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "salas")
public class Sala {
    @Id
    @Column(name = "idsalas", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "idsedes", nullable = false)
    private Sede sede;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "aforo", nullable = false)
    private Integer aforo;

    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;
}