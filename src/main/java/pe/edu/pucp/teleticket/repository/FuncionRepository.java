package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.entity.Sala;
import pe.edu.pucp.teleticket.entity.Sede;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion,Integer > {
    @Query(nativeQuery = true,
            value = "select distinct * from funciones " +
                    "where fecha = :fecha and estado = 'Activa' and " +
                    "( idobras = :idobras or idsalas= :idsalas ) and " +
                    "( CAST(:inicio  AS time) between inicio and fin or " +
                    " CAST( :fin AS time) between inicio and fin )")
    public List<Funcion> findFuncionesEnConflicto(LocalDate fecha, LocalTime inicio, LocalTime fin, int idobras, int idsalas);

    @Query(nativeQuery = true,
            value = "select distinct * from funciones " +
                    "where fecha = :fecha and estado = 'Activa' and " +
                    "( idobras = :idobras or idsalas= :idsalas ) and " +
                    "( CAST(:inicio  AS time) between inicio and fin or " +
                    " CAST( :fin AS time) between inicio and fin ) and "+
                    " idfunciones <> :idfuncion")
    public List<Funcion> findFuncionesEnConflictoId(LocalDate fecha, LocalTime inicio, LocalTime fin, int idobras, int idsalas, int idfuncion);

    public long countAllByObra(Obra obra);

    List<Funcion> findAllByObraOrderByFechaAsc(Obra obra, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select * from funciones where idsalas = :idsalas and estado = 'Activa' and fecha > DATE(now())")
    public List<Funcion> getVigentesByIdsalas(int idsalas);

    @Query(nativeQuery = true,
            value = "SELECT max(stock) FROM funciones where idsalas = :idsalas and estado = 'Activa' and fecha > DATE(now())")
    public long getMaxStockByIdsala(int idsalas);

}