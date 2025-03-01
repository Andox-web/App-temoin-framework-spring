package mg.etu2624.ticketing.model.dto;

import java.math.BigDecimal;

import mg.itu.prom16.validation.NotBlank;
import mg.itu.prom16.validation.Positive;

public class PromotionDTO {
    @NotBlank
    private Long volId;
    
    private String description;
    
    @NotBlank
    private BigDecimal pourcentageReduction;
    
    @NotBlank
    private Long classeSiegeId;
    
    @NotBlank
    @Positive
    private Integer limiteSieges;
    public Long getVolId() {
        return volId;
    }
    public void setVolId(Long volId) {
        this.volId = volId;
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
    public Long getClasseSiegeId() {
        return classeSiegeId;
    }
    public void setClasseSiegeId(Long classeSiegeId) {
        this.classeSiegeId = classeSiegeId;
    }
    public Integer getLimiteSieges() {
        return limiteSieges;
    }
    public void setLimiteSieges(Integer limiteSieges) {
        this.limiteSieges = limiteSieges;
    }

}