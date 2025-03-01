package mg.etu2624.ticketing.model.view;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "occupation_sieges")
@IdClass(OccupationSiegeId.class)
public class OccupationSiege {

    @Id
    @Column(name = "vol_id")
    private Long volId;
    
    @Column(name = "numero_vol")
    private String numeroVol;
    
    @Id
    @Column(name = "classe_siege")
    private String classeSiege;
    
    @Column(name = "nombre_total_sieges")
    private Integer nombreTotalSieges;
    
    @Column(name = "nombre_occupe")
    private Integer nombreOccupe;
    
    @Column(name = "nombre_libre")
    private Integer nombreLibre;

    @Column(name = "prix")
    private BigDecimal prix;

    public BigDecimal getPrix() { return prix; }

    public Long getVolId() {
        return volId;
    }
    public void setVolId(Long volId) {
        this.volId = volId;
    }
    public String getNumeroVol() {
        return numeroVol;
    }
    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }
    public String getClasseSiege() {
        return classeSiege;
    }
    public void setClasseSiege(String classeSiege) {
        this.classeSiege = classeSiege;
    }
    public Integer getNombreTotalSieges() {
        return nombreTotalSieges;
    }
    public void setNombreTotalSieges(Integer nombreTotalSieges) {
        this.nombreTotalSieges = nombreTotalSieges;
    }
    public Integer getNombreOccupe() {
        return nombreOccupe;
    }
    public void setNombreOccupe(Integer nombreOccupe) {
        this.nombreOccupe = nombreOccupe;
    }
    public Integer getNombreLibre() {
        return nombreLibre;
    }
    public void setNombreLibre(Integer nombreLibre) {
        this.nombreLibre = nombreLibre;
    }

}
