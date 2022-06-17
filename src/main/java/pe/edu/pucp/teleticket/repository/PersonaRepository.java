package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pucp.teleticket.dto.PersonasListado;
import pe.edu.pucp.teleticket.entity.Funcion;
import pe.edu.pucp.teleticket.entity.Persona;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Integer> {
    public List<Persona> findAllByEstadoEquals(String estado);

    public Optional<Persona> findById(Integer id);
    @Query(value = "select (select count(*) from actores) + (select count(*) from directores)",nativeQuery = true)
    Integer totalActoresDirectores();

    @Query(nativeQuery = true,
            value = "select * from personas p inner join actores a on p.idpersonas = a.idpersona where idobra = ?1")
    public List<Persona> findActoresByIdObra(int idobras);

    @Query(nativeQuery = true,
            value = "select * from personas p inner join directores d on p.idpersonas = d.idpersona where idobra = ?1")
    public List<Persona> findDirectoresByIdObra(int idobras);

    @Query(nativeQuery = true,
    value = "select p.idpersonas as 'id', p.nombre as 'nombre', round(avg(if(c.rol='Actuacion',c.estrellas,null)),1) as 'pactuacion',\n" +
            "       round(avg(if(c.rol='Direccion',c.estrellas,null)),1) as 'pdireccion', p.estado, co.actuaciones as 'obrasactor', co.direcciones as 'obrasdirector'\n" +
            "from personas p\n" +
            "         left join calificacionpersonas c on p.idpersonas = c.idpersonas\n" +
            "         inner join (select p.idpersonas as 'id',count(a.idobra) as 'actuaciones', count(d.idobra) as 'direcciones' from personas p\n" +
            "        left join directores d on p.idpersonas = d.idpersona\n" +
            "        left join actores a on p.idpersonas = a.idpersona\n" +
            "                     group by p.idpersonas) co on p.idpersonas = co.id\n" +
            "where p.nombre like %?1%\n" +
            "group by p.idpersonas",
    countQuery = "select count(*)\n" +
            "from personas p\n" +
            "where p.nombre like %?1%" +
            "group by p.idpersonas")
    public List<PersonasListado> findPersonasListado(String busqueda, Pageable pageable);
    @Query(nativeQuery = true,
            value = "select count(*)\n" +
                    "from personas p\n" +
                    "where p.nombre like %?1%\n")
    public long countPersonasListado(String busqueda);

    @Query(nativeQuery = true,
            value = "select p.idpersonas as 'id', p.nombre as 'nombre', round(avg(if(c.rol='Actuacion',c.estrellas,null)),1) as 'pactuacion',\n" +
                    "                                       round(avg(if(c.rol='Direccion',c.estrellas,null)),1) as 'pdireccion', p.estado, co.actuaciones as 'obrasactor', co.direcciones as 'obrasdirector', o.titulo as 'ptitulo'\n" +
                    "                                from personas p\n" +
                    "                                        left join calificacionpersonas c on p.idpersonas = c.idpersonas\n" +
                    "                                        left join actores ac on p.idpersonas = ac.idpersona\n" +
                    "                                         left join obras o on ac.idobra = o.idobras\n" +
                    "                                        inner join (select p.idpersonas as 'id',count(a.idobra) as 'actuaciones', count(d.idobra) as 'direcciones' from personas p\n" +
                    "                                       left join directores d on p.idpersonas = d.idpersona\n" +
                    "                                        left join actores a on p.idpersonas = a.idpersona\n" +
                    "                                                   group by p.idpersonas) co on p.idpersonas = co.id\n" +
                    "                             where p.nombre like %?1% \n" +
                    "                                group by p.idpersonas",
            countQuery = "select count(*)\n" +
                    "from personas p\n" +
                    "where p.nombre like %?1%" +
                    "group by p.idpersonas")
    public List<PersonasListado> listadoPersonascliente(String busqueda, Pageable pageable);


    @Query(nativeQuery = true,
            value = "select count(*)\n" +
                    "from personas p\n" +
                    "where p.nombre like %?1%\n")
    public long countPersonasListadoCliente(String busqueda);

    @Query(nativeQuery = true,
    value = "select (count(a.idobra)+count(d.idobra)) as 'participaciones' from personas p\n" +
            "left join directores d on p.idpersonas = d.idpersona\n" +
            "left join actores a on p.idpersonas = a.idpersona\n" +
            "where p.idpersonas=?1\n" +
            "group by p.idpersonas")
    public Optional<Integer> partipacionesPersona(Integer id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
    value = "update personas set nombre=?2, estado=?3\n" +
            "where idpersonas=?1")
    void actualizarPersona(Integer id,String nombre, String estado);

    @Query(nativeQuery = true,
    value="select p.idpersonas, p.nombre, null as 'foto' ,p.estado  from personas p\n" +
            "where idpersonas=?1")
    public Optional<Persona> buscarPersona(Integer id);

    @Query(nativeQuery = true,
            value = "select p.idpersonas as 'id', p.nombre as 'nombre', round(avg(if(c.rol='Actuacion',c.estrellas,null)),1) as 'pactuacion',\n" +
                    "                                       round(avg(if(c.rol='Direccion',c.estrellas,null)),1) as 'pdireccion', p.estado, co.actuaciones as 'obrasactor', co.direcciones as 'obrasdirector'\n" +
                    "                                from personas p\n" +
                    "                                        left join calificacionpersonas c on p.idpersonas = c.idpersonas\n"+
                    "                                        inner join (select p.idpersonas as 'id',count(a.idobra) as 'actuaciones', count(d.idobra) as 'direcciones' from personas p\n" +
                    "                                       left join directores d on p.idpersonas = d.idpersona\n" +
                    "                                        left join actores a on p.idpersonas = a.idpersona\n" +
                    "                                                   group by p.idpersonas) co on p.idpersonas = co.id\n" +
                    "                             where p.idpersonas = ?1 " +
                    "                                group by p.idpersonas")
    public PersonasListado findPersonasclienteById(int id);

    @Query(value = "select count(*)from personas",nativeQuery = true)
    Integer contarPersonas();
}
