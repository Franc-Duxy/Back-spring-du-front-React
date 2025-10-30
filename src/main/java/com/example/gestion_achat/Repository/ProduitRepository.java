//1-Class(model) , 2-Repository , 3-GestionBd(service), 4-Controller
package com.example.gestion_achat.Repository;

import com.example.gestion_achat.Class.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    Optional<Produit> findByNomIgnoreCase(String nom);
}