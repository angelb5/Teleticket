package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pucp.teleticket.entity.Operador;


import java.util.List;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Integer> {

    public Operador findByCorreo(String correo);

    public Operador findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE Operador o SET o.token = :token where o.correo = :correo")
    void updateToken(@Param("token") String token, @Param("correo") String correo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value= "CREATE EVENT delete_token_op_:id " +
            "ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL :minutos MINUTE " +
            "ON COMPLETION NOT PRESERVE ENABLE " +
            "DO update operadores set token=null WHERE idoperadores=:id")
    void crearEvento(int id, int minutos);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value= "DROP EVENT IF EXISTS delete_token_op_:id ")
    void dropEvento(int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update operadores set contrasena = :contrasena where idoperadores = :id")
    void updateContrasena(String contrasena, int id);

    @Query(nativeQuery = true,value = "select * from operadores o where LOWER(o.nombre) like %?1% order by nombre asc",
    countQuery = "select count(*) from operadores o where LOWER(o.nombre) like %?1% order by nombre asc")
    public List<Operador> listarOperadores(String nombre, Pageable paginado);

    @Query(nativeQuery = true, value = "select count(*) from operadores o where LOWER(o.nombre) like %?1% ")
     public long contarListaOperadores(String nombre);




}
