package mg.etu2624.ticketing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sieges_avion")
public class SiegeAvion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;

    @ManyToOne
    @JoinColumn(name = "classe_siege_id", nullable = false)
    private ClasseSiege classeSiege;

    @Column(name = "nombre_sieges", nullable = false)
    private int nombreSieges;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }

    public int getNombreSieges() {
        return nombreSieges;
    }

    public void setNombreSieges(int nombreSieges) {
        this.nombreSieges = nombreSieges;
    }

}
