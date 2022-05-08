package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.teleticket.entity.Cliente;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    public Cliente findByCorreo(String correo);

    public Cliente findByDni(String dni);

    List<Cliente> findAllByOrderByIdAsc(Pageable pageable);

    @Query(nativeQuery = true, value = "select * from clientes c\n" +
            "right join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "         where CONCAT(c.nombre,' ',c.apellido) like %?1%\n" +
            "group by c.idclientes",
            countQuery = "select count(*) from clientes c\n" +
                    "right join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
                    "         where CONCAT(c.nombre,' ',c.apellido) like %?1%" +
                    "group by c.idclientes;")
    public List<Cliente> listarClientesConCriticas(String busqueda, Pageable page);


    @Query(nativeQuery = true, value = "select * from clientes c\n" +
            "right join historialcompras h on c.idclientes = h.idclientes\n" +
            "where h.estado='Comprado' and (CONCAT(c.nombre,' ',c.apellido) like %?1%)\n" +
            "group by c.idclientes",
            countQuery = "select count(*) from clientes c\n" +
                    "right join historialcompras h on c.idclientes = h.idclientes\n" +
                    "where h.estado='Comprado' and (CONCAT(c.nombre,' ',c.apellido) like %?1%)\n" +
                    "group by c.idclientes;")
    public List<Cliente> listarClientesConCompras(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from clientes c\n" +
            "left join historialcompras h on c.idclientes = h.idclientes\n" +
            "where h.idcompra is null and (CONCAT(c.nombre,' ',c.apellido) like %?1%)\n" +
            "group by c.idclientes",
            countQuery = "select count(*) from clientes c\n" +
                    "left join historialcompras h on c.idclientes = h.idclientes\n" +
                    "where h.idcompra is null and (CONCAT(c.nombre,' ',c.apellido) like %?1%)\n" +
                    "group by c.idclientes")
    public List<Cliente> listarClientesSinHistorial(String busqueda, Pageable pageable);
}
