package pe.edu.pucp.teleticket;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "salas")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsalas", nullable = false)
    private Integer id;

    @Column(name = "idsedes", nullable = false)
    private int idsedes;

    @Column(name = "numero", nullable = false, length = 5)
    private String numero;

    @Column(name = "aforo", nullable = false)
    private Integer aforo;

    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;


}