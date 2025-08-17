package mg.etu2624.ticketing.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import mg.etu2624.ticketing.model.Categorie;
import mg.etu2624.ticketing.model.Promotion;
import mg.etu2624.ticketing.model.Reservation;
import mg.etu2624.ticketing.model.Siege;
import mg.etu2624.ticketing.model.Vol;
import mg.etu2624.ticketing.model.VuePrixSiege;
import mg.etu2624.ticketing.model.dto.InitDataResponse;
import mg.etu2624.ticketing.model.dto.PrixSiegeResponse;
import mg.etu2624.ticketing.model.dto.PromotionResponse;
import mg.etu2624.ticketing.model.dto.ReservationDTO;
import mg.etu2624.ticketing.model.dto.ReservationRequest;
import mg.etu2624.ticketing.model.dto.ReservationResponse;
import mg.etu2624.ticketing.repository.CategorieRepository;
import mg.etu2624.ticketing.repository.PromotionRepository;
import mg.etu2624.ticketing.repository.ReservationRepository;
import mg.etu2624.ticketing.repository.SiegeRepository;
import mg.etu2624.ticketing.repository.VolRepository;
import mg.etu2624.ticketing.repository.VuePrixSiegeRepository;

// ReservationService.java
@Service
@Transactional
public class ReservationService {

    private final SiegeRepository siegeRepository;
    private final VolRepository volRepository;
    private final CategorieRepository categorieRepository;
    private final VuePrixSiegeRepository vuePrixSiegeRepository;
    private final PromotionRepository promotionRepository;
    private final ReservationRepository reservationRepository;
    private final ServletContext servletContext;

    @Autowired
    public ReservationService(
            SiegeRepository siegeRepository,
            VolRepository volRepository,
            CategorieRepository categorieRepository,
            VuePrixSiegeRepository vuePrixSiegeRepository,
            PromotionRepository promotionRepository,
            ReservationRepository reservationRepository,
            ServletContext servletContext) {
        this.siegeRepository = siegeRepository;
        this.volRepository = volRepository;
        this.categorieRepository = categorieRepository;
        this.vuePrixSiegeRepository = vuePrixSiegeRepository;
        this.promotionRepository = promotionRepository;
        this.servletContext = servletContext;
        this.reservationRepository = reservationRepository;
    }

    public InitDataResponse getInitialData(Long volId, Long siegeId) {
        Vol vol = volRepository.findById(volId)
                .orElseThrow(() -> new EntityNotFoundException("Vol non trouvé"));
        
        Siege siege = siegeRepository.findByIdWithClasse(siegeId)
                .orElseThrow(() -> new EntityNotFoundException("Siège non trouvé"));
        
        List<Categorie> categories = categorieRepository.findAll();
        
        return new InitDataResponse(vol, siege, categories);
    }

    public PrixSiegeResponse calculerPrixSiege(Long volId, Long siegeId, Long categorieId) {
        Optional<VuePrixSiege> prixSiegeOpt = vuePrixSiegeRepository.findPrixSiege(volId, siegeId, categorieId);
        
        if (prixSiegeOpt.isEmpty()) {
            throw new IllegalStateException("Prix non disponible pour cette configuration");
        }
        
        VuePrixSiege prixSiege = prixSiegeOpt.get();
        
        PromotionResponse promotionResponse = null;
        if (prixSiege.getPromotionId() != null) {
            Optional<Promotion> promotionOpt = promotionRepository.findById(prixSiege.getPromotionId());
            if (promotionOpt.isPresent()) {
                Promotion promotion = promotionOpt.get();
                promotionResponse = new PromotionResponse(
                    promotion.getId(),
                    promotion.getPourcentageReduction()
                );
            }
        }
        
        return new PrixSiegeResponse(
            prixSiege.getPrixBase(),
            prixSiege.getPrixFinal(),
            promotionResponse,
            prixSiege.getCategorieNom()
        );
    }
    
    private Map<String, String> validateReservation(ReservationRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        if (request.getNomPassager() == null || request.getNomPassager().trim().isEmpty()) {
            errors.put("nomPassager", "Le nom complet est requis");
        }
        
        if (request.getEmailPassager() == null || !isValidEmail(request.getEmailPassager())) {
            errors.put("emailPassager", "Email invalide");
        }
        
        if (request.getPassportPassager() == null || request.getPassportPassager().isEmpty()) {
            errors.put("passportPassager", "Le passeport est requis");
        } else if (request.getPassportPassager().getSize() > 200 * 1024 * 1024) {
            errors.put("passportPassager", "Le fichier est trop volumineux (max 200MB)");
        } else if (!"application/pdf".equals(request.getPassportPassager().getContentType())) {
            errors.put("passportPassager", "Seuls les fichiers PDF sont acceptés");
        }
        
        return errors;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
    
     public void createReservation(ReservationDTO dto,HttpServletRequest request) throws Exception {
        // Validation du fichier
        if (dto.getPassportPassager().isEmpty()) {
            throw new IllegalArgumentException("Le passager doit fournir un passeport pour réserver un siège");
        }

        // Récupération des entités
        Siege siege = siegeRepository.findById(dto.getSiegeId())
                .orElseThrow(() -> new EntityNotFoundException("Siège non trouvé"));
        
        Vol vol = volRepository.findById(dto.getVolId())
                .orElseThrow(() -> new EntityNotFoundException("Vol non trouvé"));

        Categorie categorie = null;
        if (dto.getCategorieId() != null) {
            categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));
        }

        Promotion promotion = null;
        if (dto.getPromotionId() != null) {
            promotion = promotionRepository.findById(dto.getPromotionId())
                    .orElse(null); // La promotion n'est pas obligatoire
        }

        // Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setSiege(siege);
        reservation.setClasseSiege(siege.getClasseSiege());
        reservation.setVol(vol);
        reservation.setPromotion(promotion);
        reservation.setCategorie(categorie);
        reservation.setNomPassager(dto.getNomPassager());
        reservation.setEmailPassager(dto.getEmailPassager());
        reservation.setDateReservation(LocalDateTime.now());

        // Sauvegarde du fichier
        String fileName = UUID.randomUUID() + "_" + dto.getPassportPassager().getOriginalFilename();
        reservation.setPassportPassager(fileName);
        
        // Sauvegarde de la réservation
        reservationRepository.save(reservation);
        
        // Sauvegarde physique du fichier
        savePassportFile(request,dto.getPassportPassager(), fileName);
    }

    private void savePassportFile(HttpServletRequest request, MultipartFile file, String fileName) throws IOException {
        String uploadDir = "F:/tomcat/webapps/Ticketing/resources/passport/";
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        System.out.println("Fichier enregistré dans : " + filePath.toAbsolutePath());
    }
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée"));
    }
}
