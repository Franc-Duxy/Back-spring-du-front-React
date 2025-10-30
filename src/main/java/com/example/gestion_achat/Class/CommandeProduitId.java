package com.example.gestion_achat.Class;

import java.io.Serializable;

public class CommandeProduitId implements Serializable {

    private Long idCommande;    // Reste Long, car Commande.idCommande est Long
    private Integer idProduit;  // Changé en Integer pour correspondre à Produit.idProduit

    // Constructeur par défaut requis par JPA
    public CommandeProduitId() {
    }

    public CommandeProduitId(Long idCommande, Integer idProduit) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
    }

    public Long getIdCommande() { return idCommande; }
    public void setIdCommande(Long idCommande) { this.idCommande = idCommande; }
    public Integer getIdProduit() { return idProduit; }  // Integer au lieu de Long
    public void setIdProduit(Integer idProduit) { this.idProduit = idProduit; }  // Integer au lieu de Long

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandeProduitId that = (CommandeProduitId) o;
        return idCommande.equals(that.idCommande) && idProduit.equals(that.idProduit);
    }

    @Override
    public int hashCode() {
        return 31 * idCommande.hashCode() + idProduit.hashCode();
    }
}