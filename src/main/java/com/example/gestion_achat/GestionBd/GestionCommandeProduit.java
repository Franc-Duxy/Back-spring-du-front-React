package com.example.gestion_achat.GestionBd;

import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.Class.CommandeProduit;
import com.example.gestion_achat.Class.CommandeProduitId;
import com.example.gestion_achat.Class.Produit;
import com.example.gestion_achat.Repository.CommandeProduitRepository;
import com.example.gestion_achat.Repository.CommandeRepository;
import com.example.gestion_achat.Repository.ProduitRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors; // Ajout de cette ligne

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class GestionCommandeProduit {

    private static final Logger logger = LoggerFactory.getLogger(GestionCommandeProduit.class);
    private final CommandeProduitRepository commandeProduitRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final EntityManager entityManager;
    private static final int SEUIL_STOCK_BAS = 5;

    @Autowired
    public GestionCommandeProduit(CommandeProduitRepository commandeProduitRepository,
                                  ProduitRepository produitRepository,
                                  CommandeRepository commandeRepository,
                                  EntityManager entityManager) {
        this.commandeProduitRepository = commandeProduitRepository;
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
        this.entityManager = entityManager;
    }

    public CommandeProduit save(CommandeProduit commandeProduit) {
        return commandeProduitRepository.save(commandeProduit);
    }

    @Transactional
    public CommandeProduit ajouterCommandeProduit(CommandeProduit commandeProduit) {
        if (commandeProduit.getIdCommande() == null || commandeProduit.getIdProduit() == null) {
            throw new IllegalArgumentException("Les IDs de commande et de produit doivent être spécifiés.");
        }

        if (commandeProduit.getQuantite() <= 0) {
            logger.warn("Tentative d'ajout avec quantité invalide : {}", commandeProduit.getQuantite());
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }

        Commande commande = commandeRepository.findById(commandeProduit.getIdCommande())
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + commandeProduit.getIdCommande()));

        Produit produit = produitRepository.findById(commandeProduit.getIdProduit())
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable : " + commandeProduit.getIdProduit()));

        // Vérifier si le produit existe déjà dans la commande
        CommandeProduitId idExistant = new CommandeProduitId(commandeProduit.getIdCommande(), commandeProduit.getIdProduit());
        Optional<CommandeProduit> existing = commandeProduitRepository.findById(idExistant);

        if (existing.isPresent()) {
            // Produit déjà dans la commande → on additionne la quantité
            CommandeProduit cp = existing.get();
            int nouvelleQuantite = cp.getQuantite() + commandeProduit.getQuantite();

            // Vérifier le stock
            if (produit.getStock() < commandeProduit.getQuantite()) {
                throw new IllegalArgumentException("Stock insuffisant pour ajouter " + commandeProduit.getQuantite() +
                        " unités. Stock actuel : " + produit.getStock());
            }

            // Mettre à jour
            produit.setStock(produit.getStock() - commandeProduit.getQuantite());
            cp.setQuantite(nouvelleQuantite);
            cp.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(nouvelleQuantite)));

            CommandeProduit saved = commandeProduitRepository.saveAndFlush(cp);

            // Mise à jour du total de la commande
            commandeRepository.updateTotalById(commande.getIdCommande());

            logger.info("Quantité additionnée pour produit ID {} dans commande ID {} : {} → {}",
                    produit.getIdProduit(), commande.getIdCommande(), cp.getQuantite() - commandeProduit.getQuantite(), nouvelleQuantite);

            if (produit.getStock() < SEUIL_STOCK_BAS) {
                logger.warn("Stock bas pour le produit {} : {} unités restantes", produit.getNom(), produit.getStock());
            }

            return saved;

        } else {
            // Produit pas encore dans la commande → ajout normal
            if (produit.getStock() < commandeProduit.getQuantite()) {
                throw new IllegalArgumentException("Stock insuffisant pour " + produit.getNom());
            }

            produit.setStock(produit.getStock() - commandeProduit.getQuantite());
            produitRepository.save(produit);

            commandeProduit.setCommande(commande);
            commandeProduit.setProduit(produit);
            commandeProduit.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(commandeProduit.getQuantite())));

            CommandeProduit saved = commandeProduitRepository.saveAndFlush(commandeProduit);

            commandeRepository.updateTotalById(commande.getIdCommande());

            logger.info("Nouveau produit ajouté dans commande ID {} : produit ID {}",
                    saved.getIdCommande(), saved.getIdProduit());

            if (produit.getStock() < SEUIL_STOCK_BAS) {
                logger.warn("Stock bas pour le produit {} : {} unités restantes", produit.getNom(), produit.getStock());
            }

            return saved;
        }
    }

    public List<CommandeProduit> getAllCommandeProduits() {
        logger.info("Récupération de tous les éléments de commande");
        return commandeProduitRepository.findAll();
    }

    public Optional<CommandeProduit> getCommandeProduitById(CommandeProduitId id) {
        logger.info("Récupération de l'élément de commande avec ID : {}", id);
        return commandeProduitRepository.findById(id);
    }

    @Transactional
    public Optional<CommandeProduit> modifierCommandeProduit(CommandeProduitId id, CommandeProduit commandeProduitModifie) {
        return commandeProduitRepository.findById(id).map(commandeProduit -> {
            Produit produit = produitRepository.findById(commandeProduit.getIdProduit())
                    .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
            if (commandeProduitModifie.getQuantite() <= 0) {
                logger.warn("Tentative de modification avec quantité invalide : {}", commandeProduitModifie.getQuantite());
                throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
            }
            int stockApresModification = produit.getStock() + commandeProduit.getQuantite() - commandeProduitModifie.getQuantite();
            if (stockApresModification < 0) {
                logger.warn("Stock insuffisant pour modifier le produit ID {} : actuel {}, demandé {}",
                        produit.getIdProduit(), produit.getStock(), commandeProduitModifie.getQuantite());
                throw new IllegalArgumentException("Stock insuffisant pour modifier le produit " + produit.getNom());
            }

            produit.setStock(stockApresModification);
            produitRepository.save(produit);

            commandeProduit.setQuantite(commandeProduitModifie.getQuantite());
            commandeProduit.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(commandeProduitModifie.getQuantite())));

            CommandeProduit saved = commandeProduitRepository.saveAndFlush(commandeProduit);

            // Mise à jour du total avec la requête JPQL
            commandeRepository.updateTotalById(saved.getIdCommande());

            if (stockApresModification < SEUIL_STOCK_BAS) {
                logger.warn("Stock bas pour le produit {} : {} unités restantes", produit.getNom(), stockApresModification);
            }
            logger.info("Élément de commande modifié avec ID : {}", id);
            return saved;
        });
    }

    @Transactional
    public boolean supprimerCommandeProduit(CommandeProduitId id) {
        if (commandeProduitRepository.existsById(id)) {
            CommandeProduit commandeProduit = commandeProduitRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("CommandeProduit introuvable avec ID : " + id));
            Produit produit = produitRepository.findById(commandeProduit.getIdProduit())
                    .orElseThrow(() -> new IllegalArgumentException("Produit introuvable : " + commandeProduit.getIdProduit()));

            produit.setStock(produit.getStock() + commandeProduit.getQuantite());
            produitRepository.save(produit);

            commandeProduitRepository.deleteById(id);

            // Mise à jour du total avec la requête JPQL
            commandeRepository.updateTotalById(commandeProduit.getIdCommande());

            logger.info("Élément de commande supprimé avec ID : {}", id);
            return true;
        }
        logger.warn("Tentative de suppression d'un élément de commande inexistant avec ID : {}", id);
        return false;
    }

    @Transactional
    public List<CommandeProduit> ajouterMultipleCommandeProduits(List<CommandeProduit> commandeProduits) {
        if (commandeProduits == null || commandeProduits.isEmpty()) {
            throw new IllegalArgumentException("La liste des commandes produits ne peut pas être vide ou null.");
        }

        // Étape 1 : Regrouper par (idCommande, idProduit) et additionner les quantités
        Map<CommandeProduitId, CommandeProduit> grouped = new HashMap<>();

        for (CommandeProduit cp : commandeProduits) {
            if (cp.getIdCommande() == null || cp.getIdProduit() == null || cp.getQuantite() <= 0) {
                throw new IllegalArgumentException("ID ou quantité invalide : " + cp);
            }

            CommandeProduitId key = new CommandeProduitId(cp.getIdCommande(), cp.getIdProduit());

            grouped.merge(key, cp, (existing, incoming) -> {
                existing.setQuantite(existing.getQuantite() + incoming.getQuantite());
                return existing;
            });
        }

        // Étape 2 : Traiter chaque groupe (un produit unique dans une commande)
        List<CommandeProduit> savedProduits = new ArrayList<>();

        for (Map.Entry<CommandeProduitId, CommandeProduit> entry : grouped.entrySet()) {
            CommandeProduitId id = entry.getKey();
            CommandeProduit cp = entry.getValue();

            try {
                // Récupérer commande et produit
                Commande commande = commandeRepository.findById(id.getIdCommande())
                        .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + id.getIdCommande()));

                Produit produit = produitRepository.findById(id.getIdProduit())
                        .orElseThrow(() -> new IllegalArgumentException("Produit introuvable : " + id.getIdProduit()));

                // Vérifier si déjà en base
                Optional<CommandeProduit> existingOpt = commandeProduitRepository.findById(id);
                int quantiteTotale = cp.getQuantite();

                if (existingOpt.isPresent()) {
                    // Produit déjà dans la commande → on additionne
                    CommandeProduit existing = existingOpt.get();
                    quantiteTotale = existing.getQuantite() + cp.getQuantite();

                    // Vérifier le stock total
                    if (produit.getStock() < cp.getQuantite()) {
                        throw new IllegalArgumentException(
                                "Stock insuffisant pour ajouter " + cp.getQuantite() +
                                        " unités de " + produit.getNom() + ". Stock actuel : " + produit.getStock()
                        );
                    }

                    // Mettre à jour
                    produit.setStock(produit.getStock() - cp.getQuantite());
                    existing.setQuantite(quantiteTotale);
                    existing.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(quantiteTotale)));

                    CommandeProduit saved = commandeProduitRepository.saveAndFlush(existing);
                    savedProduits.add(saved);

                    logger.info("Quantité additionnée : produit ID {} dans commande ID {} → {} unités",
                            produit.getIdProduit(), commande.getIdCommande(), quantiteTotale);
                } else {
                    // Nouveau produit dans la commande
                    if (produit.getStock() < quantiteTotale) {
                        throw new IllegalArgumentException("Stock insuffisant pour " + produit.getNom());
                    }

                    produit.setStock(produit.getStock() - quantiteTotale);
                    produitRepository.save(produit);

                    cp.setCommande(commande);
                    cp.setProduit(produit);
                    cp.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(quantiteTotale)));

                    CommandeProduit saved = commandeProduitRepository.saveAndFlush(cp);
                    savedProduits.add(saved);

                    logger.info("Nouveau produit ajouté : ID {} dans commande ID {}", produit.getIdProduit(), commande.getIdCommande());
                }

                // Mise à jour du total de la commande
                commandeRepository.updateTotalById(commande.getIdCommande());

                // Alerte stock bas
                if (produit.getStock() < SEUIL_STOCK_BAS) {
                    logger.warn("Stock bas pour le produit {} : {} unités restantes", produit.getNom(), produit.getStock());
                }

            } catch (Exception e) {
                logger.error("Erreur lors du traitement de l'élément : {}", e.getMessage());
                throw e; // Annule toute la transaction
            }
        }

        return savedProduits;
    }
}