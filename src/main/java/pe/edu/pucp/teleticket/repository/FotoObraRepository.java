package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Fotosobra;

import java.util.List;

@Repository
public interface FotoObraRepository extends JpaRepository<Fotosobra, Integer> {
    @Query(value = "select idfotos from fotosobras where idobras=?1",nativeQuery = true)
    List<Integer> findAllIdByIdObras(Integer idObras);
}
