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
public interface  ClienteRepository extends JpaRepository<Cliente, Integer> {

    @Query("select new Cliente(c.id, c.dni, c.nombre, c.apellido, c.correo, c.celular, c.nacimiento, c.direccion) from Cliente c where c.correo = ?1")
    Cliente findByCorreo(String correo);

    public Cliente findByDni(String dni);

    public  Cliente findByToken(String token);

    List<Cliente> findAllByOrderByIdAsc(Pageable pageable);
    @Query(value = "select count(*) from clientes",nativeQuery = true)
    Integer totalCliente();

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT IGNORE INTO clientes (`dni`, `nombre`, `apellido`, `correo`, `contrasena`, `celular`, `nacimiento`, `direccion`) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8) "
    )
    void registrarUsuario(String dni, String nombre, String apellido, String correo, String contrasena, String celular, LocalDate nacimiento, String direccion);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update clientes set contrasena = :contrasena where idclientes = :id")
    void updateContrasena(String contrasena, int id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Cliente c SET c.foto = :foto where c.id = :id")
    void updateFoto(@Param("foto") byte[] foto, @Param("id") int id);

    @Transactional
    @Modifying
    @Query("UPDATE Cliente c SET c.nacimiento = :nacimiento, c.celular = :celular, c.direccion = :direccion where c.id = :id")
    void updateCliente(@Param("nacimiento") LocalDate nacimiento, @Param("celular") String celular, @Param("direccion") String direccion, @Param("id") int id);

    @Transactional
    @Modifying
    @Query("UPDATE Cliente c SET c.token = :token where c.correo = :correo")
    void updateToken(@Param("token") String token, @Param("correo") String correo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value= "CREATE EVENT delete_token_:id " +
            "ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL :minutos MINUTE " +
            "ON COMPLETION NOT PRESERVE ENABLE " +
            "DO update clientes set token=null WHERE idclientes=:id")
    void crearEvento(int id, int minutos);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value= "DROP EVENT IF EXISTS delete_token_:id ")
    void dropEvento(int id);

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
            "                  c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "                  inner join (select idclientes from calificacionobras union select idclientes from calificacionpersonas) c2 on c.idclientes = c2.idclientes\n" +
            "                  where lower(concat(c.nombre,' ',c.apellido)) like '%%' group by c.idclientes order by apellido asc",
    countQuery = "select count(*) from (select c.nombre from clientes c\n" +
            "       inner join (select idclientes from calificacionobras union select idclientes from calificacionpersonas) c2 on c.idclientes = c2.idclientes\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% group by c.idclientes order by apellido asc) contar ")
    public List<ClienteListado> listarClientesConCriticas(String busqueda, Pageable page);

    @Query(nativeQuery = true, value = "select count(*) from clientes c\n" +
            "                  inner join (select idclientes from calificacionobras union select idclientes from calificacionpersonas) c2 on c.idclientes = c2.idclientes\n" +
            "                  where lower(concat(c.nombre,' ',c.apellido)) like '%%' group by c.idclientes order by apellido asc")
    public long contarClientesConCriticas(String busqueda);

    @Query(nativeQuery = true, value = "select c.nombre as 'nombre', c.apellido as 'apellido', DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.nacimiento)), '%Y')+0 as 'edad',\n" +
            "       c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "inner join historialcompras c2 on c.idclientes = c2.idclientes  \n" +
            "where lower(concat(c.nombre,' ',c.apellido)) like %?1% and c2.estado='Comprado' group by c.idclientes order by 'apellido' asc",
    countQuery = "select count(*)\n" +
            "inner join historialcompras c2 on c.idclientes = c2.idclientes\n" +
            "where lower(concat(c.nombre,' ',c.apellido)) like %?1% and c2.estado='Comprado' group by c.idclientes order by 'apellido' asc")
    public List<ClienteListado> listarClientesConCompras(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(distinct c.idclientes) from clientes c\n" +
            "inner join historialcompras c2 on c.idclientes = c2.idclientes\n" +
            "where lower(concat(c.nombre,' ',c.apellido)) like %?1% and c2.estado='Comprado' order by 'apellido' asc")
    public long contarClientesConCompras(String busqueda);

    @Query(nativeQuery = true, value = "select c.nombre as 'nombre', c.apellido as 'apellido', DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.nacimiento)), '%Y')+0 as 'edad',\n" +
            "       c.correo as 'correo', c.celular as 'celular'  from clientes c\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% and c.idclientes not in (\n" +
            "           select h.idclientes from historialcompras h where estado='Comprado')",
    countQuery = "select count(*) from clientes c\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% and c.idclientes not in (\n" +
            "           select h.idclientes from historialcompras h where estado='Comprado')")
    public List<ClienteListado> listarClientesSinHistorial(String busqueda, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from clientes c\n" +
            "       where lower(concat(c.nombre,' ',c.apellido)) like %?1% and c.idclientes not in (\n" +
            "           select h.idclientes from historialcompras h where estado='Comprado')")
    public long contarClientesSinHistorial(String busqueda);

    @Query(nativeQuery = true, value = "select contrasena from clientes " +
            "where idclientes = :idclientes")
    public String getContrasenaById(int idclientes);
}
