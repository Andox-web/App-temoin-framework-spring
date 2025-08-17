package mg.etu2624.ticketing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.etu2624.ticketing.model.Siege;

// SiegeRepository.java
public interface SiegeRepository extends JpaRepository<Siege, Long> {
    @Query("SELECT s FROM Siege s JOIN FETCH s.classeSiege WHERE s.id = :siegeId")
    Optional<Siege> findByIdWithClasse(@Param("siegeId") Long siegeId);
}
