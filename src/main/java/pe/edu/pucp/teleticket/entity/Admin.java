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
@Table(name = "admin")
public class Admin implements Serializable {
    @Id
    @Column(name = "idadmin", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 45)
    private String correo;

}