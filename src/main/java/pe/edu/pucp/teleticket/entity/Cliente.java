package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 60)
    private String apellido;

    @Column(name = "correo", nullable = false, length = 60)
    private String correo;

    @Column(name = "celular", nullable = false, length = 9)
    private String celular;

    @Column(name = "nacimiento", nullable = false)
    private LocalDate nacimiento;

    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "token", length = 16)
    private String token;
}