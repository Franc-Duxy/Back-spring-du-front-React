package com.example.gestion_achat.GestionBd;

import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.Class.Paiement;
import com.example.gestion_achat.Class.PaiementDetailsDTO;
import com.example.gestion_achat.Repository.CommandeRepository;
import com.example.gestion_achat.Repository.PaiementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class GestionPaiement {

    private static final Logger logger = LoggerFactory.getLogger(GestionPaiement.class);
    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;

    public GestionPaiement(PaiementRepository paiementRepository, CommandeRepository commandeRepository) {
        this.paiementRepository = paiementRepository;
        this.commandeRepository = commandeRepository;
    }

    public List<Paiement> getAllPaiements() {
        logger.info("Récupération de tous les paiements");
        return paiementRepository.findAll();
    }

    // Nouvelle méthode pour récupérer les détails depuis la vue
    public List<PaiementDetailsDTO> getAllPaiementDetails() {
        logger.info("Récupération de tous les paiements avec détails depuis la vue");
        return paiementRepository.findAllPaiementDetails();
    }

    public Optional<Paiement> getPaiementById(Integer idPaiement) {
        logger.info("Récupération du paiement avec ID : {}", idPaiement);
        return paiementRepository.findById(idPaiement);
    }

    @Transactional
    public Paiement ajouterPaiement(Paiement paiement) {
        Long idCommande = paiement.getCommande() != null ? paiement.getCommande().getIdCommande() : null;
        if (idCommande == null) {
            logger.error("Aucune commande spécifiée pour le paiement");
            throw new IllegalArgumentException("L'ID de la commande est requis.");
        }

        if (paiement.getMethodePaiement() == null || paiement.getMethodePaiement().trim().isEmpty()) {
            logger.error("La méthode de paiement est requise");
            throw new IllegalArgumentException("La méthode de paiement est requise.");
        }

        Commande commande = commandeRepository.findById(idCommande)
                .orElseThrow(() -> {
                    logger.error("Commande introuvable avec ID : {}", idCommande);
                    return new IllegalArgumentException("Commande introuvable : " + idCommande);
                });

        // Définir le montant automatiquement à partir du total de la commande
        paiement.setMontant(commande.getTotal());
        paiement.setCommande(commande);
        Paiement saved = paiementRepository.save(paiement);

        commande.setStatut(Commande.Statut.VALIDEE);
        commandeRepository.save(commande);

        logger.info("Paiement ajouté pour la commande ID : {}", idCommande);
        return saved;
    }

    @Transactional
    public Optional<Paiement> modifierPaiement(Integer idPaiement, Paiement paiementModifie) {
        Optional<Paiement> paiementOptional = paiementRepository.findById(idPaiement);
        if (paiementOptional.isEmpty()) {
            return Optional.empty();
        }

        Paiement paiement = paiementOptional.get();
        Commande commandeExistante = paiement.getCommande();
        if (commandeExistante.getStatut() == Commande.Statut.VALIDEE) {
            logger.warn("Tentative de modification d'une commande déjà payée avec ID : {}", commandeExistante.getIdCommande());
            throw new IllegalStateException("La commande déjà payée ne peut pas être modifiée");
        } else if (commandeExistante.getStatut() == Commande.Statut.ANNULEE) {
            logger.warn("Tentative de modification d'une commande annulée avec ID : {}", commandeExistante.getIdCommande());
            throw new IllegalStateException("Une commande annulée ne peut pas être modifiée");
        }

        Long idCommande = paiementModifie.getCommande() != null ? paiementModifie.getCommande().getIdCommande() : null;
        if (idCommande != null) {
            Commande commande = commandeRepository.findById(idCommande)
                    .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + idCommande));
            paiement.setCommande(commande);
        }
        if (!commandeExistante.getTotal().equals(paiementModifie.getMontant())) {
            logger.warn("Le nouveau montant ({}) ne correspond pas au total de la commande ({})",
                    paiementModifie.getMontant(), commandeExistante.getTotal());
            throw new IllegalArgumentException("Le montant du paiement doit correspondre au total de la commande.");
        }
        paiement.setMontant(paiementModifie.getMontant());
        paiement.setMethodePaiement(paiementModifie.getMethodePaiement());
        logger.info("Paiement modifié avec ID : {}", idPaiement);
        return Optional.of(paiementRepository.save(paiement));
    }

    @Transactional
    public boolean supprimerPaiement(Integer idPaiement) {
        if (paiementRepository.existsById(idPaiement)) {
            Paiement paiement = paiementRepository.findById(idPaiement).get();
            Commande commande = paiement.getCommande();
            if (commande.getStatut() == Commande.Statut.VALIDEE) {
                logger.warn("Tentative de suppression d'un paiement pour une commande déjà payée : {}", idPaiement);
                throw new IllegalStateException("Le paiement d'une commande déjà payée ne peut pas être supprimé");
            }
            if (commande != null) {
                commande.setStatut(Commande.Statut.EN_COURS);
                commandeRepository.save(commande);
            }
            paiementRepository.deleteById(idPaiement);
            logger.info("Paiement supprimé avec ID : {}", idPaiement);
            return true;
        }
        logger.warn("Tentative de suppression d'un paiement inexistant avec ID : {}", idPaiement);
        return false;
    }
}