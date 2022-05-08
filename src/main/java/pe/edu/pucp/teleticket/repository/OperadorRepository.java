package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Operador;


import java.util.List;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    public Operador findByCorreo(String correo);

    @Query(nativeQuery = true,value = "select * from operadores o where LOWER(o.nombre) like %?1% order by nombre asc",
    countQuery = "select count(*) from operadores o where LOWER(o.nombre) like %?1% order by nombre asc")
    public List<Operador> listarOperadores(String nombre, Pageable paginado);

    @Query(nativeQuery = true, value = "select count(*) from operadores o where LOWER(o.nombre) like %?1% ")
     public long contarListaOperadores(String nombre);

}
