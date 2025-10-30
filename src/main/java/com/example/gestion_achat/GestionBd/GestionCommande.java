package com.example.gestion_achat.GestionBd;

import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.Class.Utilisateur;
import com.example.gestion_achat.Class.CommandeProduit;
import com.example.gestion_achat.Class.CommandeDetailsDTO;
import com.example.gestion_achat.Class.CommandeEnCoursDTO;
import com.example.gestion_achat.Class.CommandeValideePaiementDTO;
import com.example.gestion_achat.Repository.CommandeRepository;
import com.example.gestion_achat.Repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class GestionCommande {

    private static final Logger logger = LoggerFactory.getLogger(GestionCommande.class);
    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public GestionCommande(CommandeRepository commandeRepository, UtilisateurRepository utilisateurRepository) {
        this.commandeRepository = commandeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

    public List<CommandeDetailsDTO> getAllCommandeDetails() {
        logger.info("Récupération de toutes les commandes avec détails depuis la vue");
        return commandeRepository.findAllCommandeDetails();
    }

    public List<CommandeEnCoursDTO> getAllCommandesEnCours() {
        logger.info("Récupération de toutes les commandes en cours depuis la vue");
        return commandeRepository.findAllCommandesEnCours();
    }

    public List<CommandeValideePaiementDTO> getAllCommandesValideesPaiements() {
        logger.info("Récupération de toutes les commandes validées avec paiements depuis la vue");
        return commandeRepository.findAllCommandesValideesPaiements();
    }

    public Optional<Commande> getCommandeById(Long idCommande) {
        logger.info("Récupération de la commande avec ID : {}", idCommande);
        return commandeRepository.findById(idCommande);
    }

    public Commande ajouterCommande(Commande commande) {
        logger.info("Ajout d'une commande pour l'acheteur ID : {}",
                commande.getAcheteur() != null ? commande.getAcheteur().getIdUtilisateur() : "null");

        // 🔹 CORRECTION : Vérifier que l'utilisateur existe et le récupérer de la base de données
        if (commande.getAcheteur() != null && commande.getAcheteur().getIdUtilisateur() != null) {
            Integer idUtilisateur = commande.getAcheteur().getIdUtilisateur();
            Utilisateur utilisateurExistant = utilisateurRepository.findById(idUtilisateur)
                    .orElseThrow(() -> {
                        logger.error("Utilisateur avec ID {} introuvable", idUtilisateur);
                        return new IllegalArgumentException("Utilisateur avec ID " + idUtilisateur + " introuvable");
                    });

            // Remplacer l'objet transitoire par l'entité persistante
            commande.setAcheteur(utilisateurExistant);
            logger.info("Utilisateur trouvé : {} ({})", utilisateurExistant.getNom(), utilisateurExistant.getEmail());
        } else {
            logger.error("Aucun acheteur spécifié dans la commande");
            throw new IllegalArgumentException("L'acheteur est obligatoire pour créer une commande");
        }

        return commandeRepository.save(commande);
    }

    public Optional<Commande> modifierCommande(Long idCommande, Commande commandeModifiee) {
        return commandeRepository.findById(idCommande).map(commande -> {
            if (commande.getStatut() == Commande.Statut.VALIDEE || commande.getStatut() == Commande.Statut.ANNULEE) {
                logger.warn("Tentative de modification d'une commande au statut non modifiable : {}", commande.getStatut());
                throw new IllegalStateException("Une commande " + commande.getStatut() + " ne peut pas être modifiée");
            }
            if (commandeModifiee.getTotal().compareTo(BigDecimal.ZERO) < 0) {
                logger.warn("Tentative de modification d'une commande avec total négatif : {}", commandeModifiee.getTotal());
                throw new IllegalArgumentException("Le total de la commande ne peut pas être négatif");
            }

            // 🔹 Vérifier l'acheteur si modifié
            if (commandeModifiee.getAcheteur() != null && commandeModifiee.getAcheteur().getIdUtilisateur() != null) {
                Integer idUtilisateur = commandeModifiee.getAcheteur().getIdUtilisateur();
                Utilisateur utilisateurExistant = utilisateurRepository.findById(idUtilisateur)
                        .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec ID " + idUtilisateur + " introuvable"));
                commande.setAcheteur(utilisateurExistant);
            }

            commande.setTotal(commandeModifiee.getTotal());
            logger.info("Commande modifiée avec ID : {}", idCommande);
            return commandeRepository.save(commande);
        });
    }

    public boolean supprimerCommande(Long idCommande) {
        if (commandeRepository.existsById(idCommande)) {
            Commande commande = commandeRepository.findById(idCommande).get();
            if (commande.getStatut() == Commande.Statut.VALIDEE) {
                logger.warn("Tentative de suppression d'une commande validée avec ID : {}", idCommande);
                throw new IllegalStateException("Une commande validée ne peut pas être supprimée");
            }
            commandeRepository.deleteById(idCommande);
            logger.info("Commande supprimée avec ID : {}", idCommande);
            return true;
        }
        logger.warn("Tentative de suppression d'une commande inexistante avec ID : {}", idCommande);
        return false;
    }
}