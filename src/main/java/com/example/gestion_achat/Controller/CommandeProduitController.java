package com.example.gestion_achat.Controller;

import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.Class.CommandeProduit;
import com.example.gestion_achat.Class.CommandeProduitId;
import com.example.gestion_achat.Class.Produit;
import com.example.gestion_achat.GestionBd.GestionCommandeProduit;
import com.example.gestion_achat.Repository.CommandeRepository; // Ajoute cette importation
import com.example.gestion_achat.Repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/commande-produit")
public class CommandeProduitController {

    private final GestionCommandeProduit gestionCommandeProduit;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    @Autowired
    public CommandeProduitController(GestionCommandeProduit gestionCommandeProduit,
                                     ProduitRepository produitRepository,
                                     CommandeRepository commandeRepository) {
        this.gestionCommandeProduit = gestionCommandeProduit;
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
    }

    // Récupérer tous les éléments de commande
    @GetMapping("/tous")
    public ResponseEntity<List<CommandeProduit>> getAllCommandeProduits() {
        List<CommandeProduit> commandeProduits = gestionCommandeProduit.getAllCommandeProduits();
        return ResponseEntity.ok(commandeProduits);
    }

    // Récupérer un élément de commande par ID composé (idCommande et idProduit)
    @GetMapping("/{idCommande}/{idProduit}")
    public ResponseEntity<CommandeProduit> getCommandeProduitById(
            @PathVariable("idCommande") Long idCommande,
            @PathVariable("idProduit") Integer idProduit) {
        CommandeProduitId id = new CommandeProduitId(idCommande, idProduit);
        Optional<CommandeProduit> commandeProduit = gestionCommandeProduit.getCommandeProduitById(id);
        return commandeProduit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter un nouvel élément de commande
    @PostMapping("/ajouter")
    public ResponseEntity<CommandeProduit> ajouterCommandeProduit(@RequestBody CommandeProduit commandeProduit) {
        CommandeProduit nouveauCommandeProduit = gestionCommandeProduit.ajouterCommandeProduit(commandeProduit);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauCommandeProduit);
    }

    // Ajouter plusieurs éléments de commande
    @PostMapping("/ajouter/multiple")
    public ResponseEntity<List<CommandeProduit>> ajouterMultipleCommandeProduits(@RequestBody List<CommandeProduit> commandeProduits) {
        List<CommandeProduit> nouveauxProduits = gestionCommandeProduit.ajouterMultipleCommandeProduits(commandeProduits);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauxProduits);
    }

    // Modifier un élément de commande existant
    @PutMapping("/modifier/{idCommande}/{oldIdProduit}")
    public ResponseEntity<CommandeProduit> modifierCommandeProduit(
            @PathVariable("idCommande") Long idCommande,
            @PathVariable("oldIdProduit") Integer oldIdProduit,
            @RequestBody CommandeProduit updatedCommandeProduit) {
        CommandeProduitId oldId = new CommandeProduitId(idCommande, oldIdProduit);
        Optional<CommandeProduit> existing = gestionCommandeProduit.getCommandeProduitById(oldId);
        if (existing.isPresent()) {
            CommandeProduit commandeProduit = existing.get();
            if (updatedCommandeProduit.getIdProduit() != null && !updatedCommandeProduit.getIdProduit().equals(oldIdProduit)) {
                // Supprimer l'ancien enregistrement
                gestionCommandeProduit.supprimerCommandeProduit(oldId);
                // Créer un nouvel enregistrement avec le nouvel idProduit
                CommandeProduit newCommandeProduit = new CommandeProduit();
                newCommandeProduit.setIdCommande(idCommande);
                newCommandeProduit.setIdProduit(updatedCommandeProduit.getIdProduit());
                newCommandeProduit.setQuantite(updatedCommandeProduit.getQuantite());
                // Charger le nouveau produit
                Produit newProduit = produitRepository.findById(updatedCommandeProduit.getIdProduit())
                        .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + updatedCommandeProduit.getIdProduit()));
                newCommandeProduit.setProduit(newProduit);
                // Charger la commande associée
                Commande commande = commandeRepository.findById(idCommande)
                        .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + idCommande));
                newCommandeProduit.setCommande(commande);
                newCommandeProduit.setPrixTotal(BigDecimal.valueOf(newCommandeProduit.getQuantite()).multiply(newProduit.getPrix()));
                return ResponseEntity.ok(gestionCommandeProduit.save(newCommandeProduit));
            }
            // Mettre à jour la quantité si idProduit reste inchangé
            if (updatedCommandeProduit.getQuantite() != null) {
                commandeProduit.setQuantite(updatedCommandeProduit.getQuantite());
            }
            // Recalculer prixTotal
            if (commandeProduit.getQuantite() != null && commandeProduit.getProduit() != null) {
                BigDecimal quantite = BigDecimal.valueOf(commandeProduit.getQuantite());
                BigDecimal prix = commandeProduit.getProduit().getPrix();
                commandeProduit.setPrixTotal(quantite.multiply(prix));
            }
            System.out.println("Modification reçue pour Commande ID: " + idCommande + " Ancien Produit ID: " + oldIdProduit + " Nouveau Produit ID: " + updatedCommandeProduit.getIdProduit());
            return ResponseEntity.ok(gestionCommandeProduit.save(commandeProduit));
        }
        return ResponseEntity.notFound().build();
    }

    // Supprimer un élément de commande
    @DeleteMapping("/supprimer/{idCommande}/{idProduit}")
    public ResponseEntity<String> supprimerCommandeProduit(
            @PathVariable("idCommande") Long idCommande,
            @PathVariable("idProduit") Integer idProduit) {
        CommandeProduitId id = new CommandeProduitId(idCommande, idProduit);
        if (gestionCommandeProduit.supprimerCommandeProduit(id)) {
            return ResponseEntity.ok("Élément de commande supprimé avec succès !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Élément de commande non trouvé !");
    }
}