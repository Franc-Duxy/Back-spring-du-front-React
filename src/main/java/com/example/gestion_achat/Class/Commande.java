package com.example.gestion_achat.Class;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "commande")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande")
    private Long idCommande;

    @ManyToOne
    @JoinColumn(name = "id_acheteur", referencedColumnName = "id_utilisateur")
    private Utilisateur acheteur;

    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "date_commande", nullable = false, updatable = false)
    private LocalDateTime dateCommande = LocalDateTime.now();

    @Column(name = "statut", nullable = false)
    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.EN_COURS;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CommandeProduit> commandeProduits = new ArrayList<>();

    @OneToOne(mappedBy = "commande", fetch = FetchType.LAZY)
    @JsonBackReference // Évite la sérialisation infinie
    private Paiement paiement;

    public enum Statut {
        EN_COURS,
        VALIDEE,
        ANNULEE
    }
}