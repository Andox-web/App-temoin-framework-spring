package mg.etu2624.ticketing.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "promotions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vol_id", "classe_siege_id"})
})
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;

    @Column(name = "description")
    private String description;

    @Column(name = "pourcentage_reduction", nullable = false)
    private BigDecimal pourcentageReduction;

    @ManyToOne
    @JoinColumn(name = "classe_siege_id")
    private ClasseSiege classeSiege;

    @Column(name = "limite_sieges", nullable = false)
    private int limiteSieges;

    @Column(name = "promotion_utilisee", nullable = false)
    private int promotionUtilisee;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPourcentageReduction() {
        return pourcentageReduction;
    }

    public void setPourcentageReduction(BigDecimal pourcentageReduction) {
        this.pourcentageReduction = pourcentageReduction;
    }

    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }

    public int getLimiteSieges() {
        return limiteSieges;
    }

    public void setLimiteSieges(int limiteSieges) {
        this.limiteSieges = limiteSieges;
    }

    public int getPromotionUtilisee() {
        return promotionUtilisee;
    }

    public void setPromotionUtilisee(int promotionUtilisee) {
        this.promotionUtilisee = promotionUtilisee;
    }

}
