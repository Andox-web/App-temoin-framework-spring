package mg.etu2624.ticketing.controller;

import mg.etu2624.ticketing.config.RoleMatcher;
import mg.etu2624.ticketing.model.Utilisateur;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.controller.Controller;
import mg.itu.prom16.mapping.GetMapping;
import mg.itu.prom16.mapping.Url;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.servlet.Session;

@Controller
public class IndexController {
    
    @GetMapping
    @Url("/")    
    public String index(Session session, Model model) {
        if (session.getAttribute("user") instanceof Utilisateur utilisateur && new RoleMatcher().matches(utilisateur, "admin")) {
            return "redirect:/admin";
        } else {
            model.addObject("message", "Bienvenue sur notre application de réservation de vols !");
        }
        model.addObject("pageContent", "index");
        return "template/template";
    }

    @GetMapping
    @Url("/admin")
    @RoleRequired("admin")
    public String indexAdmin(Session session, Model model){
        if (session.getAttribute("user") instanceof Utilisateur utilisateur) {
            model.addObject("message", "Bonjour, "+utilisateur.getNom()+" ! Vous êtes connecté.");
        }else{
            return "redirect:/admin/connexion";
        }
        model.addObject("pageContent", "index");
        return "template/template";
    }
}