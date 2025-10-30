package com.example.gestion_achat.Controller;

import com.example.gestion_achat.Class.Paiement;
import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.Class.PaiementDTO;
import com.example.gestion_achat.Class.PaiementDetailsDTO;
import com.example.gestion_achat.GestionBd.GestionPaiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/paiement")
public class PaiementController {

    private final GestionPaiement gestionPaiement;

    @Autowired
    public PaiementController(GestionPaiement gestionPaiement) {
        this.gestionPaiement = gestionPaiement;
    }

    @GetMapping("/tous")
    public ResponseEntity<List<PaiementDetailsDTO>> getAllPaiements() {
        List<PaiementDetailsDTO> paiements = gestionPaiement.getAllPaiementDetails();
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/{idPaiement}")
    public ResponseEntity<PaiementDTO> getPaiementById(@PathVariable("idPaiement") Integer idPaiement) {
        Optional<Paiement> paiement = gestionPaiement.getPaiementById(idPaiement);
        return paiement.map(p -> ResponseEntity.ok(new PaiementDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/ajouter")
    public ResponseEntity<PaiementDTO> ajouterPaiement(@RequestBody PaiementDTO paiementDTO) {
        if (paiementDTO.getIdCommande() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (paiementDTO.getMethodePaiement() == null || paiementDTO.getMethodePaiement().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Paiement paiementToSave = new Paiement();
        Commande commande = new Commande();
        commande.setIdCommande(paiementDTO.getIdCommande());
        paiementToSave.setCommande(commande);
        paiementToSave.setMethodePaiement(paiementDTO.getMethodePaiement());

        Paiement nouveauPaiement = gestionPaiement.ajouterPaiement(paiementToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PaiementDTO(nouveauPaiement));
    }

    @PutMapping("/modifier/{idPaiement}")
    public ResponseEntity<PaiementDTO> modifierPaiement(
            @PathVariable("idPaiement") Integer idPaiement,
            @RequestBody PaiementDTO paiementDTO) {
        if (paiementDTO.getMethodePaiement() == null || paiementDTO.getMethodePaiement().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (paiementDTO.getMontant() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Paiement paiementToUpdate = new Paiement();
        paiementToUpdate.setMontant(paiementDTO.getMontant());
        paiementToUpdate.setMethodePaiement(paiementDTO.getMethodePaiement());
        if (paiementDTO.getIdCommande() != null) {
            Commande commande = new Commande();
            commande.setIdCommande(paiementDTO.getIdCommande());
            paiementToUpdate.setCommande(commande);
        }

        Optional<Paiement> paiementModifie = gestionPaiement.modifierPaiement(idPaiement, paiementToUpdate);
        return paiementModifie.map(p -> ResponseEntity.ok(new PaiementDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/supprimer/{idPaiement}")
    public ResponseEntity<Void> supprimerPaiement(@PathVariable("idPaiement") Integer idPaiement) {
        if (gestionPaiement.supprimerPaiement(idPaiement)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue s'est produite : " + ex.getMessage());
    }
}