package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pucp.teleticket.dto.FuncionesCompra;
import pe.edu.pucp.teleticket.entity.Historial;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {

    @Query(value = "select new Historial(id, idclientes, idfunciones, numtickets,fechalimite,estado) from Historial " +
            "where idclientes = ?1 and fechalimite > current_timestamp and estado = 'Reserva' ")
    List<Historial> findReservasByIdclientes(int idclientes);

    @Query(value = "select new Historial(id, idclientes, idfunciones, numtickets,fechalimite,estado) from Historial " +
            "where idclientes = ?1 and idfunciones = ?2 and fechalimite > current_timestamp and estado = 'Reserva' ")
    Optional<Historial> findReservaByIdclientesAndIdfunciones(int idclientes, int idfunciones);

    @Query(value = "select new Historial(id, idclientes, idfunciones, numtickets,fechalimite,estado) from Historial " +
            "where idclientes = ?1 and idfunciones = ?2 and estado = 'Comprado' ")
    Optional<Historial> findCompraByIdclientesAndIdfunciones(int idclientes, int idfunciones);

    @Query(nativeQuery = true, value = "select f.idfunciones as id, f.idobras as idobras, o.fotoprincipal as fotoprincipal, o.duracion as duracion, " +
            "o.titulo as titulo, 0 as nombresala, se.nombre as nombresede, " +
            "f.fecha as fecha, f.inicio as inicio, f.costo as costo, " +
            "0 as disponible " +
            "from historialcompras h " +
            "inner join funciones f on h.idfunciones = f.idfunciones " +
            "inner join obras o on f.idobras = o.idobras " +
            "inner join salas s on f.idsalas = s.idsalas " +
            "inner join sedes se on s.idsedes = se.idsedes " +
            "where h.idclientes=:idclientes and f.fecha=:fecha " +
            "and (CAST(:inicio AS time) between f.inicio and f.fin or " +
            "CAST(:fin AS time) between f.inicio and f.fin) " +
            "and ((h.estado = 'Reserva' and h.fechalimite > NOW()) or h.estado = 'Comprado') " +
            "and f.idfunciones <> :idfunciones")
    List<FuncionesCompra> findHistorialEnConflicto(int idfunciones, int idclientes, LocalDate fecha, LocalTime inicio, LocalTime fin);

    @Query(nativeQuery = true, value = "select count(*) from historialcompras h " +
            "inner join funciones f on f.idfunciones = h.idfunciones " +
            "where h.idclientes = :idclientes and h.estado='Comprado' and (f.fecha > NOW()  or (f.fecha = current_date() and f.fin>=NOW()))")
    public long contarComprasVigentes(int idclientes);

    @Query(nativeQuery = true, value = "select count(*) from historialcompras h " +
            "inner join funciones f on f.idfunciones = h.idfunciones " +
            "where h.idclientes = :idclientes and h.estado='Comprado' and (f.fecha < NOW() or (f.fecha = current_date() and f.fin<NOW()))")
    public long contarComprasAsistidas(int idclientes);

    @Query(value = "select new Historial(h.id, h.idclientes, h.funcion, h.idcompra, h.total, h.numtickets, h.fechacompra) " +
            "from Historial h " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where h.idclientes = :idclientes and h.estado = 'Comprado' and (f.fecha > current_date or (f.fecha = current_date  and f.fin>=current_time)) " +
            "order by h.fechacompra desc",
    countQuery = "select count(h) " +
            " from Historial h  " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where h.idclientes = :idclientes and h.estado = 'Comprado' and (f.fecha > current_date or (f.fecha = current_date  and f.fin>= current_time))")
    List<Historial> findComprasVigentes(int idclientes, Pageable pageable);

    @Query(value = "select new Historial(h.id, h.idclientes, h.funcion, h.idcompra, h.total, h.numtickets, h.fechacompra) " +
            "from Historial h " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where h.idclientes = :idclientes and h.estado = 'Comprado' and (f.fecha < current_date or (f.fecha = current_date  and f.fin < current_time)) " +
            "order by h.fechacompra desc",
            countQuery = "select count(h) " +
                    " from Historial h  " +
                    "inner join Funcion f on f.id = h.funcion.id " +
                    "where h.idclientes = :idclientes and h.estado = 'Comprado' and (f.fecha < current_date or (f.fecha = current_date  and f.fin < current_time)")
    List<Historial> findComprasAsistidas(int idclientes, Pageable pageable);

    @Query(value = "select new Historial(h.id, h.idclientes, h.funcion, h.idcompra, h.total, h.numtickets, h.fechacompra) " +
            "from Historial h " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where h.idclientes = :idclientes and h.idcompra= :idcompra and h.estado = 'Comprado' and (f.fecha > current_date or (f.fecha = current_date  and f.fin>=current_time))")
    Historial findVigenteById(int idclientes, String idcompra);

    @Query(value = "select new Historial(h.id, h.idclientes, h.funcion, h.idcompra, h.total, h.numtickets, h.fechacompra) " +
            "from Historial h " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where h.idclientes = :idclientes and h.idcompra= :idcompra and h.estado = 'Comprado' and (f.fecha < current_date or (f.fecha = current_date  and f.fin < current_time))")
    Historial findAsistidaById(int idclientes, String idcompra);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update historialcompras set estado='Cancelado' where idcompra = ?1")
    void cancelarCompra(String idcompra);

    @Query(value = "select new Historial(h.id, h.idclientes, h.funcion, h.idcompra, h.total, h.numtickets, h.fechacompra) " +
            "from Historial h " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where h.idclientes = :idclientes and f.id = :idfunciones and h.estado = 'Comprado' and (f.fecha < current_date or (f.fecha = current_date  and f.fin < current_time))")
    Historial findAsistidaByIdFuncion(int idclientes, int idfunciones);

    @Query(value = "select new Historial(h.id, h.idclientes, h.funcion, h.idcompra, h.total, h.numtickets, h.fechacompra) " +
            "from Historial h " +
            "inner join Funcion f on f.id = h.funcion.id " +
            "where f.id = :idfunciones and h.estado = 'Comprado' " +
            "order by h.fechacompra")
    List<Historial> findComprasByIdfunciones(int idfunciones);
}
