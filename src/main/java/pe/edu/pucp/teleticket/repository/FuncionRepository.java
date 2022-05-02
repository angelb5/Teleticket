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

    public long countAllByObra(Obra obra);

    List<Funcion> findAllByObra(Obra obra, Pageable pageable);

}