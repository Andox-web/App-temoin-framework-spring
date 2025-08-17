package mg.etu2624.ticketing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.etu2624.ticketing.model.VuePrixSiege;

// VuePrixSiegeRepository.java
public interface VuePrixSiegeRepository extends JpaRepository<VuePrixSiege, Long> {
    
    @Query("SELECT v FROM VuePrixSiege v " +
           "WHERE v.volId = :volId " +
           "AND v.siegeId = :siegeId " +
           "AND v.categorieId = :categorieId")
    Optional<VuePrixSiege> findPrixSiege(
        @Param("volId") Long volId,
        @Param("siegeId") Long siegeId,
        @Param("categorieId") Long categorieId);
}
