package pe.edu.pucp.teleticket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "personal")
public class Personal {
    @EmbeddedId
    PersonalKey id;

    @MapsId("idpersonas")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpersonas", nullable = false)
    private Persona persona;

    @MapsId("idobras")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idobras", nullable = false)
    private Obra obra;

    @Lob
    @Column(name = "rol", nullable = false)
    private String rol;
}