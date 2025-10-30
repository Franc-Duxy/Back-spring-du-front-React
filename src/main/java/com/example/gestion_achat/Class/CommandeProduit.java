package com.example.gestion_achat.Class;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "commande_produit")
@IdClass(CommandeProduitId.class)
public class CommandeProduit {

    @Id
    @Column(name = "id_commande")
    private Long idCommande;

    @Id
    @Column(name = "id_produit")
    private Integer idProduit;

    @ManyToOne
    @MapsId("idCommande")
    @JoinColumn(name = "id_commande", insertable = false, updatable = false)
    @JsonBackReference
    private Commande commande;

    @ManyToOne
    @MapsId("idProduit")
    @JoinColumn(name = "id_produit", insertable = false, updatable = false)
    private Produit produit;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix_total", nullable = false)
    private BigDecimal prixTotal;
}