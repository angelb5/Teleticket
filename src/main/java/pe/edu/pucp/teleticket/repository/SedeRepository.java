package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.dto.SedeEstadisticas;
import pe.edu.pucp.teleticket.dto.SedeFiltro;
import pe.edu.pucp.teleticket.entity.Sede;


import java.util.List;
import java.util.Optional;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {

    List<Sede> findAllByOrderByIdAsc(Pageable pageable);

    @Query(nativeQuery = true, value = "select * from sedes where estado = 'Disponible'")
    public List<Sede> findSedesDisponibles();

    @Query(nativeQuery = true, value = "select * from sedes where estado = 'Disponible' and idsedes=?1")
    public Optional<Sede> findDisponibleById(int id);

    @Query(nativeQuery = true, value = "select * from sedes where lower(nombre) like %?1% or lower(direccion) like %?1% order by nombre asc",
    countQuery = "select count(*) from sedes where lower(nombre) like %?1% or lower(direccion) like %?1% order by nombre asc")
    public List<Sede> listarSedesBusqueda(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value= "select count(*) from sedes where lower(nombre) like %?1% or lower(direccion) like %?1% order by nombre asc")
    public long contarListarSedesBusqueda(String busqueda);

    @Query(nativeQuery = true, value = "select * from sedes " +
            "where estado = 'Disponible' and ( lower(nombre) like %?1% or lower(direccion) like %?1% )" +
            "order by nombre asc",
            countQuery = "select count(*) from sedes " +
                    "where estado = 'Disponible' and  ( lower(nombre) like %?1% or lower(direccion) like %?1% ) order by nombre asc")
    public List<Sede> listarSedesCliente(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value= "select count(*) from sedes " +
            "where estado = 'Disponible' and  ( lower(nombre) like %?1% or lower(direccion) like %?1% ) order by nombre asc")
    public long contarListarSedesCliente(String busqueda);

    public long countAllByEstadoEqualsIgnoreCase(String estado);

    @Query(nativeQuery = true, value = "select distinct se.idsedes as id, se.nombre as nombre from funciones f " +
            "inner join salas s on s.idsalas = f.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes")
    public List<SedeFiltro> listarSedesConFunciones();

    @Query(nativeQuery = true, value = "select se.idsedes as id, se.fotoprincipal as fotoprincipal, se.nombre as nombre, " +
            "sum(h.numtickets) as tickets, sum(h.total) as recaudacion " +
            "from historialcompras h " +
            "inner join funciones f on h.idfunciones = f.idfunciones and h.estado='Comprado' " +
            "inner join salas s on s.idsalas = f.idsalas " +
            "inner join sedes se on se.idsedes = s.idsedes " +
            "where TIMESTAMPDIFF(DAY, h.fechacompra, current_timestamp())<=30 " +
            "group by se.idsedes " +
            "order by tickets desc limit 4")
    public List<SedeEstadisticas> listarSedesPreferidas();
}
