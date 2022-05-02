package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.Sala;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala,Integer> {
    List<Sala> findAllByIdsedes(Integer Idsedes,Pageable pageable);

    long countAllByIdsedes(Integer Idsedes);
}
