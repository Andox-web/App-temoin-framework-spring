package mg.etu2624.ticketing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restrictions_reservation_global")
public class RestrictionReservationGlobal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delai_creation_heures")
    private BigDecimal delaiCreationHeures;

    @Column(name = "delai_annulation_heures")
    private BigDecimal delaiAnnulationHeures;

    @Column(name = "date_restriction", nullable = false)
    private LocalDateTime dateRestriction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateRestriction() {
        return dateRestriction;
    }

    public void setDateRestriction(LocalDateTime dateRestriction) {
        this.dateRestriction = dateRestriction;
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

}
