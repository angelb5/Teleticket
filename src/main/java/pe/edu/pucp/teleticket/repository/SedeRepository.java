package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Sede;


import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {

    List<Sede> findAllByOrderByIdAsc(Pageable pageable);
}
