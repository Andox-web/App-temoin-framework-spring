package mg.etu2624.ticketing.model.dto;

import java.math.BigDecimal;

import mg.itu.prom16.validation.NotBlank;

public class PrixVolClasseDTO {
    @NotBlank
    private Long classeSiegeId;
    private BigDecimal prix;
    public Long getClasseSiegeId() {
        return classeSiegeId;
    }
    public void setClasseSiegeId(Long classeSiegeId) {
        this.classeSiegeId = classeSiegeId;
    }
    public BigDecimal getPrix() {
        return prix;
    }
    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
    @Override
    public String toString() {
        return "PrixVolClasseDto [classeSiegeId=" + classeSiegeId + ", prix=" + prix + "]";
    }
    
}