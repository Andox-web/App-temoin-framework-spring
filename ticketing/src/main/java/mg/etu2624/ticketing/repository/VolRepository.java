package mg.etu2624.ticketing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.etu2624.ticketing.model.Vol;

// VolRepository.java
public interface VolRepository extends JpaRepository<Vol, Long> {
    Optional<Vol> findById(Long id);
}
