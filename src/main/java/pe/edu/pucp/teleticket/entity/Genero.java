package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "genero")
public class Genero {
    @Id
    @Column(name = "idgenero", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 18)
    private String nombre;
}