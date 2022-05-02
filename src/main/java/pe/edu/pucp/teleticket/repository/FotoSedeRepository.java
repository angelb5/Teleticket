package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Fotossede;

import java.util.List;

@Repository
public interface FotoSedeRepository extends JpaRepository<Fotossede, Integer> {

    @Query(value = "select idfotossedes from fotossedes where idsedes=?1",nativeQuery = true)
    List<Integer> findAllIdByIdSedes(Integer idSede);
}
