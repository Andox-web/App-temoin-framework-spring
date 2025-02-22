package mg.etu2624.ticketing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "classe_siege_id", nullable = false)
    private ClasseSiege classeSiege;

    @ManyToOne
    @JoinColumn(name = "siege_id", nullable = false)
    private Siege siege;

    @Column(name = "nom_passager", nullable = false)
    private String nomPassager;

    @Column(name = "email_passager", nullable = false)
    private String emailPassager;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "prix", nullable = false)
    private BigDecimal prix;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }

    public Siege getSiege() {
        return siege;
    }

    public void setSiege(Siege siege) {
        this.siege = siege;
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

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

}
