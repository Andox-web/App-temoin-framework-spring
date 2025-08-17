package mg.etu2624.ticketing.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class ReservationDTO {
    private Long volId;
    
    private Long siegeId;
    
    private Long promotionId;
    
    private String nomPassager;
    
    private String emailPassager;
    
    private MultipartFile passportPassager;
    
    private Long categorieId;

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

    public Long getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    // Getters and Setters
    
}