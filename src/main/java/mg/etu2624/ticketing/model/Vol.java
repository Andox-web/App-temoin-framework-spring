package mg.etu2624.ticketing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.antlr.v4.runtime.misc.Pair;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @Column(name = "limite_creation")
    private LocalDateTime limiteCreation;

    @Column(name = "limite_annulation")
    private LocalDateTime limiteAnnulation;

    @Column(name = "duree_estimee", nullable = false)
    private BigDecimal dureeEstimee;

    @Column(name = "origine", nullable = false)
    private String origine;

    @Column(name = "destination", nullable = false)
    private String destination;

    @OneToMany(mappedBy = "vol")
    private Set<PrixVolClasse> prixVolClasses;

    @OneToMany(mappedBy = "vol")
    private Set<VolSiege> volSieges;

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

    public LocalDateTime getLimiteCreation() {
        return limiteCreation;
    }

    public void setLimiteCreation(LocalDateTime limiteCreation) {
        this.limiteCreation = limiteCreation;
    }

    public LocalDateTime getLimiteAnnulation() {
        return limiteAnnulation;
    }

    public void setLimiteAnnulation(LocalDateTime limiteAnnulation) {
        this.limiteAnnulation = limiteAnnulation;
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

    public BigDecimal getDureeEstimee() {
        return dureeEstimee;
    }

    public void setDureeEstimee(BigDecimal dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public Set<PrixVolClasse> getPrixVolClasses() {
        return prixVolClasses;
    }

    public void setPrixVolClasses(Set<PrixVolClasse> prixVolClasses) {
        this.prixVolClasses = prixVolClasses;
    }
    
    public Set<VolSiege> getVolSieges() {
        return volSieges;
    }

    public void setVolSieges(Set<VolSiege> volSieges) {
        this.volSieges = volSieges;
    }
    public Pair<Integer,Map<String,List<List<Object[]>>>> getVolSiegeInfo(EntityManager em)throws Exception{ 
        // Récupération des sièges avec statut et promotions
        List<Object[]> siegesData = em.createQuery(
            "SELECT s, vs.estReserve, p , r FROM Siege s " +
            "LEFT JOIN VolSiege vs ON s.id = vs.siege.id AND vs.vol.id = :volId " +
            "LEFT JOIN Promotion p ON p.vol.id = :volId AND p.classeSiege.id = s.classeSiege.id " +
            "LEFT JOIN Reservation r ON r.siege.id = s.id AND r.vol.id = :volId " +
            "WHERE s.avion.id = :avionId ORDER BY s.numeroSiege", Object[].class)
            .setParameter("volId", getId())
            .setParameter("avionId", getAvion().getId())
            .getResultList();

        // Groupement par classe et calcul des colonnes
        Map<String, List<Object[]>> siegesParClasse = siegesData.stream()
            .sorted((s1, s2) -> {
                try {
                    int r1 = Integer.parseInt(((Siege)s1[0]).getNumeroSiege().split("-")[2].replace("S", ""));
                    int r2 = Integer.parseInt(((Siege)s2[0]).getNumeroSiege().split("-")[2].replace("S", ""));
                    return Integer.compare(r1, r2);
                } catch (NumberFormatException e) {
                    return 0; // Gestion d'erreur personnalisée
                }
            })
            .collect(Collectors.groupingBy(s -> ((Siege)s[0]).getClasseSiege().getNom()));
        

        // Optimisation du nombre de colonnes
        Map<String, Integer> maxColonnes = new HashMap<>();
        siegesParClasse.forEach((classe, sieges) -> {
            maxColonnes.put(classe, sieges.size());
        });

        int maxColonne = optimiserColonnes(maxColonnes);
        Map<String, List<List<Object[]>>> siegeParRangeeParClasse = siegesParClasse.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    int optimalColonnes = maxColonnes.get(entry.getKey());
                    List<Object[]> sieges = entry.getValue();
                    
                    return (List<List<Object[]>>) IntStream.range(0, (sieges.size() + optimalColonnes - 1) / optimalColonnes)
                        .mapToObj(i -> sieges.subList(
                            i * optimalColonnes, 
                            Math.min((i + 1) * optimalColonnes, sieges.size())
                        ))
                        .collect(Collectors.toList());
                }
            ));
        return new Pair<Integer,Map<String,List<List<Object[]>>>>(Integer.valueOf(maxColonne), siegeParRangeeParClasse);
    }
    
    public int optimiserColonnes(Map<String, Integer> maxColonnes) {
        if (maxColonnes.isEmpty()) return 1;
    
        // 1. Calculer la moyenne quadratique
        double sommeCarres = 0;
        int count = 0;
        
        for (int cols : maxColonnes.values()) {
            sommeCarres += cols * cols;
            count++;
        }
        
        double rms = Math.sqrt(sommeCarres / count);
    
        // 2. Calculer la déviation moyenne
        double deviationTotale = 0;
        for (int cols : maxColonnes.values()) {
            deviationTotale += Math.abs(cols - rms);
        }
        
        // 3. Ajustement final
        int optimal = (int) Math.round(rms - (deviationTotale / count));
        
        return Math.max(optimal, 1); // Garantir au moins 1 colonne
    }
    
}
