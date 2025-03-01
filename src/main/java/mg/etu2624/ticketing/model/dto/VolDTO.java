package mg.etu2624.ticketing.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import mg.itu.prom16.validation.NotBlank;

public class VolDTO {
    private Long id;

    private String numeroVol;
    
    @NotBlank
    private Long avionId;
    
    @NotBlank
    private LocalDateTime depart;
    
    @NotBlank
    private BigDecimal dureeEstimee;
    
    @NotBlank
    private String origine;
    @NotBlank
    private String destination;
    
    private List<PrixVolClasseDTO> prixVolClasses;
    public String getNumeroVol() {
        return numeroVol;
    }
    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }
    public Long getAvionId() {
        return avionId;
    }
    public void setAvionId(Long avionId) {
        this.avionId = avionId;
    }
    public LocalDateTime getDepart() {
        return depart;
    }
    public void setDepart(LocalDateTime depart) {
        this.depart = depart;
    }
    public BigDecimal getDureeEstimee() {
        return dureeEstimee;
    }
    public void setDureeEstimee(BigDecimal dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }
    public String getOrigine() {
        return origine;
    }
    public void setOrigine(String origine) {
        this.origine = origine;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public List<PrixVolClasseDTO> getPrixVolClasses() {
        return prixVolClasses;
    }
    public void setPrixVolClasses(List<PrixVolClasseDTO> prixVolClasses) {
        this.prixVolClasses = prixVolClasses;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "VolDto [numeroVol=" + numeroVol + ", avionId=" + avionId + ", depart=" + depart + ", dureeEstimee="
                + dureeEstimee + ", origine=" + origine + ", destination=" + destination + ", prixVolClasses="
                + prixVolClasses + "]";
    }
    
}
