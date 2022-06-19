package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.dto.ObraFiltro;
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
    public Integer contarListaObrasBusqueda(String nombre);

    @Query(nativeQuery = true, value = "select count(*)  from funciones f where (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) and f.idobras=?1")
    public long funcionesVigentes(Integer idObra);

    @Query(nativeQuery = true, value = "select count(*) from obras  where destacado = 1")
    public int obrasDestacadas();

    @Query(nativeQuery = true, value = "select s.idsedes as 'id', s.nombre as 'nombre' from sedes s inner join salas s2 on s.idsedes = s2.idsedes\n" +
            "inner join funciones f on s2.idsalas = f.idsalas where f.idobras=?1 group by s.idsedes;")
    public List<SedeFiltro> listarSedesSegunObra(Integer idObra);

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "where lower(o.titulo) like %?1% " +
                    "and f.estado='Activa' " +
                    "and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) " +
                    "        group by o.idobras",
            countQuery = "select count(distinct o.idobras) " +
                    "                    from obras o" +
                    "                    inner join funciones f on o.idobras = f.idobras " +
                    "                    where lower(o.titulo)  like %?1% " +
                    "                    and f.estado='Activa' " +
                    "                    and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) " +
                    "                    group by o.idobras")
    public List<ObrasListado> listadoObrasliente(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(distinct o.idobras) " +
            "from obras o " +
            "inner join funciones f on o.idobras = f.idobras " +
            "where lower(o.titulo) like %?1% and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) " +
            "group by o.idobras")
    public Integer contarListaObrasCliente(String nombre);


    @Query(nativeQuery = true, value = "select count(distinct o.idobras) " +
            "from obras o " +
            "inner join funciones f on o.idobras = f.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "where s.idsedes=?1 and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) " +
            "group by o.idobras")
    public Integer contarObrasClienteByIdsede(int idsede);

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "where s.idsedes=?1 " +
                    "and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) " +
                    "        group by o.idobras",
            countQuery = "select count(distinct o.idobras) " +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "where s.idsedes=?1 " +
                    "and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW())) " +
                    "group by o.idobras")
    public List<ObrasListado> listadoObraslienteByIdsede(int idsede, Pageable pageable);

    @Query(nativeQuery = true,
    value = "select o.idobras as id,  o.titulo as otitulo, o.fotoprincipal as fotoprincipal, if(timestamp(f.fecha,f.inicio)>=NOW(), min(f.costo),null)  as minprecio from actores a\n" +
            "inner join obras o on a.idobra = o.idobras\n" +
            "left join funciones f on f.idobras = o.idobras\n" +
            "where a.idpersona =?1 \n" +
            "group by o.idobras")
    public List<ObrasListado> findActuacionesByIdpersona(int id);

    @Query(nativeQuery = true,
    value = "select o.idobras as id,  o.titulo as otitulo, o.fotoprincipal as fotoprincipal, if(timestamp(f.fecha,f.inicio)>=NOW(), min(f.costo),null) as minprecio from directores d\n" +
            "inner join obras o on d.idobra = o.idobras\n" +
            "left join funciones f on f.idobras = o.idobras\n" +
            "where d.idpersona =?1 \n" +
            "group by o.idobras")
    public List<ObrasListado> findDireccionesByIdpersona(int id);

    @Query(nativeQuery = true, value = "select distinct o.idobras as id, o.titulo as titulo from funciones f " +
            "inner join obras o on o.idobras = f.idobras")
    public List<ObraFiltro> listarObrasConFunciones();
}
