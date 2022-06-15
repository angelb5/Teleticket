package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.teleticket.dto.MejoresCalificacioneDto;
import pe.edu.pucp.teleticket.entity.Calificacionpersona;

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

    @Query(value = "select p.nombre,p.foto,cp.estrellas from personas as p \n" +
            "inner join calificacionpersonas as cp on p.idpersonas=cp.idpersonas\n" +
            "inner join actores as a on p.idpersonas=a.idpersona\n" +
            "where p.estado='Disponible' order by cp.estrellas desc",nativeQuery = true)
    List<MejoresCalificacioneDto> listaMejoresActores();

    @Query(value = "select p.nombre,p.foto,cp.estrellas from personas as p \n" +
            "inner join calificacionpersonas as cp on p.idpersonas=cp.idpersonas\n" +
            "inner join directores as d on p.idpersonas=d.idpersona\n" +
            "where p.estado='Disponible' order by cp.estrellas desc",nativeQuery = true)
    List<MejoresCalificacioneDto> listaMejoresDirectores();


}