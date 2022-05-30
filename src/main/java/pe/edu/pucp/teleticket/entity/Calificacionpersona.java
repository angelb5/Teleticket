package pe.edu.pucp.teleticket.entity;

import pe.edu.pucp.teleticket.entity.Cliente;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Persona;

import javax.persistence.*;

@Entity
@Table(name = "calificacionpersonas")
public class Calificacionpersona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcalificacion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idclientes", nullable = false)
    private Cliente idclientes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpersonas", nullable = false)
    private Persona idpersonas;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idfunciones", nullable = false)
    private Funcion idfunciones;

    @Lob
    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "estrellas", nullable = false)
    private Integer estrellas;

    public Integer getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(Integer estrellas) {
        this.estrellas = estrellas;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Funcion getIdfunciones() {
        return idfunciones;
    }

    public void setIdfunciones(Funcion idfunciones) {
        this.idfunciones = idfunciones;
    }

    public Persona getIdpersonas() {
        return idpersonas;
    }

    public void setIdpersonas(Persona idpersonas) {
        this.idpersonas = idpersonas;
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