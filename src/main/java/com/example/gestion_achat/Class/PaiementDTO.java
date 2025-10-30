package com.example.gestion_achat.Class;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaiementDTO {
    private Integer idPaiement;
    private Long idCommande;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private String methodePaiement;

    // Constructeur par défaut requis pour Jackson
    public PaiementDTO() {
    }

    // Constructeur pour convertir un Paiement en DTO
    public PaiementDTO(Paiement paiement) {
        this.idPaiement = paiement.getIdPaiement();
        this.idCommande = paiement.getCommande() != null ? paiement.getCommande().getIdCommande() : null;
        this.montant = paiement.getMontant();
        this.datePaiement = paiement.getDatePaiement();
        this.methodePaiement = paiement.getMethodePaiement();
    }

    // Getters et setters
    public Integer getIdPaiement() { return idPaiement; }
    public void setIdPaiement(Integer idPaiement) { this.idPaiement = idPaiement; }
    public Long getIdCommande() { return idCommande; }
    public void setIdCommande(Long idCommande) { this.idCommande = idCommande; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
    public String getMethodePaiement() { return methodePaiement; }
    public void setMethodePaiement(String methodePaiement) { this.methodePaiement = methodePaiement; }
}