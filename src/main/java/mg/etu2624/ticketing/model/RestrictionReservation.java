package mg.etu2624.ticketing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restrictions_reservation")
public class RestrictionReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vol_id", unique = true, nullable = false)
    private Vol vol;

    @Column(name = "duree", nullable = false)
    private int duree;

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

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

}
