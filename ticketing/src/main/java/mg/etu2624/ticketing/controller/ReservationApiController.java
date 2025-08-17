package mg.etu2624.ticketing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mg.etu2624.ticketing.model.Reservation;
import mg.etu2624.ticketing.model.dto.InitDataResponse;
import mg.etu2624.ticketing.model.dto.PrixSiegeResponse;
import mg.etu2624.ticketing.model.dto.ReservationDTO;
import mg.etu2624.ticketing.service.PdfGeneratorService;
import mg.etu2624.ticketing.service.ReservationService;

// ReservationApiController.java
@RestController
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationService reservationService;
    private final PdfGeneratorService pdfGeneratorService; 

    @Autowired
    public ReservationApiController(ReservationService reservationService,PdfGeneratorService pdfGeneratorService) {
        this.reservationService = reservationService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping("/init")
    public ResponseEntity<InitDataResponse> getInitialData(
            @RequestParam Long volId, 
            @RequestParam Long siegeId) {
        
        InitDataResponse response = reservationService.getInitialData(volId, siegeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prix")
    public ResponseEntity<?> getPrixSiege(
            @RequestParam Long volId,
            @RequestParam Long siegeId,
            @RequestParam Long categorieId) {
        try {
            PrixSiegeResponse prix = reservationService.calculerPrixSiege(volId, siegeId, categorieId);
            return ResponseEntity.ok(prix);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("success", false, "error", e.getMessage())
            );
        }
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReservation(
            ReservationDTO dto,
            BindingResult bindingResult,
            HttpServletRequest request) {

        // Validation des paramètres obligatoires
        if (dto.getVolId() == null || dto.getSiegeId() == null) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "volId and siegeId are required")
            );
        }

        // Validation des champs
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(
                Map.of("message", "Validation failed", "errors", errors)
            );
        }

        try {
            reservationService.createReservation(dto,request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Réservation effectuée avec succès"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("success", false, "error", e.getMessage())
            );
        }
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadReservationPdf(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return pdfGeneratorService.generateReservationPdf(reservation);
    }
}