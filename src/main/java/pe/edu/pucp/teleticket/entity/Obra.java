package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "obras")
public class Obra {
    @Id
    @Column(name = "idobras", nullable = false)
    private int id;

    @Column(name = "titulo", nullable = false, length = 45)
    private String titulo;

    @Lob
    @Column(name = "restricciones", nullable = false)
    private String restricciones;

    @Column(name = "duracion", nullable = false)
    private Integer duracion;

    @Column(name = "resumen", nullable = false, length = 600)
    private String resumen;

    @Column(name = "destacado", nullable = false)
    private Integer destacado;

}