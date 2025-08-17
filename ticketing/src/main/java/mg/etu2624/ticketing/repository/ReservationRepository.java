package mg.etu2624.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.etu2624.ticketing.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}