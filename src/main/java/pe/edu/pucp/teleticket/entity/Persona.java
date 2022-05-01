package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "personas")
public class Persona {
    @Id
    @Column(name = "idpersonas", nullable = false)
    private int id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;
}