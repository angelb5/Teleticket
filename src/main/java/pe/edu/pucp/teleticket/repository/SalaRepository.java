package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Sala;
import pe.edu.pucp.teleticket.entity.Sede;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala,Integer> {
    List<Sala> findAllBySede(Sede sede, Pageable pageable);

    List<Sala> findAllBySede(Sede sede);
    public Optional<Sala> findById(Integer id);

    long countAllBySede(Sede Sede);
}
