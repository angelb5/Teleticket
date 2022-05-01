package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Persona;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Integer> {
    public List<Persona> findAllByEstadoEquals(String estado);
}
