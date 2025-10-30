//1-Class(model) , 2-Repository , 3-GestionBd(service), 4-Controller

package com.example.gestion_achat.Controller;

import com.example.gestion_achat.Class.Produit;
import com.example.gestion_achat.GestionBd.GestionProduit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // 5173: port du frontend React
@RequestMapping("/api/produit")
public class ProduitController {

    private final GestionProduit gestionProduit;

    @Autowired
    public ProduitController(GestionProduit gestionProduit) {
        this.gestionProduit = gestionProduit;
    }

    // Récupérer tous les produits
    @GetMapping("/tous")
    public ResponseEntity<List<Produit>> getAllProduits() {
        List<Produit> produits = gestionProduit.getAllProduits();
        return ResponseEntity.ok(produits);
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<Produit>> getAllStocks() { // Changé pour List<Produit>
        List<Produit> stocks = gestionProduit.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    // Récupérer un produit par son ID
    @GetMapping("/{idProduit}")
    public ResponseEntity<Produit> getProduitById(@PathVariable("idProduit") Integer idProduit) {
        Optional<Produit> produit = gestionProduit.getProduitById(idProduit);
        return produit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter un nouveau produit
    @PostMapping("/ajouter")
    public ResponseEntity<Produit> ajouterProduit(@RequestBody Produit produit) {
        try {
            Produit nouveauProduit = gestionProduit.ajouterProduit(produit);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauProduit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Ou un message d'erreur
        }
    }

    // Modifier un produit existant
    @PutMapping("/modifier/{idProduit}")
    public ResponseEntity<Produit> modifierProduit(@PathVariable("idProduit") Integer idProduit, @RequestBody Produit produit) {
        Optional<Produit> produitModifie = gestionProduit.modifierProduit(idProduit, produit);
        return produitModifie.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer un produit
    @DeleteMapping("/supprimer/{idProduit}")
    public ResponseEntity<String> supprimerProduit(@PathVariable("idProduit") Integer idProduit) {
        if (gestionProduit.supprimerProduit(idProduit)) {
            return ResponseEntity.ok("Produit supprimé avec succès !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit non trouvé !");
    }
}
