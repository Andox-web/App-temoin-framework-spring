package mg.etu2624.ticketing.config;

import mg.etu2624.ticketing.model.Utilisateur;

public class RoleMatcher implements mg.itu.prom16.role.RoleMatcher {

    @Override
    public boolean matches(Object object, String role) {
        if (object instanceof Utilisateur user) {
            return user.getRoles().stream().anyMatch(r -> r.getNom().equals(role));
        } else {
            return false; 
        }
    }

}
