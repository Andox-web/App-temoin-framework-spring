package mg.etu2624.ticketing.controller;

import org.hibernate.Hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import mg.etu2624.ticketing.model.Utilisateur;
import mg.itu.prom16.annotation.Autowired;
import mg.itu.prom16.controller.Controller;
import mg.itu.prom16.mapping.GetMapping;
import mg.itu.prom16.mapping.PostMapping;
import mg.itu.prom16.mapping.Url;
import mg.itu.prom16.param.RequestBody;
import mg.itu.prom16.response.RedirectAttributes;
import mg.itu.prom16.servlet.Session;
import mg.itu.prom16.validation.BindingResult;
import mg.itu.prom16.validation.Email;
import mg.itu.prom16.validation.NotBlank;
import mg.itu.prom16.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    @Url("/admin/connexion")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping
    @Url("/admin/connexion")
    public String handleLogin(@Valid @RequestBody UtilisateurLogin login ,Session session, BindingResult result, RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("value", result);
            return "redirect:/admin/connexion";
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Query query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email AND u.motDePasse = :password");
            query.setParameter("email", login.email);
            query.setParameter("password", login.password);
            Utilisateur user = (Utilisateur) query.getSingleResult();
            
            if (user != null) {
                Hibernate.initialize(user.getRoles()); 
                session.setAttribute("user", user);
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid email or password");
                redirectAttributes.addFlashAttribute("value", result);
                return "redirect:/admin/connexion";
            }
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback(); 
            }

            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("value", result);
            return "redirect:/admin/connexion";
        } finally {
            if (transaction.isActive()) {
                transaction.commit(); 
            }
            em.close();
        }
    }

    public static class UtilisateurLogin {

        @Email
        @NotBlank
        String email;

        @NotBlank
        String password;
    }

    @GetMapping
    @Url("/admin/deconnexion")
    public String handleLogout(Session session) {
        session.invalidate();
        return "redirect:/";
    }
}
