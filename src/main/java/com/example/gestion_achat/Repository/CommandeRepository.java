package com.example.gestion_achat.Repository;

import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.Class.CommandeDetailsDTO;
import com.example.gestion_achat.Class.CommandeEnCoursDTO;
import com.example.gestion_achat.Class.CommandeValideePaiementDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Commande c SET c.total = (SELECT COALESCE(SUM(cp.prixTotal), 0) FROM CommandeProduit cp WHERE cp.commande.idCommande = c.idCommande) WHERE c.idCommande = :idCommande")
    void updateTotalById(Long idCommande);

    @Query(value = "SELECT * FROM vue_commandes_details", nativeQuery = true)
    List<CommandeDetailsDTO> findAllCommandeDetails();

    @Query(value = "SELECT id_commande AS idCommande, id_acheteur AS idUtilisateur, total, date_commande AS dateCommande, statut " +
            "FROM commande WHERE statut = 'EN_COURS'", nativeQuery = true)
    List<CommandeEnCoursDTO> findAllCommandesEnCours();

    @Query(value = "SELECT * FROM vue_commandes_validees_paiements", nativeQuery = true)
    List<CommandeValideePaiementDTO> findAllCommandesValideesPaiements();
}