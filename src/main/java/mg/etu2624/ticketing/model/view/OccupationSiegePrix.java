package mg.etu2624.ticketing.model.view;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "occupation_sieges_prix")
public class OccupationSiegePrix {

    @Id
    @Column(name = "vol_id")
    private Long volId;

    @Column(name = "numero_vol")
    private String numeroVol;

    @Id
    @Column(name = "classe_siege")
    private String classeSiege;

    @Id
    @Column(name = "categorie_id")
    private Long categorieId;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "prix")
    private BigDecimal prix;

    public Long getVolId() { return volId; }
    public void setVolId(Long volId) { this.volId = volId; }

    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }

    public String getClasseSiege() { return classeSiege; }
    public void setClasseSiege(String classeSiege) { this.classeSiege = classeSiege; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
}
