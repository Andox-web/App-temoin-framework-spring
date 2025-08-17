package mg.etu2624.ticketing.model.dto;

import org.springframework.web.multipart.MultipartFile;

// ReservationRequest.java
public class ReservationRequest {
    private Long volId;
    private Long siegeId;
    private Long categorieId;
    private String categorieNom;
    private Long promotionId;
    private String nomPassager;
    private String emailPassager;
    private MultipartFile passportPassager;
    public Long getVolId() {
        return volId;
    }
    public void setVolId(Long volId) {
        this.volId = volId;
    }
    public Long getSiegeId() {
        return siegeId;
    }
    public void setSiegeId(Long siegeId) {
        this.siegeId = siegeId;
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
    public Long getPromotionId() {
        return promotionId;
    }
    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }
    public String getNomPassager() {
        return nomPassager;
    }
    public void setNomPassager(String nomPassager) {
        this.nomPassager = nomPassager;
    }
    public String getEmailPassager() {
        return emailPassager;
    }
    public void setEmailPassager(String emailPassager) {
        this.emailPassager = emailPassager;
    }
    public MultipartFile getPassportPassager() {
        return passportPassager;
    }
    public void setPassportPassager(MultipartFile passportPassager) {
        this.passportPassager = passportPassager;
    }

    // Getters & Setters
    
}