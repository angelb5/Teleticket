package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Persona;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "calificacionpersonas")
public class Calificacionpersona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcalificacion")
    private int id;

    @Column(name = "idclientes")
    private int idclientes;

    @Column(name = "idpersonas")
    private int idpersonas;

    @JoinColumn(name = "idfunciones")
    private int idfunciones;

    @Lob
    @Column(name = "rol")
    private String rol;

    @Column(name = "estrellas")
    private int estrellas;
}