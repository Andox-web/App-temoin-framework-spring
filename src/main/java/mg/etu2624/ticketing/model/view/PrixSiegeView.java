package mg.etu2624.ticketing.model.view;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mg.etu2624.ticketing.model.Promotion;

@Entity
@Immutable
@Table(name = "vue_prix_siege")
public class PrixSiegeView {

    @EmbeddedId
    private PrixSiegeViewId id;

    @Column(name = "numero_siege")
    private String numeroSiege;

    @Column(name = "prix_base")
    private BigDecimal prixBase;

    @ManyToOne(optional = true)
    @JoinColumn(name = "promotion_id", nullable = true)
    private Promotion promotion;

    @Column(name = "prix_final")
    private BigDecimal prixFinal;

    @Column(name = "categorie_id")
    private Long categorieId;

    @Column(name = "categorie_nom")
    private String categorieNom;

    public PrixSiegeView() {}

    public PrixSiegeViewId getId() {
        return id;
    }

    public void setId(PrixSiegeViewId id) {
        this.id = id;
    }

    public String getNumeroSiege() {
        return numeroSiege;
    }

    public void setNumeroSiege(String numeroSiege) {
        this.numeroSiege = numeroSiege;
    }

    public BigDecimal getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(BigDecimal prixBase) {
        this.prixBase = prixBase;
    }

    public BigDecimal getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(BigDecimal prixFinal) {
        this.prixFinal = prixFinal;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public Long getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }
    
}
