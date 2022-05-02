package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Funcion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion,Integer > {
    @Query(nativeQuery = true,
            value="select * from funciones \n" +
                    "where fecha = \"2022-08-30\" and estado = 'Activa' and \n" +
                    "idobras = 1 and\n" +
                    "CAST('18:00:00' AS time) between inicio and fin or\n" +
                    "CAST('19:30:00' AS time) between inicio and fin\n" +
                    "union\n" +
                    "select * from funciones \n" +
                    "where fecha = \"2022-08-30\" and estado = 'Activa' and \n" +
                    "idsalas = 1 and\n" +
                    "CAST('18:00:00' AS time) between inicio and fin or\n" +
                    "CAST('19:30:00' AS time) between inicio and fin")
    public List<Funcion> findFuncionesEnConflicto(LocalDate fecha, LocalTime inicio, LocalTime fin, int idobras, int idsalas);
}
