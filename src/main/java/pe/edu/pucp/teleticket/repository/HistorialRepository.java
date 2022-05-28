package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
}
