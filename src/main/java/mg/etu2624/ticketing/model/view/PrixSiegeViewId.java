package mg.etu2624.ticketing.model.view;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PrixSiegeViewId implements Serializable {

    @Column(name = "vol_id")
    private Long volId;

    @Column(name = "siege_id")
    private Long siegeId;

    public PrixSiegeViewId() {}

    public PrixSiegeViewId(Long volId, Long siegeId) {
        this.volId = volId;
        this.siegeId = siegeId;
    }

    public Long getVolId() {
        return volId;
    }

    public void setVolId(Long volId) {
        this.volId = volId;
    }

    public Long getSiegeId() {
        return siegeId;
    }

    public void setSiegeId(Long siegeId) {
        this.siegeId = siegeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrixSiegeViewId)) return false;
        PrixSiegeViewId that = (PrixSiegeViewId) o;
        return Objects.equals(getVolId(), that.getVolId()) &&
               Objects.equals(getSiegeId(), that.getSiegeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVolId(), getSiegeId());
    }
}

