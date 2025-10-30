package com.example.gestion_achat.Class;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class CommandeDetailsDTO {
    private Integer idCommande;
    private BigDecimal total;
    private LocalDateTime dateCommande;
    private Commande.Statut statut;
    private Integer idCaissier;
    private String nomCaissier;
    private String emailCaissier;
    private Integer idProduit;
    private String nomProduit;
    private BigDecimal prixUnitaire;
    private Integer quantite;
    private BigDecimal prixTotalProduit;

    // Constructeur avec tous les paramètres dans l'ordre de la vue
    public CommandeDetailsDTO(Integer idCommande, BigDecimal total, Timestamp dateCommande, String statut,
                              Integer idCaissier, String nomCaissier, String emailCaissier, Integer idProduit,
                              String nomProduit, BigDecimal prixUnitaire, Integer quantite, BigDecimal prixTotalProduit) {
        this.idCommande = idCommande;
        this.total = total;
        this.dateCommande = dateCommande != null ? dateCommande.toLocalDateTime() : null;
        this.statut = statut != null ? Commande.Statut.valueOf(statut) : null; // Conversion String -> Commande.Statut
        this.idCaissier = idCaissier;
        this.nomCaissier = nomCaissier;
        this.emailCaissier = emailCaissier;
        this.idProduit = idProduit;
        this.nomProduit = nomProduit;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
        this.prixTotalProduit = prixTotalProduit;
    }
}