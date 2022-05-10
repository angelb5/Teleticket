package pe.edu.pucp.teleticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pucp.teleticket.entity.Cliente;

import org.springframework.data.domain.Pageable;
import pe.edu.pucp.teleticket.dto.ClienteListado;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    public Cliente findByCorreo(String correo);

    public Cliente findByDni(String dni);

    List<Cliente> findAllByOrderByIdAsc(Pageable pageable);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT IGNORE INTO clientes (`dni`, `nombre`, `apellido`, `correo`, `contrasena`, `celular`, `nacimiento`, `direccion`) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8) "
    )
    void registrarUsuario(String dni, String nombre, String apellido, String correo, String contrasena, String celular, LocalDate nacimiento, String direccion);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Cliente c SET c.foto = :foto where c.id = :id")
    void updateFoto(@Param("foto") byte[] foto, @Param("id") int id);

    @Query(nativeQuery = true, value = "select c.nombre as 'nombre', c.apellido as 'apellido', DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.nacimiento)), '%Y')+0 as 'edad',\n" +
            "       c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "    where lower(concat(c.nombre,' ',c.apellido)) like %?1% order by apellido asc",
    countQuery = "select count(*) from clientes c\n" +
            "    where lower(concat(c.nombre,' ',c.apellido)) like %?1% order by apellido asc")
    public List<ClienteListado> listarClientes(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from clientes c\n" +
                    "    where lower(concat(c.nombre,' ',c.apellido)) like %?1% order by apellido asc")
    long contarListarClientes(String busqueda);

    @Query(nativeQuery = true, value = "select c.nombre as 'nombre', c.apellido as 'apellido', DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.nacimiento)), '%Y')+0 as 'edad',\n" +
            "       c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "       inner join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "       inner join calificacionpersonas c3 on c.idclientes = c3.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc",
    countQuery = "select count(*) from (select c.nombre from clientes c\n" +
            "       inner join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "       inner join calificacionpersonas c3 on c.idclientes = c3.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc) contar ")
    public List<ClienteListado> listarClientesConCriticas(String busqueda, Pageable page);

    @Query(nativeQuery = true, value = "select count(*) from (select c.nombre from clientes c\n" +
            "       inner join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "       inner join calificacionpersonas c3 on c.idclientes = c3.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc) contar")
    public long contarClientesConCriticas(String busqueda);

    @Query(nativeQuery = true, value = "select c.nombre as 'nombre', c.apellido as 'apellido', DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.nacimiento)), '%Y')+0 as 'edad',\n" +
            "       c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "       inner join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "       inner join calificacionpersonas c3 on c.idclientes = c3.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc",
    countQuery = "select count(*) from (select c.nombre from clientes c\n" +
            "       inner join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "       inner join calificacionpersonas c3 on c.idclientes = c3.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc) contar")
    public List<ClienteListado> listarClientesConCompras(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from (select c.nombre from clientes c\n" +
            "       inner join calificacionobras c2 on c.idclientes = c2.idclientes\n" +
            "       inner join calificacionpersonas c3 on c.idclientes = c3.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc) contar ")
    public long contarClientesConCompras(String busqueda);

    @Query(nativeQuery = true, value = "select c.nombre as 'nombre', c.apellido as 'apellido', DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.nacimiento)), '%Y')+0 as 'edad',\n" +
            "       c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "       left join historialcompras h on c.idclientes = h.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% and h.idcompra is null order by apellido asc",
    countQuery = "select count(*) from clientes c\n" +
            "       left join historialcompras h on c.idclientes = h.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% and h.idcompra is null order by apellido asc")
    public List<ClienteListado> listarClientesSinHistorial(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = " select count(*) from clientes c\n" +
            "       left join historialcompras h on c.idclientes = h.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% and h.idcompra is null order by apellido asc")
    public long contarClientesSinHistorial(String busqueda);
}
