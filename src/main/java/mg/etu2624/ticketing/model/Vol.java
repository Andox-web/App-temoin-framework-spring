package mg.etu2624.ticketing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vols")
public class Vol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_vol", unique = true, nullable = false)
    private String numeroVol;

    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;

    @Column(name = "depart", nullable = false)
    private LocalDateTime depart;

    @Column(name = "arrivee")
    private LocalDateTime arrivee;

    @Column(name = "limite_activiter")
    private LocalDateTime limiteActiviter;

    @Column(name = "duree_estimee", nullable = false)
    private int dureeEstimee;

    @Column(name = "origine", nullable = false)
    private String origine;

    @Column(name = "destination", nullable = false)
    private String destination;

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

    public LocalDateTime getLimiteActiviter() {
        return limiteActiviter;
    }

    public void setLimiteActiviter(LocalDateTime limiteActiviter) {
        this.limiteActiviter = limiteActiviter;
    }

    public int getDureeEstimee() {
        return dureeEstimee;
    }

    public void setDureeEstimee(int dureeEstimee) {
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

}
