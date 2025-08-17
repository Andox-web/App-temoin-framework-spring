package mg.etu2624.ticketing.model.dto;

import java.util.List;

import mg.etu2624.ticketing.model.Categorie;
import mg.etu2624.ticketing.model.Siege;
import mg.etu2624.ticketing.model.Vol;

// InitDataResponse.java
public class InitDataResponse {
    private Vol vol;
    private Siege siege;
    private List<Categorie> categories;
    
    public InitDataResponse(Vol vol, Siege siege, List<Categorie> categories) {
        this.vol = vol;
        this.siege = siege;
        this.categories = categories;
    }
    public Vol getVol() {
        return vol;
    }
    public void setVol(Vol vol) {
        this.vol = vol;
    }
    public Siege getSiege() {
        return siege;
    }
    public void setSiege(Siege siege) {
        this.siege = siege;
    }
    public List<Categorie> getCategories() {
        return categories;
    }
    public void setCategories(List<Categorie> categories) {
        this.categories = categories;
    }

    // Getters & Setters
    
}