package mg.etu2624.ticketing.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import mg.etu2624.ticketing.config.JpaConfig;
import mg.etu2624.ticketing.model.RestrictionReservation;
import mg.etu2624.ticketing.model.RestrictionReservationGlobal;
import mg.etu2624.ticketing.model.Vol;
import mg.itu.prom16.annotation.Autowired;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.controller.Controller;
import mg.itu.prom16.mapping.GetMapping;
import mg.itu.prom16.mapping.PostMapping;
import mg.itu.prom16.mapping.Url;
import mg.itu.prom16.param.RequestParam;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.response.RedirectAttributes;

@Controller
@RoleRequired("admin")
@Url("/admin/parametres/regles-reservation")
public class RestrictionController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    public String showRestrictionForm(Model model) {
        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer la liste des vols depuis la base de données
            List<Vol> vols = em.createQuery("SELECT v FROM Vol v", Vol.class)
                               .getResultList();
            model.addObject("vols", vols); // Ajouter la liste des vols au modèle
        } finally {
            em.close();
        }
        model.addObject("pageContent", "restrictions");
        return "template/template"; // Nom de la vue JSP
    }

    @PostMapping
    public String saveRestriction(
        @RequestParam Long volId,
        @RequestParam BigDecimal delaiCreationHeures,
        @RequestParam BigDecimal delaiAnnulationHeures,
        RedirectAttributes model) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            if (volId != null) {
                // Gestion des restrictions par vol (RestrictionReservation)
                Vol vol = em.find(Vol.class, volId);
                if (vol == null) {
                    model.addFlashAttribute("error", "Vol introuvable");
                    return "redirect:/admin/parametres/regles-reservation";
                }

                RestrictionReservation restriction = em.createQuery(
                    "SELECT r FROM RestrictionReservation r WHERE r.vol = :vol", RestrictionReservation.class)
                    .setParameter("vol", vol)
                    .getResultStream()
                    .findFirst()
                    .orElse(new RestrictionReservation());

                restriction.setVol(vol);
                restriction.setDelaiCreationHeures(delaiCreationHeures);
                restriction.setDelaiAnnulationHeures(delaiAnnulationHeures);
                em.persist(restriction);
                
            } else {
                // Gestion des restrictions globales (RestrictionReservationGlobal)
                RestrictionReservationGlobal globalRestriction = new RestrictionReservationGlobal();
                globalRestriction.setDelaiCreationHeures(delaiCreationHeures);
                globalRestriction.setDelaiAnnulationHeures(delaiAnnulationHeures);
                globalRestriction.setDateRestriction(LocalDateTime.now());
                
                em.persist(globalRestriction);
            }
            transaction.commit();
            model.addFlashAttribute("success","true");
            return "redirect:/admin/parametres/regles-reservation";

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            model.addFlashAttribute("error", "Erreur lors de la sauvegarde: " + e.getMessage());
            if (e instanceof PersistenceException exception) {
                model.addFlashAttribute("error", JpaConfig.getSqlMessage(exception));
            }
            return "redirect:/admin/parametres/regles-reservation";
        } finally {
            em.close();
        }
    }
}