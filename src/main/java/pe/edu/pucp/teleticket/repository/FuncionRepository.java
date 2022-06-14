package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.dto.FuncionOperadorDto;
import pe.edu.pucp.teleticket.dto.FuncionesCompra;
import pe.edu.pucp.teleticket.dto.ObrasListado;
import pe.edu.pucp.teleticket.dto.SedesCompra;
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
            value = "select * from funciones where idsalas = :idsalas and estado = 'Activa' and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW()))")
    public List<Funcion> getVigentesByIdsalas(int idsalas);

    @Query(nativeQuery = true,
            value = "SELECT max(stock) FROM funciones where idsalas = :idsalas and estado = 'Activa' and (f.fecha > NOW()  or (f.fecha = current_date()  and f.inicio>NOW()))")
    public long getMaxStockByIdsala(int idsalas);


    @Query(nativeQuery = true,
            value = "select * from funciones f " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "inner join sedes se on s.idsedes = se.idsedes " +
                    "where se.idsedes = :idsedes and " +
                    "f.estado = 'Activa' " +
                    "and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW()))")
    public List<Funcion> getVigentesByIdsedes(int idsedes);

    @Query(nativeQuery = true, value = "select * from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where s2.idsedes=?1 and funciones.idobras=?2",
            countQuery = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
                    "inner join sedes s2 on s.idsedes = s2.idsedes where s2.idsedes=?1 and funciones.idobras=?2")
    public List<Funcion> listaFuncionesPorSede(Integer idSede,Integer idObra, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where s2.idsedes=?1 and funciones.idobras=?2")
    public long contarListaFuncionesPorSede(Integer idSede, Integer idObra);

    @Query(nativeQuery = true, value = "select * from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and funciones.idobras=?2",
    countQuery = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and funciones.idobras=?2")
    public List<Funcion> listaFuncionesPorAno(Integer ano, Integer idObra, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and funciones.idobras=?2")
    public long contarListaFuncionesPorAno(Integer ano, Integer idObra);

    @Query(nativeQuery = true, value = "select * from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and month(funciones.fecha)=?2 and funciones.idobras=?3",
    countQuery = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and month(funciones.fecha)=?2 and funciones.idobras=?3")
    public List<Funcion> listaFuncionesPorAnoyMes(Integer ano,Integer mes, Integer idObra, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
                    "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and month(funciones.fecha)=?2 and funciones.idobras=?3")
    public long contarListaFuncionesPorAnoyMes(Integer ano,Integer mes, Integer idObra);

    @Query(nativeQuery = true, value = "select * from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and month(funciones.fecha)=?2 and\n" +
            "                            s.idsedes=?3 and funciones.idobras=?4",
    countQuery = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and month(funciones.fecha)=?2 and\n" +
            "                            s.idsedes=?3 and funciones.idobras=?4")
    public List<Funcion> listaFuncionesPorAnoyMesySede(Integer ano,Integer mes,Integer idSede, Integer idObra, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
                    "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and month(funciones.fecha)=?2 and\n" +
                    "                            s.idsedes=?3 and funciones.idobras=?4")
    public long contarListaFuncionesPorAnoyMesySede(Integer ano,Integer mes,Integer idSede, Integer idObra);

    @Query(nativeQuery = true, value = "select * from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and\n" +
            "                            s.idsedes=?2 and funciones.idobras=?3",
    countQuery = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
            "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and\n" +
            "                            s.idsedes=?2 and funciones.idobras=?3")
    public List<Funcion> listaFuncionesPorAnoySede(Integer ano, Integer sede, Integer idObra, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones inner join salas s on funciones.idsalas = s.idsalas\n" +
                    "inner join sedes s2 on s.idsedes = s2.idsedes where year(funciones.fecha)=?1 and\n" +
                    "                            s.idsedes=?2 and funciones.idobras=?3")
    public long contarListaFuncionesPorAnoySede(Integer ano, Integer sede, Integer idObra);

    @Query(nativeQuery = true, value = "select ss.idfunciones as id, f.idobras as idobras, o.fotoprincipal as fotoprincipal, o.duracion as duracion, " +
            "o.titulo as titulo, s.numero as nombresala, se.nombre as nombresede, " +
            "f.fecha as fecha, f.inicio as inicio, f.costo as costo, " +
            "MIN(disponible) as disponible from " +
            "(select (f.stock - sum(h.numtickets)) as disponible, f.idfunciones from historialcompras h " +
            "inner join funciones f on f.idfunciones = h.idfunciones " +
            "where (h.estado = 'Reserva' and h.fechalimite > NOW()) or h.estado = 'Comprado' " +
            "group by f.idfunciones " +
            "union " +
            "select maxreservas as disponible, idfunciones from funciones) " +
            "as ss " +
            "inner join funciones f on f.idfunciones = ss.idfunciones " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on se.idsedes = s.idsedes " +
            "where f.idobras=?1 and f.fecha=?2 and se.idsedes =?3 and f.estado = 'Activa' " +
            "group by ss.idfunciones " +
            "order by se.nombre, inicio")
    public List<FuncionesCompra> listaFuncionesCompraPorObraFechaSede(int idobra, LocalDate fecha, int idsede);

    @Query(nativeQuery = true, value = "select ss.idfunciones as id, f.idobras as idobras, o.fotoprincipal as fotoprincipal, o.duracion as duracion, " +
            "o.titulo as titulo, s.numero as nombresala, se.nombre as nombresede, " +
            "f.fecha as fecha, f.inicio as inicio, f.costo as costo, " +
            "MIN(disponible) as disponible from " +
            "(select (f.stock - sum(h.numtickets)) as disponible, f.idfunciones from historialcompras h " +
            "inner join funciones f on f.idfunciones = h.idfunciones " +
            "where (h.estado = 'Reserva' and h.fechalimite > NOW()) or h.estado = 'Comprado' " +
            "group by f.idfunciones " +
            "union " +
            "select maxreservas as disponible, idfunciones from funciones) " +
            "as ss " +
            "inner join funciones f on f.idfunciones = ss.idfunciones " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on se.idsedes = s.idsedes " +
            "where f.idfunciones=?1  and f.estado = 'Activa' and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW()))" +
            "group by ss.idfunciones")
    public FuncionesCompra getFuncionesCompraPorId(int idfunciones);

    @Query(value = "select distinct new pe.edu.pucp.teleticket.dto.SedesCompra(se.id, se.nombre, se.direccion) from " +
            "Funcion f " +
            "inner join Sala s on f.sala.id = s.id " +
            "inner join Sede se on se.id = s.sede.id " +
            "where f.obra.id=?1 and f.fecha=?2 and f.estado = 'Activa' ")
    public List<SedesCompra> listaSedesPorObraFecha(int idobra, LocalDate fecha);

    @Query(nativeQuery = true, value = "select distinct fecha from funciones where estado='Activa' and idobras=?1 and fecha >= current_date() " +
            "order by fecha asc")
    public List<String> listaFechasDeObra(int idobras);

    @Query(nativeQuery = true,
            value = "select f.idfunciones'id', o.titulo as 'obra', o.fotoprincipal as 'foto', f.fecha as 'fecha' ,f.inicio as 'Hora', se.nombre as 'sede' , f.estado as 'estado'\n" +
                    "            from funciones f\n" +
                    "                     left join obras o on f.idobras = o.idobras\n" +
                    "                     left join salas s on f.idsalas = s.idsalas\n" +
                    "                     left join sedes se on s.idsedes = se.idsedes\n" +
                    "            where o.titulo like %?1% \n" +
                    "            group by f.idfunciones",
            countQuery = "select count(distinct f.idfunciones)\n" +
                    "            from funciones f\n" +
                    "                     left join obras o on f.idobras = o.idobras\n" +
                    "                     left join salas s on f.idsalas = s.idsalas\n" +
                    "                     left join sedes se on s.idsedes = se.idsedes\n" +
                    "            where o.titulo like %?1%\n" +
                    "            group by f.idfunciones")
    public List<FuncionOperadorDto> listadoOperadorFunciones(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(distinct f.idfunciones)\n" +
            "            from funciones f\n" +
            "                     left join obras o on f.idobras = o.idobras\n" +
            "                     left join salas s on f.idsalas = s.idsalas\n" +
            "                     left join sedes se on s.idsedes = se.idsedes\n" +
            "            where o.titulo like %?1%\n")
    public Integer contarlistadoOperadorFunciones(String nombre);
}