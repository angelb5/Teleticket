package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    public Admin findByCorreo(String correo);
}
