package mg.etu2624.ticketing.model.dto;

import mg.itu.prom16.servlet.MultipartFile;
import mg.itu.prom16.validation.Email;
import mg.itu.prom16.validation.NotBlank;
import mg.itu.prom16.validation.Size;

public class ReservationDTO {

    private Long volId;
    private Long siegeId;
    
    @NotBlank(message = "Le nom du passager est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nomPassager;
    
    @NotBlank(message = "L'email du passager est requis")
    @Email(message = "L'email doit être valide")
    private String emailPassager;
    
    private Long promotionId;
    
    @Size(max = 200, message = "la taille du fichier ne doit pas dépasser 200 ")
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

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public MultipartFile getPassportPassager() {
        return passportPassager;
    }

    public void setPassportPassager(MultipartFile passportPassager) {
        this.passportPassager = passportPassager;
    }

    @Override
    public String toString() {
        return "ReservationDTO [volId=" + volId + ", siegeId=" + siegeId + ", nomPassager=" + nomPassager
                + ", emailPassager=" + emailPassager + ", promotionId=" + promotionId + ", passportPassager="
                + passportPassager + "]";
    }
    
}
