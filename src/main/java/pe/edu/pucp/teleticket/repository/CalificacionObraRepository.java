package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.teleticket.entity.Calificacionobra;
import pe.edu.pucp.teleticket.entity.Obra;

import java.util.List;
import java.util.Optional;

public interface CalificacionObraRepository extends JpaRepository<Calificacionobra, Integer> {

    List<Obra> findByIdobras(int idobra);
    Integer calificaciones[] = {0, 1, 2, 3, 4, 5};

}