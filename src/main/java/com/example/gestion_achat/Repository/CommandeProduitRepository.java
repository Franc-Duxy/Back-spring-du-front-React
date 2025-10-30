package com.example.gestion_achat.Repository;

import com.example.gestion_achat.Class.CommandeProduit;
import com.example.gestion_achat.Class.CommandeProduitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Ajout de cet import

@Repository
public interface CommandeProduitRepository extends JpaRepository<CommandeProduit, CommandeProduitId> {
    // Méthode pour trouver tous les CommandeProduit par idCommande
    List<CommandeProduit> findByCommandeIdCommande(Long idCommande);
}
