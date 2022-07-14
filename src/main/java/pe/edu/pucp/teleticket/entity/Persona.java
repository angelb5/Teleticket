package pe.edu.pucp.teleticket.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "personas")
@NoArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpersonas", nullable = false)
    private int id;

    @NotEmpty(message = "Tiene que llenar el nombre")
    @Length(message = "No puede exceder de los 45 caracteres", max = 45)
    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @NotNull(message = "Tiene que seleccionar un estado")
    @Lob
    @Column(name = "estado")
    private String estado;

    public Persona(int id, String nombre, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }
}