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
            value = "select * from personas p inner join actores a on p.idpersonas = a.idpersona where idobra = ?1")
    public List<Persona> findActoresByIdObra(int idobras);

    @Query(nativeQuery = true,
            value = "select * from personas p inner join directores d on p.idpersonas = d.idpersona where idobra = ?1")
    public List<Persona> findDirectoresByIdObra(int idobras);
}
