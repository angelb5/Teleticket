package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.dto.*;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Obra;

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
            value = "select * from funciones where idsalas = :idsalas and estado = 'Activa' and (fecha > NOW()  or (fecha = current_date() and inicio>NOW()))")
    public List<Funcion> getVigentesByIdsalas(int idsalas);

    @Query(nativeQuery = true,
            value = "SELECT max(stock) FROM funciones where idsalas = :idsalas and estado = 'Activa' and (fecha > NOW()  or (fecha = current_date()  and inicio>NOW()))")
    public long getMaxStockByIdsala(int idsalas);


    @Query(nativeQuery = true,
            value = "select * from funciones f " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "inner join sedes se on s.idsedes = se.idsedes " +
                    "where se.idsedes = :idsedes and " +
                    "f.estado = 'Activa' " +
                    "and (f.fecha > NOW()  or (f.fecha = current_date() and f.inicio>NOW()))")
    public List<Funcion> getVigentesByIdsedes(int idsedes);

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
            "where ((h.estado = 'Reserva' and h.fechalimite > NOW()) or h.estado = 'Comprado') and h.idclientes<>?4 " +
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
    public List<FuncionesCompra> listaFuncionesCompraPorObraFechaSedeIdcliente(int idobra, LocalDate fecha, int idsede, int idcliente);


    @Query(nativeQuery = true, value = "select ss.idfunciones as id, f.idobras as idobras, o.fotoprincipal as fotoprincipal, o.duracion as duracion, " +
            "o.titulo as titulo, s.numero as nombresala, se.nombre as nombresede, " +
            "f.fecha as fecha, f.inicio as inicio, f.costo as costo, " +
            "MIN(disponible) as disponible from " +
            "(select (f.stock - sum(h.numtickets)) as disponible, f.idfunciones from historialcompras h " +
            "inner join funciones f on f.idfunciones = h.idfunciones " +
            "where ((h.estado = 'Reserva' and h.fechalimite > NOW()) or h.estado = 'Comprado' ) and h.idclientes<>?2 " +
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
    public FuncionesCompra getFuncionesCompraPorIdfuncionIdclientes(int idfunciones, int idcliente);

    @Query(value = "select distinct new pe.edu.pucp.teleticket.dto.SedesCompra(se.id, se.nombre, se.direccion) from " +
            "Funcion f " +
            "inner join Sala s on f.sala.id = s.id " +
            "inner join Sede se on se.id = s.sede.id " +
            "where f.obra.id=?1 and f.fecha=?2 and f.estado = 'Activa' ")
    public List<SedesCompra> listaSedesPorObraFecha(int idobra, LocalDate fecha);

    @Query(nativeQuery = true, value = "select distinct fecha from funciones where estado='Activa' and idobras=?1 and fecha >= current_date() " +
            "order by fecha asc")
    public List<String> listaFechasDeObra(int idobras);

    @Query(nativeQuery = true, value = "select f.idfunciones'id', o.titulo as 'obra', o.fotoprincipal as 'foto', f.fecha as 'fecha'," +
            "f.inicio as 'hora', se.nombre as 'sede', s.numero as 'sala', a.pasistencia as 'pasistencia', " +
            "if(f.estado<>'Cancelada',if(f.fecha>current_date() or (f.fecha=current_date() and f.inicio>now()),'Vigente','Realizada'), 'Cancelada') as 'estado' " +
            "from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "order by f.fecha desc, f.inicio desc",
    countQuery = "select count(*) from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% ")
    List<FuncionOperadorDto> listarFuncionesPorMes(String mes, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1%")
    Integer contarFuncionesPorMes(String mes);

    @Query(nativeQuery = true, value = "select f.idfunciones'id', o.titulo as 'obra', o.fotoprincipal as 'foto', f.fecha as 'fecha'," +
            "f.inicio as 'hora', se.nombre as 'sede' , s.numero as 'sala', a.pasistencia as 'pasistencia', " +
            "if(f.estado<>'Cancelada',if(f.fecha>current_date() or (f.fecha=current_date() and f.inicio>now()),'Vigente','Realizada'), 'Cancelada') as 'estado' " +
            "from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and se.idsedes=?2 " +
            "order by f.fecha desc, f.inicio desc",
    countQuery = "select count(*) from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and se.idsedes=?2 ")
    List<FuncionOperadorDto> listarFuncionesPorMesAndIdsede(String mes, int idsedes, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and se.idsedes=?2 " +
            "order by f.fecha desc, f.inicio desc")
    Integer contarFuncionesPorMesAndIdsede(String mes, int idsedes);

    @Query(nativeQuery = true, value = "select f.idfunciones'id', o.titulo as 'obra', o.fotoprincipal as 'foto', f.fecha as 'fecha'," +
            "f.inicio as 'hora', se.nombre as 'sede' , s.numero as 'sala', a.pasistencia as 'pasistencia', " +
            "if(f.estado<>'Cancelada',if(f.fecha>current_date() or (f.fecha=current_date() and f.inicio>now()),'Vigente','Realizada'), 'Cancelada') as 'estado' " +
            "from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and o.idobras=?2 " +
            "order by f.fecha desc, f.inicio desc",
            countQuery = "select count(*) from funciones f " +
                    "inner join obras o on f.idobras = o.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "inner join sedes se on s.idsedes = se.idsedes " +
                    "left join asistencia a on a.idfunciones = f.idfunciones " +
                    "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
                    "and o.idobras=?2 ")
    List<FuncionOperadorDto> listarFuncionesPorMesAndIdobra(String mes, int idobras, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and o.idobras=?2 ")
    Integer contarFuncionesPorMesAndIdobra(String mes, int idobras);

    @Query(nativeQuery = true, value = "select f.idfunciones'id', o.titulo as 'obra', o.fotoprincipal as 'foto', f.fecha as 'fecha'," +
            "f.inicio as 'hora', se.nombre as 'sede' , s.numero as 'sala', a.pasistencia as 'pasistencia', " +
            "if(f.estado<>'Cancelada',if(f.fecha>current_date() or (f.fecha=current_date() and f.inicio>now()),'Vigente','Realizada'), 'Cancelada') as 'estado' " +
            "from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and o.idobras=?2 " +
            "and se.idsedes=?3 " +
            "order by f.fecha desc, f.inicio desc",
            countQuery = "select count(*) from funciones f " +
                    "inner join obras o on f.idobras = o.idobras " +
                    "inner join salas s on f.idsalas = s.idsalas " +
                    "inner join sedes se on s.idsedes = se.idsedes " +
                    "left join asistencia a on a.idfunciones = f.idfunciones " +
                    "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
                    "and o.idobras=?2 " +
                    "and se.idsedes=?3")
    List<FuncionOperadorDto> listarFuncionesPorMesAndIdObraAndIdsede(String mes, int idobras, int idesede, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from funciones f " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "left join asistencia a on a.idfunciones = f.idfunciones " +
            "where concat(month(f.fecha),'-',year(f.fecha)) like %?1% " +
            "and o.idobras=?2 " +
            "and se.idsedes=?3 ")
    Integer contarFuncionesPorMesAndIdobraAndIdsede(String mes, int idobras, int idsede);

    @Query(nativeQuery = true, value = "select distinct concat(month(f.fecha),'-',year(f.fecha)) as mes " +
            "from funciones f")
    List<String> listarMesesConFunciones();

    @Query(nativeQuery = true, value = "select distinct concat(month(f.fecha),'-',year(f.fecha)) as mes " +
            "from funciones f " +
            "where f.idobras = ?1 ")
    List<String> listarMesesConFuncionesPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select f.idfunciones as id, s.numero as sala, se.nombre as sede, f.fecha as fecha, f.inicio as hora, " +
            "coalesce(sum(h.numtickets),0) as vendidos, " +
            "coalesce(sum(h.total),0) as recaudacion, " +
            "coalesce(count(distinct co.idcalificacion),0) as calificaciones, " +
            "coalesce(avg(co.estrellas),0) as puntuacion " +
            "from funciones f " +
            "inner join salas s on s.idsalas = f.idsalas " +
            "inner join sedes se on se.idsedes = s.idsedes " +
            "left join historialcompras h on h.idfunciones = f.idfunciones and h.estado='Comprado' " +
            "left join calificacionobras co on co.idfunciones = f.idfunciones " +
            "where f.idobras = :idobra " +
            "group by f.idfunciones " +
            "order by vendidos desc, fecha desc, hora desc " +
            "limit 1")
    Optional<FuncionEstadisticas> hallarFuncionMasVistaPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select f.idfunciones as id, s.numero as sala, se.nombre as sede, f.fecha as fecha, f.inicio as hora, " +
            "coalesce(sum(h.numtickets),0) as vendidos, " +
            "coalesce(sum(h.total),0) as recaudacion, " +
            "coalesce(count(distinct co.idcalificacion),0) as calificaciones, " +
            "coalesce(avg(co.estrellas),0) as puntuacion " +
            "from funciones f " +
            "inner join salas s on s.idsalas = f.idsalas " +
            "inner join sedes se on se.idsedes = s.idsedes " +
            "left join historialcompras h on h.idfunciones = f.idfunciones and h.estado='Comprado' " +
            "left join calificacionobras co on co.idfunciones = f.idfunciones " +
            "where f.idobras = :idobra " +
            "group by f.idfunciones " +
            "order by vendidos asc, fecha desc, hora desc " +
            "limit 1")
    Optional<FuncionEstadisticas> hallarFuncionMenosVistaPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select f.idfunciones as id, s.numero as sala, se.nombre as sede, f.fecha as fecha, f.inicio as hora, " +
            "coalesce(sum(h.numtickets),0) as vendidos, " +
            "coalesce(sum(h.total),0) as recaudacion, " +
            "coalesce(count(distinct co.idcalificacion),0) as calificaciones, " +
            "coalesce(avg(co.estrellas),0) as puntuacion " +
            "from funciones f " +
            "inner join salas s on s.idsalas = f.idsalas " +
            "inner join sedes se on se.idsedes = s.idsedes " +
            "left join historialcompras h on h.idfunciones = f.idfunciones and h.estado='Comprado' " +
            "left join calificacionobras co on co.idfunciones = f.idfunciones " +
            "where f.idobras = :idobra " +
            "group by f.idfunciones " +
            "order by puntuacion desc, fecha desc, hora desc " +
            "limit 1")
    Optional<FuncionEstadisticas> hallarFuncionMejorCalificadaPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from funciones " +
            "where idobras = ?1 ")
    public int contarFuncionesPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from funciones f " +
            "where timestamp(f.fecha,f.inicio)>=NOW() " +
            "and estado <> 'Cancelada' " +
            "and idobras = ?1 ")
    public int contarVigentesPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from funciones f " +
            "where timestamp(f.fecha,f.inicio)<NOW() " +
            "and estado <> 'Cancelada' " +
            "and idobras = ?1 ")
    public int contarRealizadasPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from funciones f " +
            "where estado = 'Cancelada' " +
            "and idobras = ?1 ")
    public int contarCanceladasPorIdobra(int idobra);

    @Query(nativeQuery = true, value = "select distinct f.* \n" +
            "from funciones f \n" +
            "inner join obras o on o.idobras = f.idobras\n" +
            "inner join actores ac on o.idobras = ac.idobra\n" +
            "inner join directores di on o.idobras = di.idobra\n" +
            "inner join personas p on ac.idpersona = p.idpersonas or di.idpersona = p.idpersonas\n" +
            "where timestamp(f.fecha, f.inicio)>=current_timestamp() and p.idpersonas=?1")
    public List<Funcion> listarVigentesPorIdpersona(int idpersona);

}