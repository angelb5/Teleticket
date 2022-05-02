package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sedes")
public class Sede {
    @Id
    @Column(name = "idsedes", nullable = false)
    private int id;

    @Column(name = "nombre", nullable = false, length = 70)
    private String nombre;

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

    @OneToMany(mappedBy = "sede")
    private Set<Sala> salas = new LinkedHashSet<>();

}