package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "fotosobras")
public class Fotosobra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfotos", nullable = false)
    private Integer id;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @Column(name = "idobras")
    private int idobras;

}