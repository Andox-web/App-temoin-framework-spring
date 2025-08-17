package mg.etu2624.ticketing.model;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// VuePrixSiege.java (Projection pour la vue SQL)
@Immutable
@Entity
@Table(name = "vue_prix_siege")
public class VuePrixSiege {
    @Id
    @Column(name = "siege_id")
    private Long siegeId;
    
    @Column(name = "vol_id")
    private Long volId;
    
    @Column(name = "numero_siege")
    private String numeroSiege;

    @Column(name = "prix_base")
    private Double prixBase;
    
    @Column(name = "promotion_id")
    private Long promotionId;
    
    @Column(name = "categorie_id")
    private Long categorieId;
    
    @Column(name = "categorie_nom")
    private String categorieNom;
    
    @Column(name = "prix_final")
    private Double prixFinal;

    public Long getSiegeId() {
        return siegeId;
    }

    public void setSiegeId(Long siegeId) {
        this.siegeId = siegeId;
    }

    public Long getVolId() {
        return volId;
    }

    public void setVolId(Long volId) {
        this.volId = volId;
    }

    public String getNumeroSiege() {
        return numeroSiege;
    }

    public void setNumeroSiege(String numeroSiege) {
        this.numeroSiege = numeroSiege;
    }

    public Double getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(Double prixBase) {
        this.prixBase = prixBase;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
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

    public Double getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(Double prixFinal) {
        this.prixFinal = prixFinal;
    }

    // Getters (pas de setters)
    
}