package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;

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

    @NotEmpty(message = "Tiene que designar un número a la sala")
    @Length(message = "Tiene que designar entre 1 a 20 caracteres", min = 1, max = 20)
    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Positive(message = "Tiene que designar un valor positivo de personas")
    @Max(message = "El aforo no puede exceder el de mil personas por razones de seguridad", value = 1000)
    @NotNull(message = "Tiene que designar una cantida de aforo")
    @Column(name = "aforo", nullable = false)
    private Integer aforo;

    @NotNull(message = "Tiene que designar un estado a la sala")
    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;
}