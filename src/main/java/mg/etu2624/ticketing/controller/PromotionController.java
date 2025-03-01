package mg.etu2624.ticketing.controller;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import mg.etu2624.ticketing.config.JpaConfig;
import mg.etu2624.ticketing.model.ClasseSiege;
import mg.etu2624.ticketing.model.Promotion;
import mg.etu2624.ticketing.model.Vol;
import mg.etu2624.ticketing.model.dto.PromotionDTO;
import mg.itu.prom16.annotation.Autowired;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.controller.Controller;
import mg.itu.prom16.mapping.GetMapping;
import mg.itu.prom16.mapping.PostMapping;
import mg.itu.prom16.mapping.Url;
import mg.itu.prom16.param.PathVariable;
import mg.itu.prom16.param.RequestBody;
import mg.itu.prom16.param.RequestParam;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.response.RedirectAttributes;
import mg.itu.prom16.response.ResponseBody;
import mg.itu.prom16.validation.BindingResult;
import mg.itu.prom16.validation.Valid;

@Controller
@Url("/admin/promotions")
@RoleRequired("admin")
public class PromotionController {

    @Autowired
    private EntityManagerFactory emf;

    // Afficher le formulaire
    @GetMapping
    public String showForm(@RequestParam Long volId , Model model) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Vol> vols = em.createQuery("FROM Vol", Vol.class).getResultList();
            List<ClasseSiege> classes = em.createQuery("FROM ClasseSiege", ClasseSiege.class).getResultList();
            
            model.addObject("vols", vols);
            model.addObject("classes", classes);
            
            // Charger promotions si volId présent
            if (volId != null) {
                List<Promotion> promotions = em.createQuery(
                    "FROM Promotion WHERE vol.id = :volId", Promotion.class)
                    .setParameter("volId", volId)
                    .getResultList();
                model.addObject("promotions", promotions);
            }
            
            model.addObject("pageContent", "promotions");
        } finally {
            em.close();
        }
        return "template/template";
    }

    // Soumettre le formulaire
    @PostMapping
    public String createPromotion(@Valid @RequestBody PromotionDTO dto, BindingResult result, RedirectAttributes model) {
        EntityManager em = emf.createEntityManager();
        try {
            if (result.hasErrors()) {
                model.addFlashAttribute("value", result.getErrors());
                return "redirect:/admin/promotions?volId=" + dto.getVolId();
            }
            
            em.getTransaction().begin();
            
            Promotion promotion = new Promotion();
            promotion.setVol(em.find(Vol.class, dto.getVolId()));
            promotion.setDescription(dto.getDescription());
            promotion.setPourcentageReduction(dto.getPourcentageReduction());
            promotion.setClasseSiege(em.find(ClasseSiege.class, dto.getClasseSiegeId()));
            promotion.setLimiteSieges(dto.getLimiteSieges());
            
            em.persist(promotion);
            em.getTransaction().commit();
            
            return "redirect:/admin/promotions?volId=" + dto.getVolId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            model.addFlashAttribute("error", e.getMessage());
            if (e instanceof jakarta.persistence.PersistenceException exception) {
                model.addFlashAttribute("error", JpaConfig.getSqlMessage(exception));
            }
            return "redirect:/admin/promotions?volId=" + dto.getVolId();
        } finally {
            em.close();
        }
    }

    // API pour JS
    @GetMapping
    @ResponseBody
    @Url("/vols/{volId}")
    public List<Promotion> getPromotionsByVol(@PathVariable Long volId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("FROM Promotion WHERE vol.id = :volId", Promotion.class)
                    .setParameter("volId", volId)
                    .getResultList().stream().map(p -> {
                        p.getVol().setPrixVolClasses(null);
                        p.getVol().setVolSieges(null);
                        return p;
                    }).toList();
        } finally {
            em.close();
        }
    }
}