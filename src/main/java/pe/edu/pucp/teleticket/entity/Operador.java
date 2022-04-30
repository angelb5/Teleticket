package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "operadores")
public class Operador implements Serializable {
    @Id
    @Column(name = "idoperadores", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 45)
    private String correo;

    @Column(name = "token", length = 16)
    private String token;

}