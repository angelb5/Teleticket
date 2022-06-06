package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.teleticket.entity.Calificacionobra;
import pe.edu.pucp.teleticket.entity.Obra;

import java.util.List;
import java.util.Optional;

public interface CalificacionObraRepository extends JpaRepository<Calificacionobra, Integer> {

    @Query(nativeQuery = true, value = "select estrellas " +
            "from calificacionobras " +
            "where idfunciones = :idfunciones " +
            "and idclientes = :idclientes " +
            "and idobras = :idobras ")
    Integer findEstrellasObraCliente(int idfunciones, int idclientes, int idobras);

    @Query(nativeQuery = true, value = "select * " +
            "from calificacionobras " +
            "where idfunciones = :idfunciones " +
            "and idclientes = :idclientes " +
            "and idobras = :idobras ")
    Calificacionobra findCalificacionobraDB(int idfunciones, int idclientes, int idobras);

    @Query(nativeQuery = true, value = "select avg(estrellas) from calificacionobras where idobras = :idobras")
    Optional<Float> getPuntajeByIdobra(int idobras);

}