package mg.etu2624.ticketing.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.antlr.v4.runtime.misc.Pair;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import mg.etu2624.ticketing.model.Avion;
import mg.etu2624.ticketing.model.ClasseSiege;
import mg.etu2624.ticketing.model.PrixVolClasse;
import mg.etu2624.ticketing.model.Siege;
import mg.etu2624.ticketing.model.Vol;
import mg.etu2624.ticketing.model.dto.PrixVolClasseDTO;
import mg.etu2624.ticketing.model.dto.VolDTO;
import mg.etu2624.ticketing.model.view.OccupationSiege;
import mg.itu.prom16.annotation.Autowired;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.controller.Controller;
import mg.itu.prom16.mapping.GetMapping;
import mg.itu.prom16.mapping.PutMapping;
import mg.itu.prom16.mapping.Rest;
import mg.itu.prom16.mapping.Url;
import mg.itu.prom16.param.PathVariable;
import mg.itu.prom16.param.RequestBody;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.response.ResponseEntity;

@Controller
@Url("/vols")
public class VolController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    public String listVols(@RequestBody VolSearch criteria, Model model) {
        EntityManager em = emf.createEntityManager();
        try {
            Map<String, Object> params = new HashMap<>();
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT v FROM Vol v LEFT JOIN v.prixVolClasses pvc JOIN OccupationSiege o ON v.id = o.volId WHERE 1=1");

            // Filtres dynamiques
            if (criteria.getNumeroVol() != null && !criteria.getNumeroVol().isEmpty()) {
                jpql.append(" AND v.numeroVol LIKE :numeroVol"); 
                params.put("numeroVol", "%" + criteria.getNumeroVol() + "%");
            }

            try {
                if (criteria.getAvionId() != null && !criteria.getAvionId().isEmpty()) {
                    jpql.append(" AND v.avion.id = :avionId");
                    params.put("avionId", Long.parseLong(criteria.getAvionId()));
                }
            } catch (NumberFormatException ignored) {}

            try {
                if (criteria.getClasseId() != null && !criteria.getClasseId().isEmpty()) {
                    jpql.append(" AND pvc.classeSiege.id = :classeId");
                    params.put("classeId", Long.parseLong(criteria.getClasseId()));
                }
            } catch (NumberFormatException ignored) {}

            if (criteria.getDepartMin() != null && !criteria.getDepartMin().isEmpty()) {
                jpql.append(" AND v.depart >= :departMin");
                params.put("departMin", LocalDateTime.parse(criteria.getDepartMin() + "T00:00"));
            }
            if (criteria.getDepartMax() != null && !criteria.getDepartMax().isEmpty()) {
                jpql.append(" AND v.depart <= :departMax");
                params.put("departMax", LocalDateTime.parse(criteria.getDepartMax() + "T23:59"));
            }

            if (criteria.getOrigine() != null && !criteria.getOrigine().isEmpty()) {
                jpql.append(" AND v.origine LIKE :origine");
                params.put("origine", "%" + criteria.getOrigine() + "%");
            }
            if (criteria.getDestination() != null && !criteria.getDestination().isEmpty()) {
                jpql.append(" AND v.destination LIKE :destination");
                params.put("destination", "%" + criteria.getDestination() + "%");
            }

            try {
                if (criteria.getPrixMin() != null && !criteria.getPrixMin().isEmpty()) {
                    jpql.append(" AND o.prix >= :prixMin");
                    params.put("prixMin", new BigDecimal(criteria.getPrixMin()));
                }
                if (criteria.getPrixMax() != null && !criteria.getPrixMax().isEmpty()) {
                    jpql.append(" AND o.prix <= :prixMax");
                    params.put("prixMax", new BigDecimal(criteria.getPrixMax()));
                }
            } catch (NumberFormatException ignored) {}

            // Exécution requête
            TypedQuery<Vol> query = em.createQuery(jpql.toString(), Vol.class);
            params.forEach(query::setParameter);
            List<Vol> vols = query.getResultList();

            // Préparation modèle
            model.addObject("vols", vols)
                .addObject("avions", em.createQuery("FROM Avion", Avion.class).getResultList())
                .addObject("classesSiege", em.createQuery("FROM ClasseSiege", ClasseSiege.class).getResultList())
                .addObject("occupationMap", em.createQuery("FROM OccupationSiege", OccupationSiege.class)
                    .getResultList().stream().collect(Collectors.groupingBy(OccupationSiege::getVolId)))
                .addObject("numeroVol", criteria.getNumeroVol())
                .addObject("avionId", criteria.getAvionId())
                .addObject("departMin", criteria.getDepartMin())
                .addObject("departMax", criteria.getDepartMax())
                .addObject("origine", criteria.getOrigine())
                .addObject("destination", criteria.getDestination())
                .addObject("classeId", criteria.getClasseId())
                .addObject("prixMin", criteria.getPrixMin())
                .addObject("prixMax", criteria.getPrixMax())
                .addObject("pageContent", "vols/list");

        } finally {
            em.close();
        }
        return "template/template";
    }

    public static class VolSearch{
        String numeroVol;
        String avionId;
        String departMin;
        String departMax;
        String origine;
        String destination;
        String classeId;
        String prixMin;
        String prixMax;
        
        public String getPrixMax() {
            return prixMax;
        }
        public void setPrixMax(String prixMax) {
            this.prixMax = prixMax;
        }
        public String getNumeroVol() {
            return numeroVol;
        }
        public void setNumeroVol(String numeroVol) {
            this.numeroVol = numeroVol;
        }
        public String getAvionId() {
            return avionId;
        }
        public void setAvionId(String avionId) {
            this.avionId = avionId;
        }
        public String getDepartMin() {
            return departMin;
        }
        public void setDepartMin(String departMin) {
            this.departMin = departMin;
        }
        public String getDepartMax() {
            return departMax;
        }
        public void setDepartMax(String departMax) {
            this.departMax = departMax;
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
        public String getClasseId() {
            return classeId;
        }
        public void setClasseId(String classeId) {
            this.classeId = classeId;
        }
        public String getPrixMin() {
            return prixMin;
        }
        public void setPrixMin(String prixMin) {
            this.prixMin = prixMin;
        }
        
    }

    @GetMapping
    @Url("/create")
    @RoleRequired("admin")
    public String showCreateVolForm(Model model) {
        EntityManager em = emf.createEntityManager();
        try {
            Query queryAvions = em.createQuery("SELECT a FROM Avion a");
            Query queryClassesSiege = em.createQuery("SELECT cs FROM ClasseSiege cs");
            model.addObject("avions", queryAvions.getResultList());
            model.addObject("classesSiege", queryClassesSiege.getResultList());
        } finally {
            em.close();
        }
        model.addObject("pageContent", "vols/create");
        return "template/template";
    }
    
    @Rest
    @PutMapping
    @Url("/create")
    @RoleRequired("admin")
    public ResponseEntity<String> createVol(@RequestBody("vol") VolDTO volDto) {
        
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Vol vol = new Vol();
            vol.setNumeroVol(volDto.getNumeroVol());
            vol.setAvion(em.getReference(Avion.class, volDto.getAvionId()));
            vol.setDepart(volDto.getDepart());
            vol.setDureeEstimee(volDto.getDureeEstimee());
            vol.setOrigine(volDto.getOrigine());
            vol.setDestination(volDto.getDestination());
            em.persist(vol);
            for (PrixVolClasseDTO prixVolClasseDto : volDto.getPrixVolClasses()) {
                PrixVolClasse prixVolClasse = new PrixVolClasse();
                prixVolClasse.setVol(vol);
                prixVolClasse.setClasseSiege(em.getReference(ClasseSiege.class, prixVolClasseDto.getClasseSiegeId()));
                prixVolClasse.setPrix(prixVolClasseDto.getPrix());
                em.persist(prixVolClasse);
            }
            em.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
            em.getTransaction().rollback();
            return ResponseEntity.error(500, e.getMessage());
        }
         finally {
            em.close();
        }
        return ResponseEntity.ok("Vol créé");
    }

    @GetMapping
    @Url("/view/{id}")
    public String viewVol(@PathVariable Long id, Model model) {
        EntityManager em = emf.createEntityManager();
        try {
            // Récupération des données principales
            Vol vol = em.find(Vol.class, id);
            List<OccupationSiege> occupations = em.createQuery(
                "SELECT o FROM OccupationSiege o WHERE o.volId = :volId", OccupationSiege.class)
                .setParameter("volId", id)
                .getResultList();

            boolean isOngoing = vol.getDepart().isAfter(LocalDateTime.now()); 

            Pair volInfo = vol.getVolSiegeInfo(em);
            model.addObject("vol", vol)
                 .addObject("occupations", occupations)
                 .addObject("siegeParRangeeParClasse", volInfo.b)
                 .addObject("maxColonne", volInfo.a)
                 .addObject("isOngoing", isOngoing)
                 .addObject("pageContent", "vols/view");

        }catch (Exception e) {
            System.err.println(e.getMessage());
            return "redirect:/vols";
        }finally {
            em.close();
        }
        return "template/template";
    }
}
