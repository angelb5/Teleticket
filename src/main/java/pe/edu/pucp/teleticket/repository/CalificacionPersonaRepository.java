package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.teleticket.entity.Calificacionobra;
import pe.edu.pucp.teleticket.entity.Calificacionpersona;
import pe.edu.pucp.teleticket.entity.Persona;

import java.util.List;

public interface CalificacionPersonaRepository extends JpaRepository<Calificacionpersona, Integer> {

    @Query(nativeQuery = true, value = "select estrellas " +
            "from calificacionpersonas " +
            "where rol = 'Direccion' " +
            "and idfunciones = :idfunciones " +
            "and idclientes = :idclientes " +
            "and idpersonas = :idpersonas ")
    Integer findEstrellasDireccionCliente(int idfunciones, int idclientes, int idpersonas);

    @Query(nativeQuery = true, value = "select estrellas " +
            "from calificacionpersonas " +
            "where rol = 'Actuacion' " +
            "and idfunciones = :idfunciones " +
            "and idclientes = :idclientes " +
            "and idpersonas = :idpersonas ")
    Integer findEstrellasActuacionCliente(int idfunciones, int idclientes, int idpersonas);

    @Query(nativeQuery = true, value = "select * " +
            "from calificacionpersonas " +
            "where idfunciones = :idfunciones " +
            "and idclientes = :idclientes " +
            "and idpersonas = :idpersonas " +
            "and rol = :rol ")
    Calificacionpersona findCalificacionpersonaDB(int idfunciones, int idclientes, int idpersonas, String rol);
}