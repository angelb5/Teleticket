package pe.edu.pucp.teleticket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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

    @Query(nativeQuery = true,
            value = "select * from personas p inner join actores a on p.idpersonas = a.idpersona where idobra = ?1")
    public List<Persona> findActoresByIdObra(int idobras);

    @Query(nativeQuery = true,
            value = "select * from personas p inner join directores d on p.idpersonas = d.idpersona where idobra = ?1")
    public List<Persona> findDirectoresByIdObra(int idobras);

    @Query(nativeQuery = true,
    value = "select p.idpersonas as 'id', p.nombre as 'nombre', round(avg(if(c.rol='Actuacion',c.estrellas,null)),1) as 'pnactuacion',\n" +
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
            value = "select p.idpersonas as 'id', p.nombre as 'nombre', round(avg(if(c.rol='Actuacion',c.estrellas,null)),1) as 'pnactuacion',\n" +
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

    @Query(nativeQuery = true,
    value = "insert into personas(nombre, estado)\n" +
            "values (?2,?3) where idpersonas=?1")
    public void actualizarPersona(Integer id,String nombre, String estado);

    @Query(nativeQuery = true,
    value="select idpersonas,nombre,estado from personas\n" +
            "where idpersonas=?1")
    public Optional<Persona> buscarPersona(Integer id);

}
