package mg.etu2624.ticketing.model.view;

import java.io.Serializable;

public class OccupationSiegeId implements Serializable {

    private Long volId;

    private String classeSiege;

    public Long getVolId() {
        return volId;
    }
    public void setVolId(Long volId) {
        this.volId = volId;
    }
    public String getClasseSiege() {
        return classeSiege;
    }
    public void setClasseSiege(String classeSiege) {
        this.classeSiege = classeSiege;
    }
    
}