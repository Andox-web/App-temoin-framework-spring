package mg.etu2624.ticketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.etu2624.ticketing.model.Categorie;

// CategorieRepository.java
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    List<Categorie> findAll();
}