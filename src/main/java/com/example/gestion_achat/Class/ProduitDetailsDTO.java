package com.example.gestion_achat.Class;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitDetailsDTO {
    private Integer idProduit;
    private String nomProduit;
    private BigDecimal prix;
    private Integer stock;

    public ProduitDetailsDTO(Integer idProduit, String nomProduit, BigDecimal prix, Integer stock) {
        this.idProduit = idProduit;
        this.nomProduit = nomProduit;
        this.prix = prix;
        this.stock = stock;
    }
}