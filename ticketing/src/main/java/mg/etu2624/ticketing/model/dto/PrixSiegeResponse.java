package mg.etu2624.ticketing.model.dto;

// PrixSiegeResponse.java
public class PrixSiegeResponse {
    private Double prixBase;
    private Double prixFinal;
    private PromotionResponse promotion;
    private String categorieNom;

    // Constructors
    public PrixSiegeResponse() {}

    public PrixSiegeResponse(Double prixBase, Double prixFinal, 
                             PromotionResponse promotion, String categorieNom) {
        this.prixBase = prixBase;
        this.prixFinal = prixFinal;
        this.promotion = promotion;
        this.categorieNom = categorieNom;
    }

    public Double getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(Double prixBase) {
        this.prixBase = prixBase;
    }

    public Double getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(Double prixFinal) {
        this.prixFinal = prixFinal;
    }

    public PromotionResponse getPromotion() {
        return promotion;
    }

    public void setPromotion(PromotionResponse promotion) {
        this.promotion = promotion;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }

    // Getters & Setters
    
}