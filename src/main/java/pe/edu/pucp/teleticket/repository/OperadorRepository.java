package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Operador;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    public Operador findByCorreo(String correo);
}
