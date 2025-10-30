package com.example.gestion_achat.Class;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CommandeEnCoursDTO {
    private Integer idCommande;
    private Integer idUtilisateur;
    private BigDecimal total;
    private java.sql.Timestamp dateCommande;  // Timestamp pour requête native
    private String statut;                    // String pour statut

    public CommandeEnCoursDTO(Integer idCommande, Integer idUtilisateur, BigDecimal total, java.sql.Timestamp dateCommande, String statut) {
        this.idCommande = idCommande;
        this.idUtilisateur = idUtilisateur;
        this.total = total;
        this.dateCommande = dateCommande;
        this.statut = statut;
    }

    // Getters et setters ajustés
    public Integer getIdCommande() { return idCommande; }
    public void setIdCommande(Integer idCommande) { this.idCommande = idCommande; }
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public java.sql.Timestamp getDateCommande() { return dateCommande; }           // Aligné avec Timestamp
    public void setDateCommande(java.sql.Timestamp dateCommande) { this.dateCommande = dateCommande; }  // Aligné avec Timestamp
    public String getStatut() { return statut; }                                   // Aligné avec String
    public void setStatut(String statut) { this.statut = statut; }                 // Aligné avec String
}