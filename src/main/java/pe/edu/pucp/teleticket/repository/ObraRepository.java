package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.dto.ObrasListado;
import pe.edu.pucp.teleticket.entity.Obra;
import pe.edu.pucp.teleticket.dto.SedeFiltro;

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

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "where lower(o.titulo) like %?1% " +
                    "and f.fecha > NOW() " +
                    "        group by o.idobras",
            countQuery = "select count(distinct o.idobras) " +
                    "                    from obras o" +
                    "                    inner join funciones f on o.idobras = f.idobras " +
                    "                    where lower(o.titulo)  like %?1% " +
                    "                    and f.fecha > NOW() " +
                    "                    group by o.idobras")
    public List<ObrasListado> listadoObrasliente(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(distinct o.idobras) " +
            "from obras o " +
            "inner join funciones f on o.idobras = f.idobras " +
            "where lower(o.titulo) like %?1% and f.fecha > NOW() " +
            "group by o.idobras")
    public long contarListaObrasCliente(String nombre);


    @Query(nativeQuery = true, value = "select count(distinct o.idobras) " +
            "from obras o " +
            "inner join funciones f on o.idobras = f.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "where s.idsedes=?1 and f.fecha > NOW() " +
            "group by o.idobras")
    public Integer contarObrasClienteByIdsede(int idsede);

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "where s.idsedes=?1 " +
                    "and f.fecha > NOW() " +
                    "        group by o.idobras",
            countQuery = "select count(distinct o.idobras) " +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "where s.idsedes=?1 " +
                    "and f.fecha > NOW() " +
                    "group by o.idobras")
    public List<ObrasListado> listadoObraslienteByIdsede(int idsede, Pageable pageable);

}
