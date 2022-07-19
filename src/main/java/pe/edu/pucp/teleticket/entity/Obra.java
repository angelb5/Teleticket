package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "obras")
public class Obra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idobras", nullable = false)
    private int id;

    @Length(message = "El máximo son 45 caracteres", max = 45)
    @NotBlank(message = "Tiene que indicar un título")
    @Column(name = "titulo", nullable = false, length = 45)
    private String titulo;

    @NotNull(message = "Tiene que seleccionar la restricción de edad")
    @Lob
    @Column(name = "restricciones", nullable = false)
    private String restricciones;

    @Positive(message = "El valor tiene que ser positivo")
    @Column(name = "duracion", nullable = false)
    private int duracion;

    @NotBlank(message = "Tiene que indicar el resumen de la obra")
    @Length(message = "El resumen no puede contener más de 600 caracteres", max = 600)
    @Column(name = "resumen", nullable = false, length = 600)
    private String resumen;

    @Column(name = "destacado", nullable = false)
    private int destacado;

    @Size(message = "Tiene que seleccionar al menos un género", min = 1)
    @ManyToMany
    @JoinTable(name = "obrasgenero",
            joinColumns = @JoinColumn(name = "idobras", referencedColumnName = "idobras"),
            inverseJoinColumns = @JoinColumn(name = "idgenero", referencedColumnName = "idgenero"))
    private List<Genero> generos = new ArrayList<>();

    @Column(name = "fotoprincipal", nullable = true)
    private Integer fotoprincipal;


    @Size(message = "Tiene que seleccionar al menos un actor", min = 1)
    @ManyToMany
    @JoinTable(name = "actores",
            joinColumns = @JoinColumn(name = "idobra", referencedColumnName = "idobras"),
            inverseJoinColumns = @JoinColumn(name = "idpersona", referencedColumnName = "idpersonas"))
    private List<Persona> actores = new ArrayList<>();

    @Size(message = "Tiene que seleccionar al menos un director", min = 1)
    @NotNull
    @ManyToMany
    @JoinTable(name = "directores",
            joinColumns = @JoinColumn(name = "idobra", referencedColumnName = "idobras"),
            inverseJoinColumns = @JoinColumn(name = "idpersona", referencedColumnName = "idpersonas"))
    private List<Persona> directores = new ArrayList<>();

}