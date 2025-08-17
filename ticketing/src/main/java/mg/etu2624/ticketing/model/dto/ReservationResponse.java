package mg.etu2624.ticketing.model.dto;

import java.util.Map;

// ReservationResponse.java
public class ReservationResponse {
    private boolean success;
    private Long reservationId;
    private Map<String, String> errors;

    // Constructors
    public ReservationResponse(boolean success, Long reservationId) {
        this.success = success;
        this.reservationId = reservationId;
    }

    public ReservationResponse(boolean success, Map<String, String> errors) {
        this.success = success;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    // Getters & Setters
    
}
