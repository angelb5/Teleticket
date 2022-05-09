package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Sede;


import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {

    List<Sede> findAllByOrderByIdAsc(Pageable pageable);


    @Query(nativeQuery = true, value = "select * from sedes where lower(nombre) like %?1% or lower(direccion) like %?1% order by nombre asc",
    countQuery = "select count(*) from sedes where lower(nombre) like %?1% or lower(direccion) like %?1% order by nombre asc")
    public List<Sede> listarSedesBusqueda(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value= "select count(*) from sedes where lower(nombre) like %?1% or lower(direccion) like %?1% order by nombre asc")
    public long contarListarSedesBusqueda(String busqueda);
}
