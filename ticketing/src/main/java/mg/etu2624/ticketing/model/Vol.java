package mg.etu2624.ticketing.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vols")
public class Vol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_vol")
    private String numeroVol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avion_id")
    private Avion avion;
    
    private LocalDateTime depart;
    private LocalDateTime arrivee;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNumeroVol() {
        return numeroVol;
    }
    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }
    public Avion getAvion() {
        return avion;
    }
    public void setAvion(Avion avion) {
        this.avion = avion;
    }
    public LocalDateTime getDepart() {
        return depart;
    }
    public void setDepart(LocalDateTime depart) {
        this.depart = depart;
    }
    public LocalDateTime getArrivee() {
        return arrivee;
    }
    public void setArrivee(LocalDateTime arrivee) {
        this.arrivee = arrivee;
    }
    
}
