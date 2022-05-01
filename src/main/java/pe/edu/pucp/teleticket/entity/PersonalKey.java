package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PersonalKey implements Serializable {

    @Column(name = "idpersonas", nullable = false)
    private int idpersonas;
    @Column(name = "idobras", nullable = false)
    private int idobras;

    @Override
    public int hashCode() {
        return Objects.hash(idpersonas, idobras);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersonalKey entity = (PersonalKey) o;
        return Objects.equals(this.idpersonas, entity.idpersonas) &&
                Objects.equals(this.idobras, entity.idobras);
    }
}