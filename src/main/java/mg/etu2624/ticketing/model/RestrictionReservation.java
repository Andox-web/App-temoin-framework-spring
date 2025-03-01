package mg.etu2624.ticketing.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "restrictions_reservation")
public class RestrictionReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vol_id", unique = true, nullable = false)
    private Vol vol;

    @Column(name = "delai_creation_heures")
    private BigDecimal delaiCreationHeures;

    @Column(name = "delai_annulation_heures")
    private BigDecimal delaiAnnulationHeures;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vol getVol() {
        return vol;
    }

    public BigDecimal getDelaiCreationHeures() {
        return delaiCreationHeures;
    }

    public void setDelaiCreationHeures(BigDecimal delaiCreationHeures) {
        this.delaiCreationHeures = delaiCreationHeures;
    }

    public BigDecimal getDelaiAnnulationHeures() {
        return delaiAnnulationHeures;
    }

    public void setDelaiAnnulationHeures(BigDecimal delaiAnnulationHeures) {
        this.delaiAnnulationHeures = delaiAnnulationHeures;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }
    
}
