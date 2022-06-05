package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "calificacionobras")
public class Calificacionobra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcalificacion", nullable = false)
    private int id;

    @Column(name = "idclientes")
    private int idclientes;

    @Column(name = "idobras")
    private int idobras;

    @Column(name = "idfunciones")
    private int idfunciones;

    @Column(name = "estrellas")
    private int estrellas;
}