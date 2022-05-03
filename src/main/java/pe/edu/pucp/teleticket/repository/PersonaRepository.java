package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Persona;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Integer> {
    public List<Persona> findAllByEstadoEquals(String estado);

    @Query(nativeQuery = true,
            value = "select * from personas p\n" +
                    "inner join personal pl on p.idpersonas=pl.idpersonas\n" +
                    "inner join obras o on o.idobras = pl.idobras\n" +
                    "where pl.rol = 'Actuacion' and o.idobras = ?1")
    public List<Persona> findActoresByIdObra(int idobras);

    @Query(nativeQuery = true,
            value = "select * from personas p\n" +
                    "inner join personal pl on p.idpersonas=pl.idpersonas\n" +
                    "inner join obras o on o.idobras = pl.idobras\n" +
                    "where pl.rol = 'Direccion' and o.idobras = ?1")
    public List<Persona> findDirectoresByIdObra(int idobras);
}
