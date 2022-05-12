package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "operadores")
public class Operador implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idoperadores", nullable = false)
    private Integer id;

    @Length(message = "No puede exceder de 60 caracteres", max = 60)
    @NotBlank(message = "Tiene que llenar el nombre")
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Length(message = "No debe exceder de los 45 caracteres", max = 45)
    @Email(message = "Tiene que ser una dirección de correo")
    @NotBlank(message = "Tiene que indicar el correo")
    @Column(name = "correo", nullable = false, length = 45)
    private String correo;

    @Column(name = "token", length = 45)
    private String token;

}