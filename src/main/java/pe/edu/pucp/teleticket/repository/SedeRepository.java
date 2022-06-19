package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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


    @Query(nativeQuery = true, value = "select count(*)from sedes where estado = 'Disponible'")
    public int totalSedesDisponibles();

    @Query(nativeQuery = true, value = "select distinct se.idsedes as id, se.nombre as nombre from funciones f " +
            "inner join salas s on s.idsalas = f.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes")
    public List<SedeFiltro> listarSedesConFunciones();
}
