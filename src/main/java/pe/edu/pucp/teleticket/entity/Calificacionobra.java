package pe.edu.pucp.teleticket.entity;

import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;

import javax.persistence.*;

@Entity
@Table(name = "calificacionobras")
public class Calificacionobra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcalificacion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idclientes", nullable = false)
    private Cliente idclientes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idobras", nullable = false)
    private Obra idobras;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idfunciones", nullable = false)
    private Funcion idfunciones;

    @Column(name = "estrellas", nullable = false)
    private Integer estrellas;

    public Integer getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(Integer estrellas) {
        this.estrellas = estrellas;
    }

    public Funcion getIdfunciones() {
        return idfunciones;
    }

    public void setIdfunciones(Funcion idfunciones) {
        this.idfunciones = idfunciones;
    }

    public Obra getIdobras() {
        return idobras;
    }

    public void setIdobras(Obra idobras) {
        this.idobras = idobras;
    }

    public Cliente getIdclientes() {
        return idclientes;
    }

    public void setIdclientes(Cliente idclientes) {
        this.idclientes = idclientes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}