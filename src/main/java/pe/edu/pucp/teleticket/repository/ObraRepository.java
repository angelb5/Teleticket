package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Obra;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Integer> {
}
