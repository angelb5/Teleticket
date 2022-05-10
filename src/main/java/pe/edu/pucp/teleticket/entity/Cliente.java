package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idclientes", nullable = false)
    private Integer id;

    @Size(min=8, max=8, message = "El DNI debe tener 8 números")
    @Pattern(message = "Solo puede ingresar números", regexp="^[0-9]*$")
    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 60,message = "El nombre debe tener como máximo 60 caracteres")
    @Pattern(regexp="^([a-zA-ZÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïðòóôõöùúûüýÿ \\\\s])+$",message = "El nombre solo puede contener letras")
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 60,message = "El apellido debe tener como máximo 60 caracteres")
    @Pattern(regexp="^([a-zA-ZÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïðòóôõöùúûüýÿ \\\\s])+$",message = "El apellido solo puede contener letras")
    @Column(name = "apellido", nullable = false, length = 60)
    private String apellido;

    @Email(message = "Debe ingresar un correo válido")
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 60,message = "El correo debe tener como máximo 60 caracteres")
    @Column(name = "correo", nullable = false, length = 60)
    private String correo;

    @Size(min=9, max=9, message = "El celular debe tener 9 números")
    @Pattern(message = "Solo puede ingresar números", regexp="^[0-9]*$")
    @Column(name = "celular", nullable = false, length = 9)
    private String celular;

    @NotNull(message = "Este campo no puede estar vacío")
    @Column(name = "nacimiento", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nacimiento;

    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 100,message = "El correo debe tener como máximo 100 caracteres")
    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "token", length = 16)
    private String token;
}