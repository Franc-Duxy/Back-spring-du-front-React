package com.example.gestion_achat.Controller;

import com.example.gestion_achat.Class.Utilisateur;
import com.example.gestion_achat.GestionBd.GestionUtilisateur;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/utilisateur")
public class UtilisateurController {

    private final GestionUtilisateur gestionUtilisateur;

    @Autowired
    public UtilisateurController(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
    }

    @GetMapping("/tous")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = gestionUtilisateur.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/{idUtilisateur}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable("idUtilisateur") Integer idUtilisateur) {
        Optional<Utilisateur> utilisateur = gestionUtilisateur.getUtilisateurById(idUtilisateur);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Utilisateur> getUtilisateurByEmail(@PathVariable("email") String email) {
        Optional<Utilisateur> utilisateur = gestionUtilisateur.getUtilisateurByEmail(email);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/ajouter")
    public ResponseEntity<Object> ajouterUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        try {
            Utilisateur nouvelUtilisateur = gestionUtilisateur.ajouterUtilisateur(utilisateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/modifier/{idUtilisateur}")
    public ResponseEntity<Utilisateur> modifierUtilisateur(
            @PathVariable("idUtilisateur") Integer idUtilisateur,
            @Valid @RequestBody Utilisateur utilisateur) {
        Optional<Utilisateur> utilisateurModifie = gestionUtilisateur.modifierUtilisateur(idUtilisateur, utilisateur);
        return utilisateurModifie.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/supprimer/{idUtilisateur}")
    public ResponseEntity<Map<String, String>> supprimerUtilisateur(@PathVariable("idUtilisateur") Integer idUtilisateur) {
        if (gestionUtilisateur.supprimerUtilisateur(idUtilisateur)) {
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès !"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Utilisateur non trouvé !"));
    }

    // Nouvel endpoint pour vérifier les identifiants admin
    @PostMapping("/verifier-admin")
    public ResponseEntity<Map<String, Boolean>> verifierAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String mdp = credentials.get("mdp");
        boolean isValid = gestionUtilisateur.verifierAdmin(email, mdp);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    // Nouvel endpoint pour le login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String mdp = credentials.get("mdp");
        Optional<Utilisateur> utilisateur = gestionUtilisateur.verifierUtilisateur(email, mdp);

        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Connexion réussie",
                    "utilisateur", Map.of(
                            "id", utilisateur.get().getIdUtilisateur(),
                            "nom", utilisateur.get().getNom(),
                            "email", utilisateur.get().getEmail(),
                            "role", utilisateur.get().getRole().toString()
                    )
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Email ou mot de passe incorrect"));
        }
    }
}