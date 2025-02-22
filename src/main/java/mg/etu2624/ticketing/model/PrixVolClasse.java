package mg.etu2624.ticketing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prix_vols_classes")
public class PrixVolClasse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "classe_siege_id", nullable = false)
    private ClasseSiege classeSiege;

    @Column(name = "prix", nullable = false)
    private BigDecimal prix;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

}