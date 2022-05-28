package pe.edu.pucp.teleticket.dto;

import java.util.ArrayList;
import java.util.List;

public class SedesCompra {

    private final String nombre, direccion;
    private final int id;
    private List<FuncionesCompra> funcionesCompra = new ArrayList<>();

    public SedesCompra(int id, String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getId() {
        return id;
    }

    public List<FuncionesCompra> getFuncionesCompra() {
        return funcionesCompra;
    }

    public void setFuncionesCompra(List<FuncionesCompra> funcionesCompra) {
        this.funcionesCompra = funcionesCompra;
    }
}
