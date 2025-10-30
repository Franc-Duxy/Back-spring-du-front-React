package com.example.gestion_achat.Repository;

import com.example.gestion_achat.Class.Paiement;
import com.example.gestion_achat.Class.PaiementDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Integer> {

    @Query(value = "SELECT * FROM vue_paiements_details", nativeQuery = true)
    List<PaiementDetailsDTO> findAllPaiementDetails();

    boolean existsByCommandeIdCommande(Long idCommande);

    Optional<Paiement> findByCommandeIdCommande(Long idCommande);
}