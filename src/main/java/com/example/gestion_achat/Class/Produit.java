package com.example.gestion_achat.Class;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Integer idProduit; // Aligné avec SERIAL en PostgreSQL

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prix", precision = 15, scale = 2, nullable = false)
    private BigDecimal prix;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0; // Valeur par défaut alignée avec SQL
}