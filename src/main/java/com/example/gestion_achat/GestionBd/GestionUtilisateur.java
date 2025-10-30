package com.example.gestion_achat.GestionBd;

import com.example.gestion_achat.Class.Utilisateur;
import com.example.gestion_achat.Repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestionUtilisateur {

    private static final Logger logger = LoggerFactory.getLogger(GestionUtilisateur.class);
    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public GestionUtilisateur(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Integer idUtilisateur) {
        return utilisateurRepository.findById(idUtilisateur);
    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            logger.warn("Un utilisateur avec l'email {} existe déjà", utilisateur.getEmail());
            throw new IllegalArgumentException("L'email " + utilisateur.getEmail() + " est déjà utilisé.");
        }
        utilisateur.setMdp(passwordEncoder.encode(utilisateur.getMdp()));
        logger.info("Ajout de l'utilisateur : {}", utilisateur.getEmail());
        return utilisateurRepository.save(utilisateur);
    }

    public Optional<Utilisateur> modifierUtilisateur(Integer idUtilisateur, Utilisateur utilisateurModifie) {
        return utilisateurRepository.findById(idUtilisateur).map(utilisateur -> {
            utilisateur.setNom(utilisateurModifie.getNom());
            utilisateur.setEmail(utilisateurModifie.getEmail());
            if (utilisateurModifie.getMdp() != null && !utilisateurModifie.getMdp().isEmpty()) {
                utilisateur.setMdp(passwordEncoder.encode(utilisateurModifie.getMdp()));
            }
            utilisateur.setRole(utilisateurModifie.getRole());
            logger.info("Utilisateur modifié : {}", utilisateur.getEmail());
            return utilisateurRepository.save(utilisateur);
        });
    }

    public boolean supprimerUtilisateur(Integer idUtilisateur) {
        if (utilisateurRepository.existsById(idUtilisateur)) {
            utilisateurRepository.deleteById(idUtilisateur);
            logger.info("Utilisateur supprimé : {}", idUtilisateur);
            return true;
        }
        logger.warn("Utilisateur introuvable : {}", idUtilisateur);
        return false;
    }

    // Méthode pour vérifier les identifiants admin
    public boolean verifierAdmin(String email, String mdp) {
        Optional<Utilisateur> admin = utilisateurRepository.findByEmail(email);
        return admin.isPresent() && admin.get().getRole() == Utilisateur.Role.ADMIN
                && passwordEncoder.matches(mdp, admin.get().getMdp());
    }

    // Nouvelle méthode pour vérifier n'importe quel utilisateur pour mon login
    public Optional<Utilisateur> verifierUtilisateur(String email, String mdp) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent() && passwordEncoder.matches(mdp, utilisateur.get().getMdp())) {
            return utilisateur; // Retourne l'utilisateur si les identifiants sont corrects
        }
        return Optional.empty(); // Retourne vide si incorrect
    }
}