package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.entity.Sede;
import pe.edu.pucp.teleticket.entity.SedeFiltro;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Integer> {
    List<Obra> findAllByOrderByIdAsc(Pageable pageable);

    @Query(nativeQuery = true, value = "select * from obras o where lower(o.titulo) like %?1% order by o.titulo asc",
            countQuery = "select count(*) from obras o where lower(o.titulo) like %?1% order by o.titulo asc")
    public List<Obra> listarObrasBusqueda(String nombre, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from obras o where lower(o.titulo) like %?1% order by o.titulo asc")
    public long contarListaObrasBusqueda(String nombre);

    @Query(nativeQuery = true, value = "select count(*)  from funciones f where datediff(f.fecha, now())>0 and f.idobras=?1")
    public long funcionesVigentes(Integer idObra);

    @Query(nativeQuery = true, value = "select count(*) from obras  where destacado = 1")
    public int obrasDestacadas();

    @Query(nativeQuery = true, value = "select year(fecha) from funciones where idobras=?1 order by fecha asc limit 0,1;")
    public Optional<Integer> obtenerPrimerAnoFuncion(Integer id);

    @Query(nativeQuery = true, value = "select s.idsedes as 'id', s.nombre as 'nombre' from sedes s inner join salas s2 on s.idsedes = s2.idsedes\n" +
            "inner join funciones f on s2.idsalas = f.idsalas where f.idobras=?1 group by s.idsedes;")
    public List<SedeFiltro> listarSedesSegunObra(Integer idObra);

}
