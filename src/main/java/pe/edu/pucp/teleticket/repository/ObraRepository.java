package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    @Query(nativeQuery = true, value = "select count(*)  from funciones f where timestamp(f.fecha,f.inicio)>= current_timestamp() and f.idobras=?1")
    public long funcionesVigentes(Integer idObra);

    @Query(nativeQuery = true, value = "select count(distinct o.idobras) from obras o " +
            "inner join funciones f on f.idobras = o.idobras " +
            "where f.estado='Activa' " +
            "and o.destacado = 1 and timestamp(f.fecha,f.inicio)>= current_timestamp() ")
    public int obrasDestacadas();

    @Query(nativeQuery = true, value = "select distinct se.idsedes as 'id', se  .nombre as 'nombre' from funciones f " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "where f.idobras=?")
    public List<SedeFiltro> listarSedesSegunObra(Integer idObra);

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "where lower(o.titulo) like %?1% " +
                    "and f.estado='Activa' " +
                    "and timestamp(f.fecha,f.inicio)>= current_timestamp() " +
                    "        group by o.idobras",
            countQuery = "select count(distinct o.idobras) " +
                    "                    from obras o" +
                    "                    inner join funciones f on o.idobras = f.idobras " +
                    "                    where lower(o.titulo)  like %?1% " +
                    "                    and f.estado='Activa' " +
                    "                    and timestamp(f.fecha,f.inicio)>= current_timestamp() ")
    public List<ObrasListado> listadoObrasCliente(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(distinct o.idobras) " +
            "from obras o " +
            "inner join funciones f on o.idobras = f.idobras " +
            "where lower(o.titulo) like %?1% and f.estado='Activa' and  timestamp(f.fecha,f.inicio)>= current_timestamp() ")
    public Integer contarListaObrasCliente(String nombre);


    @Query(nativeQuery = true, value = "select count(distinct o.idobras) " +
            "from obras o " +
            "inner join funciones f on o.idobras = f.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "where s.idsedes=?1 and f.estado='Activa' and timestamp(f.fecha,f.inicio)>= current_timestamp() ")
    public Integer contarObrasClienteByIdsede(int idsede);

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "where s.idsedes=?1 " +
                    "and f.estado='Activa' " +
                    "and timestamp(f.fecha,f.inicio)>= current_timestamp() " +
                    "        group by o.idobras",
            countQuery = "select count(distinct o.idobras) " +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "where s.idsedes=?1 " +
                    "and f.estado='Activa' " +
                    "and timestamp(f.fecha,f.inicio)>= current_timestamp() ")
    public List<ObrasListado> listadoObrasClienteByIdsede(int idsede, Pageable pageable);

    @Query(nativeQuery = true,
    value = "select o.idobras as id,  o.titulo as otitulo, o.fotoprincipal as fotoprincipal, min(f.costo) as minprecio\n" +
            "from obras o\n" +
            "inner join actores a on a.idobra = o.idobras and a.idpersona = ?1 \n" +
            "left join funciones f on f.idobras = o.idobras and timestamp(f.fecha,f.inicio)>=current_timestamp() and f.estado='Activa'\n" +
            "group by o.idobras")
    public List<ObrasListado> findActuacionesByIdpersona(int id);

    @Query(nativeQuery = true,
    value = "select o.idobras as id,  o.titulo as otitulo, o.fotoprincipal as fotoprincipal, min(f.costo) as minprecio\n" +
            "from obras o\n" +
            "inner join directores d on d.idobra = o.idobras and d.idpersona = ?1 \n" +
            "left join funciones f on f.idobras = o.idobras and timestamp(f.fecha,f.inicio)>=current_timestamp() and f.estado='Activa'\n" +
            "group by o.idobras")
    public List<ObrasListado> findDireccionesByIdpersona(int id);

    @Query(nativeQuery = true, value = "select distinct o.idobras as id, o.titulo as titulo from funciones f " +
            "inner join obras o on o.idobras = f.idobras")
    public List<ObraFiltro> listarObrasConFunciones();

    @Query(nativeQuery = true, value = "select distinct o.* from funciones f " +
            "inner join obras o on o.idobras = f.idobras " +
            "where f.estado='Activa' " +
            "and timestamp(f.fecha,f.inicio)>= current_timestamp() ")
    public List<Obra> listarObrasConFuncionesVigentes();

    @Query(nativeQuery = true, value = "select distinct o.* from funciones f " +
            "inner join obras o on o.idobras = f.idobras " +
            "where f.estado='Activa' " +
            "and timestamp(f.fecha,f.inicio)>= current_timestamp()" +
            "and o.destacado=0 ")
    public List<Obra> listarObrasConFuncionesVigentesNoDestacadas();

    @Query(nativeQuery = true, value = "select sum(h.total) " +
            "from historialcompras h " +
            "inner join funciones f on h.idfunciones = f.idfunciones " +
            "where h.estado='Comprado' and f.idobras = ?1 ")
    public Optional<Float> obtenerRecaudaciontotalPorIdobra(int idobra);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value ="update funciones set fin=fin + interval ?2 minute where idobras=?1 and timestamp(fecha,inicio)>= current_timestamp()" )
    public void actualizarHorarios(int idobra, int timeMinutos);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update obras o " +
            "set o.destacado=0 " +
            "where o.idobras not in (select distinct idobras from funciones f " +
            "where f.estado = 'Activa' " +
            "and timestamp(f.fecha,f.inicio)>=current_timestamp())")
    public void actualizarDestacadas();

    @Query(nativeQuery = true,
            value = "select min(timestampdiff(minute,f1.fin,f2.inicio))\n" +
                    "from funciones f1\n" +
                    "    inner join funciones f2 on f1.idsalas=f2.idsalas and f1.fecha=f2.fecha and f2.idfunciones!=f1.idfunciones and f2.inicio>=f1.fin\n" +
                    "where f1.idobras = ?1" )
    public Integer maxDiffTiempoHorario(int idobra);

    @Query(nativeQuery = true,
            value = "select o.idobras 'id', o.titulo as 'otitulo' , o.fotoprincipal as 'fotoprincipal', min(f.costo) as 'minprecio'\n" +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "where f.estado='Activa' " +
                    "and timestamp(f.fecha,f.inicio)>= current_timestamp() " +
                    "and o.destacado=1 " +
                    "group by o.idobras " +
                    "limit 8")
    public List<ObrasListado> listarObrasDestacadas();

    @Query(nativeQuery = true,
            value = "select o.* " +
                    "from obras o " +
                    "inner join funciones f on o.idobras = f.idobras " +
                    "where f.estado='Activa' " +
                    "and timestamp(f.fecha,f.inicio)>= current_timestamp() " +
                    "and o.destacado=1 " +
                    "group by o.idobras " +
                    "limit 8")
    public List<Obra> listarObrasDestacadasOperador();

    @Query(nativeQuery = true,value = "select o.*\n" +
            "from obras o\n" +
            "         inner join funciones f on o.idobras = f.idobras and timestamp(f.fecha,f.inicio)>= current_timestamp()\n" +
            "inner join (select idfunciones, sum(numtickets) as 'vendidos'\n" +
            "            from historialcompras\n" +
            "            where estado = 'Comprado'\n" +
            "            group by idfunciones) f2 on f.idfunciones=f2.idfunciones\n" +
            "where f2.vendidos/f.stock >= 0.85\n" +
            "group by f.idobras limit 0,4;")
    public List<Obra> listarUltimasObras();
}
