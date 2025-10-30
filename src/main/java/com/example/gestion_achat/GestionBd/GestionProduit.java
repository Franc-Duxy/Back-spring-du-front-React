//1-Class(model) , 2-Repository , 3-GestionBd(service), 4-Controller

package com.example.gestion_achat.GestionBd;

import com.example.gestion_achat.Class.Produit;
import com.example.gestion_achat.Repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class GestionProduit {
    private static final Logger logger = LoggerFactory.getLogger(GestionProduit.class);
    private final ProduitRepository produitRepository;

    public GestionProduit(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public List<Produit> getAllStocks() { // Changé pour retourner List<Produit>
        logger.info("Récupération de tous les stocks depuis la table produit");
        return produitRepository.findAll(); // Utilise findAll() directement
    }

    public List<Produit> getAllProduits() {
        logger.info("Récupération de tous les produits");
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduitById(Integer idProduit) {
        logger.info("Récupération du produit avec ID : {}", idProduit);
        return produitRepository.findById(idProduit);
    }

    public Produit ajouterProduit(Produit produit) {
        if (produit.getPrix().compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Tentative d'ajout d'un produit avec prix négatif : {}", produit.getPrix());
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        if (produit.getStock() < 0) {
            logger.warn("Tentative d'ajout d'un produit avec stock négatif : {}", produit.getStock());
            throw new IllegalArgumentException("Le stock ne peut pas être négatif");
        }
        Optional<Produit> produitExistant = produitRepository.findAll().stream()
                .filter(p -> p.getNom().equalsIgnoreCase(produit.getNom()))
                .findFirst();
        if (produitExistant.isPresent()) {
            logger.warn("Le produit existe déjà : {}", produit.getNom());
            throw new IllegalArgumentException("Un produit avec le nom " + produit.getNom() + " existe déjà.");
        }
        logger.info("Ajout du produit : {}", produit.getNom());
        return produitRepository.save(produit);
    }

    public Optional<Produit> modifierProduit(Integer idProduit, Produit produitModifier) {
        return produitRepository.findById(idProduit).map(produit -> {
            if (produitModifier.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le prix ne peut pas être négatif");
            }
            if (produitModifier.getStock() < 0) {
                throw new IllegalArgumentException("Le stock ne peut pas être négatif");
            }

            // Vérifier si le nom existe déjà pour un autre produit
            Optional<Produit> produitExistant = produitRepository.findByNomIgnoreCase(produitModifier.getNom());
            if (produitExistant.isPresent() && !produitExistant.get().getIdProduit().equals(idProduit)) {
                throw new IllegalArgumentException("Un autre produit avec le même nom existe déjà.");
            }

            produit.setNom(produitModifier.getNom());
            produit.setPrix(produitModifier.getPrix());
            produit.setStock(produitModifier.getStock());
            return produitRepository.save(produit);
        });
    }


    public boolean supprimerProduit(Integer idProduit) {
        if (produitRepository.existsById(idProduit)) {
            produitRepository.deleteById(idProduit);
            logger.info("Produit supprimé avec ID : {}", idProduit);
            return true;
        }
        logger.warn("Tentative de suppression d'un produit inexistant avec ID : {}", idProduit);
        return false;
    }
}