package com.example.gestion_achat.Controller;

import com.example.gestion_achat.Class.CommandeDetailsDTO;
import com.example.gestion_achat.Class.CommandeEnCoursDTO;
import com.example.gestion_achat.Class.CommandeValideePaiementDTO;
import com.example.gestion_achat.Class.Commande;
import com.example.gestion_achat.GestionBd.GestionCommande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // 5173: port du frontend React
@RequestMapping("/api/commande")
public class CommandeController {

    private final GestionCommande gestionCommande;

    @Autowired
    public CommandeController(GestionCommande gestionCommande) {
        this.gestionCommande = gestionCommande;
    }

    @GetMapping("/toutes")
    public ResponseEntity<List<Commande>> getAllCommandes() {
        return ResponseEntity.ok(gestionCommande.getAllCommandes());
    }

    @GetMapping("/en-cours")
    public ResponseEntity<List<CommandeEnCoursDTO>> getAllCommandesEnCours() {
        return ResponseEntity.ok(gestionCommande.getAllCommandesEnCours());
    }

    @GetMapping("/validees-paiements")
    public ResponseEntity<List<CommandeValideePaiementDTO>> getAllCommandesValideesPaiements() {
        List<CommandeValideePaiementDTO> commandes = gestionCommande.getAllCommandesValideesPaiements();
        return ResponseEntity.ok(commandes);
    }

    // Récupérer une commande par ID
    @GetMapping("/{idCommande}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable("idCommande") Long idCommande) {
        Optional<Commande> commande = gestionCommande.getCommandeById(idCommande);
        return commande.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter une nouvelle commande
    @PostMapping("/ajouter")
    public ResponseEntity<Commande> ajouterCommande(@RequestBody Commande commande) {
        Commande nouvelleCommande = gestionCommande.ajouterCommande(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleCommande);
    }

    // Modifier une commande existante
    @PutMapping("/modifier/{idCommande}")
    public ResponseEntity<Commande> modifierCommande(
            @PathVariable("idCommande") Long idCommande,
            @RequestBody Commande commande) {
        Optional<Commande> commandeModifiee = gestionCommande.modifierCommande(idCommande, commande);
        return commandeModifiee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer une commande
    @DeleteMapping("/supprimer/{idCommande}")
    public ResponseEntity<String> supprimerCommande(@PathVariable("idCommande") Long idCommande) {
        if (gestionCommande.supprimerCommande(idCommande)) {
            return ResponseEntity.ok("Commande supprimée avec succès !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Commande non trouvée !");
    }
}