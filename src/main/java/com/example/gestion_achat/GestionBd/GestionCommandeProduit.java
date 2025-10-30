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

        Commande commande = commandeRepository.findById(commandeProduit.getIdCommande())
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + commandeProduit.getIdCommande()));
        Produit produit = produitRepository.findById(commandeProduit.getIdProduit())
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable : " + commandeProduit.getIdProduit()));

        if (commandeProduit.getQuantite() < 0) {
            logger.warn("Tentative d'ajout avec quantité négative : {}", commandeProduit.getQuantite());
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        if (produit.getStock() < commandeProduit.getQuantite()) {
            logger.warn("Stock insuffisant pour le produit ID {} : actuel {}, demandé {}",
                    produit.getIdProduit(), produit.getStock(), commandeProduit.getQuantite());
            throw new IllegalArgumentException("Stock insuffisant pour " + produit.getNom());
        }

        produit.setStock(produit.getStock() - commandeProduit.getQuantite());
        produitRepository.save(produit);

        commandeProduit.setCommande(commande);
        commandeProduit.setProduit(produit);
        commandeProduit.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(commandeProduit.getQuantite())));

        CommandeProduit saved = commandeProduitRepository.saveAndFlush(commandeProduit);

        // Mise à jour du total avec la requête JPQL
        commandeRepository.updateTotalById(commande.getIdCommande());

        int nouveauStock = produit.getStock();
        if (nouveauStock < SEUIL_STOCK_BAS) {
            logger.warn("Stock bas pour le produit {} : {} unités restantes", produit.getNom(), nouveauStock);
        }
        logger.info("Ajout d'un élément de commande pour commande ID : {} et produit ID : {}",
                saved.getIdCommande(), saved.getIdProduit());
        return saved;
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
            if (commandeProduitModifie.getQuantite() < 0) {
                logger.warn("Tentative de modification avec quantité négative : {}", commandeProduitModifie.getQuantite());
                throw new IllegalArgumentException("La quantité ne peut pas être négative");
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

        List<CommandeProduit> savedProduits = commandeProduits.stream().map(commandeProduit -> {
            try {
                // Vérification des IDs
                if (commandeProduit.getIdCommande() == null || commandeProduit.getIdProduit() == null) {
                    throw new IllegalArgumentException("Les IDs de commande et de produit doivent être spécifiés pour l'élément : " + commandeProduit);
                }

                // Vérification de l'existence de la commande et du produit
                Commande commande = commandeRepository.findById(commandeProduit.getIdCommande())
                        .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + commandeProduit.getIdCommande()));
                Produit produit = produitRepository.findById(commandeProduit.getIdProduit())
                        .orElseThrow(() -> new IllegalArgumentException("Produit introuvable : " + commandeProduit.getIdProduit()));

                // Validation de la quantité
                if (commandeProduit.getQuantite() < 0) {
                    logger.warn("Tentative d'ajout avec quantité négative : {}", commandeProduit.getQuantite());
                    throw new IllegalArgumentException("La quantité ne peut pas être négative pour l'élément : " + commandeProduit);
                }

                // Vérification du stock
                if (produit.getStock() < commandeProduit.getQuantite()) {
                    logger.warn("Stock insuffisant pour le produit ID {} : actuel {}, demandé {}",
                            produit.getIdProduit(), produit.getStock(), commandeProduit.getQuantite());
                    throw new IllegalArgumentException("Stock insuffisant pour " + produit.getNom() + " dans l'élément : " + commandeProduit);
                }

                // Mise à jour du stock
                produit.setStock(produit.getStock() - commandeProduit.getQuantite());
                produitRepository.save(produit);

                // Configuration des relations et calcul du prix total
                commandeProduit.setCommande(commande);
                commandeProduit.setProduit(produit);
                commandeProduit.setPrixTotal(produit.getPrix().multiply(BigDecimal.valueOf(commandeProduit.getQuantite())));

                // Enregistrement de l'élément
                CommandeProduit saved = commandeProduitRepository.save(commandeProduit);

                // Mise à jour du total de la commande
                commandeRepository.updateTotalById(commande.getIdCommande());

                int nouveauStock = produit.getStock();
                if (nouveauStock < SEUIL_STOCK_BAS) {
                    logger.warn("Stock bas pour le produit {} : {} unités restantes", produit.getNom(), nouveauStock);
                }
                logger.info("Ajout d'un élément de commande pour commande ID : {} et produit ID : {}",
                        saved.getIdCommande(), saved.getIdProduit());
                return saved;
            } catch (Exception e) {
                logger.error("Erreur lors de l'ajout d'un élément de commande : {}", e.getMessage());
                throw e; // Rejette la transaction si une erreur survient
            }
        }).collect(Collectors.toList());

        return savedProduits;
    }
}