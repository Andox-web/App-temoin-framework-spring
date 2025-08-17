package mg.etu2624.ticketing.model.dto;

// PromotionResponse.java
public class PromotionResponse {
    private Long id;
    private Double pourcentageReduction;

    // Constructors
    public PromotionResponse() {}

    public PromotionResponse(Long id, Double pourcentageReduction) {
        this.id = id;
        this.pourcentageReduction = pourcentageReduction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPourcentageReduction() {
        return pourcentageReduction;
    }

    public void setPourcentageReduction(Double pourcentageReduction) {
        this.pourcentageReduction = pourcentageReduction;
    }

    // Getters & Setters
    
}