package com.example.gestion_achat.Class;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class PaiementDetailsDTO {
    private Integer idPaiement;
    private Integer idCommande;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private String methodePaiement;
    private BigDecimal totalCommande;
    private Commande.Statut statutCommande;
    private String nomCaissier;
    private String emailCaissier;

    // Constructeur avec tous les paramètres dans l'ordre de la vue
    public PaiementDetailsDTO(Integer idPaiement, Integer idCommande, BigDecimal montant, Timestamp datePaiement,
                              String methodePaiement, BigDecimal totalCommande, String statutCommande,
                              String nomCaissier, String emailCaissier) {
        this.idPaiement = idPaiement;
        this.idCommande = idCommande;
        this.montant = montant;
        this.datePaiement = datePaiement != null ? datePaiement.toLocalDateTime() : null;
        this.methodePaiement = methodePaiement;
        this.totalCommande = totalCommande;
        this.statutCommande = statutCommande != null ? Commande.Statut.valueOf(statutCommande) : null;
        this.nomCaissier = nomCaissier;
        this.emailCaissier = emailCaissier;
    }
}