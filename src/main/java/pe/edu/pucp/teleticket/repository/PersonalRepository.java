package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Personal;
import pe.edu.pucp.teleticket.entity.PersonalKey;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, PersonalKey> {
}
