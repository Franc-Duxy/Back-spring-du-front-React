package com.example.gestion_achat.Class;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class CommandeValideePaiementDTO {
    private Integer idCommande;
    private BigDecimal total;
    private LocalDateTime dateCommande;
    private Commande.Statut statut;
    private String nomCaissier;
    private Integer idPaiement;
    private BigDecimal montantPaiement;
    private LocalDateTime datePaiement;
    private String methodePaiement;

    // Constructeur avec tous les paramètres dans l'ordre de la vue
    public CommandeValideePaiementDTO(Integer idCommande, BigDecimal total, Timestamp dateCommande, String statut,
                                      String nomCaissier, Integer idPaiement, BigDecimal montantPaiement,
                                      Timestamp datePaiement, String methodePaiement) {
        this.idCommande = idCommande;
        this.total = total;
        this.dateCommande = dateCommande != null ? dateCommande.toLocalDateTime() : null;
        this.statut = statut != null ? Commande.Statut.valueOf(statut) : null;
        this.nomCaissier = nomCaissier;
        this.idPaiement = idPaiement;
        this.montantPaiement = montantPaiement;
        this.datePaiement = datePaiement != null ? datePaiement.toLocalDateTime() : null;
        this.methodePaiement = methodePaiement;
    }
}