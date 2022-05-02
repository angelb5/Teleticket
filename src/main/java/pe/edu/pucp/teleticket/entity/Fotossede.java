package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "fotossedes")
public class Fotossede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfotossedes", nullable = false)
    private Integer id;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @Column(name = "idsedes", nullable = false)
    private int idsedes;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "contenido", nullable = false, length = 45)
    private String contenido;

}