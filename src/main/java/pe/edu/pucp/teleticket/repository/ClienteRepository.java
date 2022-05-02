package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Cliente;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    public Cliente findByCorreo(String correo);

    List<Cliente> findAllByOrderByIdAsc(Pageable pageable);

}
