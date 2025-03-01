package mg.etu2624.ticketing.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import mg.etu2624.ticketing.config.JpaConfig;
import mg.etu2624.ticketing.model.ClasseSiege;
import mg.etu2624.ticketing.model.Promotion;
import mg.etu2624.ticketing.model.Reservation;
import mg.etu2624.ticketing.model.Siege;
import mg.etu2624.ticketing.model.Vol;
import mg.etu2624.ticketing.model.VolSiege;
import mg.etu2624.ticketing.model.dto.ReservationDTO;
import mg.etu2624.ticketing.model.view.PrixSiegeView;
import mg.itu.prom16.annotation.Autowired;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.controller.Controller;
import mg.itu.prom16.mapping.DeleteMapping;
import mg.itu.prom16.mapping.GetMapping;
import mg.itu.prom16.mapping.PostMapping;
import mg.itu.prom16.mapping.Url;
import mg.itu.prom16.param.PathVariable;
import mg.itu.prom16.param.RequestBody;
import mg.itu.prom16.param.RequestParam;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.response.RedirectAttributes;
import mg.itu.prom16.response.ResponseBody;
import mg.itu.prom16.response.ResponseEntity;
import mg.itu.prom16.validation.BindingResult;
import mg.itu.prom16.validation.Valid;

@Controller
@Url("/reservations")
public class ReservationController {

    @Autowired
    private EntityManagerFactory emf;

    // Créer une réservation
    @GetMapping
    public String showForm(@RequestParam Long volId, 
                          @RequestParam Long siegeId,
                          Model model) {
        
        EntityManager em = emf.createEntityManager();
        try {
            PrixSiegeView prix = em.createQuery(
                "SELECT p FROM PrixSiegeView p WHERE p.id.volId = :volId AND p.id.siegeId = :siegeId", 
                PrixSiegeView.class)
                .setParameter("volId", volId)
                .setParameter("siegeId", siegeId)
                .getSingleResult();
            Vol vol = em.find(Vol.class, volId);
            Siege siege = em.find(Siege.class, siegeId);
            model.addObject("vol", vol);
            model.addObject("siege", siege);
            model.addObject("prix", prix);
            model.addObject("pageContent", "reservations");   
        }catch(Exception e){
            return "redirect:/";
        }finally {
            em.close();
        }
        return "template/template";
    }

    // Récupération du prix via AJAX
    @GetMapping
    @Url("/prix")
    @ResponseBody
    public ResponseEntity<PrixSiegeView> getPrix(@RequestParam Long volId, 
                                                @RequestParam Long siegeId) {
        EntityManager em = emf.createEntityManager();
        try {
            PrixSiegeView prix = em.createQuery(
                "SELECT p FROM PrixSiegeView p WHERE p.id.volId = :volId AND p.id.siegeId = :siegeId", 
                PrixSiegeView.class)
                .setParameter("volId", volId)
                .setParameter("siegeId", siegeId)
                .getSingleResult();
            if (prix.getPromotion() != null) {
                prix.getPromotion().getVol().setPrixVolClasses(null);
                prix.getPromotion().getVol().setVolSieges(null);
            }
            return ResponseEntity.ok(prix);
        } catch (NoResultException e) {
            return ResponseEntity.notFound();
        } finally {
            em.close();
        }
    }

    // Création de la réservation
    @PostMapping
    public String createReservation(@Valid @RequestBody ReservationDTO dto,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        System.out.println(dto);
        if (dto.getVolId()==null || dto.getSiegeId()==null) {
            return "redirect:/";
        }
        if (result.hasErrors()) {
            System.out.println(result.getErrors());
            redirectAttributes.addFlashAttribute("result", result);
            return "redirect:/reservations?volId=" + dto.getVolId() + "&siegeId=" + dto.getSiegeId();
        }
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();            
            
            Reservation reservation = new Reservation();
            // Traitement du fichier
            if (dto.getPassportPassager().isEmpty())
            throw new Exception("le Passager doit donne un passport pour reserve un siege");

            Siege siege = em.find(Siege.class, dto.getSiegeId());
            reservation.setSiege(siege);
            reservation.setClasseSiege(siege.getClasseSiege());
            reservation.setEmailPassager(dto.getEmailPassager());
            reservation.setNomPassager(dto.getNomPassager());
            
            if (dto.getPromotionId()!=null) {
                reservation.setPromotion(em.find(Promotion.class, dto.getPromotionId()));    
            }
            Vol vol = em.find(Vol.class, dto.getVolId());
            
            // Sauvegarder le fichier
            String fileName = UUID.randomUUID() + "_" + dto.getPassportPassager().getOriginalFileName();
            reservation.setPassportPassager(fileName);
            
            reservation.setVol(vol);
            reservation.setDateReservation(LocalDateTime.now());
            em.persist(reservation);
            
            // sauvegarder le fichier ...
            dto.getPassportPassager().saveFile("/WEB-INF/passport/", fileName);
            
            em.getTransaction().commit();
            redirectAttributes.addFlashAttribute("success", "Réservation effectuée avec succès");
            return "redirect:/vols/view/"+vol.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (e instanceof PersistenceException exception) {
                redirectAttributes.addFlashAttribute("error", JpaConfig.getSqlMessage(exception));  
            }
            if (em.getTransaction().isActive())em.getTransaction().rollback();
            redirectAttributes.addFlashAttribute("result", result);
            return "redirect:/reservations?volId=" + dto.getVolId() + "&siegeId=" + dto.getSiegeId();
        }finally{
            em.close();
        }

        
    }
    // Supprimer une réservation
    @GetMapping
    @RoleRequired("admin")
    @Url("/annuler")
    public String deleteReservation(@RequestParam Long id,RedirectAttributes attributes) {
        EntityManager em = emf.createEntityManager();
        String lien = "/";
        try {
            em.getTransaction().begin();

            Reservation reservation = em.find(Reservation.class, id);
            if (reservation == null) {
                attributes.addFlashAttribute("error", "Réservation introuvable");
                em.getTransaction().commit();
                return "redirect:/";
            }
            lien = "/vols/view/"+reservation.getVol().getId();

            em.remove(reservation);
            em.getTransaction().commit();
            attributes.addFlashAttribute("success", "Réservation annulée");
            return "redirect:"+lien;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            attributes.addFlashAttribute("error", e.getMessage());
            if (e instanceof PersistenceException exception) {
                attributes.addFlashAttribute("error", JpaConfig.getSqlMessage(exception));  
            }
            return "redirect:"+lien;
        } finally {
            em.close();
        }
    }
}