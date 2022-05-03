package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "obras")
public class Obra {
    @Id
    @Column(name = "idobras", nullable = false)
    private int id;

    @Column(name = "titulo", nullable = false, length = 45)
    private String titulo;

    @Lob
    @Column(name = "restricciones", nullable = false)
    private String restricciones;

    @Column(name = "duracion", nullable = false)
    private int duracion;

    @Column(name = "resumen", nullable = false, length = 600)
    private String resumen;

    @Column(name = "destacado", nullable = false)
    private int destacado;

    @ManyToMany
    @JoinTable(name = "obrasgenero",
            joinColumns = @JoinColumn(name = "idobras"),
            inverseJoinColumns = @JoinColumn(name = "idgenero"))
    private Set<Genero> generos = new LinkedHashSet<>();

    @Column(name = "fotoprincipal", nullable = true)
    private Integer fotoprincipal;

}