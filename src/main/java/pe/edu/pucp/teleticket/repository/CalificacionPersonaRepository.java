package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.teleticket.entity.Calificacionpersona;
import pe.edu.pucp.teleticket.entity.Persona;

import java.util.List;

public interface CalificacionPersonaRepository extends JpaRepository<Calificacionpersona, Integer> {

    List<Persona> findByIdpersonas(int idpersona);
    Integer calificaciones[] = {0, 1, 2, 3, 4, 5};
    String roles[] = {"Director(a)", "Actor/actriz"};
}