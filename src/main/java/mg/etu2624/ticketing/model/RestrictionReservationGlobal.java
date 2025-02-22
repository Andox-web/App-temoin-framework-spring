package mg.etu2624.ticketing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restrictions_reservation_global")
public class RestrictionReservationGlobal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duree", nullable = false)
    private int duree;

    @Column(name = "date_restriction", nullable = false)
    private LocalDateTime dateRestriction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public LocalDateTime getDateRestriction() {
        return dateRestriction;
    }

    public void setDateRestriction(LocalDateTime dateRestriction) {
        this.dateRestriction = dateRestriction;
    }

}
