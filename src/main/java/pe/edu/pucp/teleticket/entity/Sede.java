package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.pucp.teleticket.repository.FotoSedeRepository;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sedes")

public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsedes", nullable = false)
    private int id;

    @NotEmpty(message = "Tiene que llenar el nombre")
    @Length(message = "No puede exceder de los 70 caracteres", max = 70)
    @Column(name = "nombre", nullable = false, length = 70)
    private String nombre;

    @NotNull(message = "Tiene que llenar la latitud")
    @Max(message = "No puede ser mayor a 90°", value = 90)
    @Min(message = "No puede ser menor a -90°", value = -90)
    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Max(message = "No puede ser mayor que 180°", value = 180)
    @Min(message = "No puede ser menor que -180°", value = -180)
    @NotNull(message = "Tiene que llenar la longitud")
    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @NotBlank(message = "Tiene que llenar la dirección")
    @Column(name = "direccion", nullable = false)
    private String direccion;

    @NotNull(message = "Tiene que seleccionar un estado")
    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

    @OneToMany(mappedBy = "sede")
    private Set<Sala> salas = new LinkedHashSet<>();

    @Column(name = "fotoprincipal", nullable = true)
    private Integer fotoprincipal;


}