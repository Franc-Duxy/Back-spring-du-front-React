package com.example.gestion_achat.Class;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "paiement")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Integer idPaiement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande", unique = true)
    @JsonManagedReference // Gère la sérialisation de cette relation
    private Commande commande;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime datePaiement;

    @Column(name = "methode_paiement", length = 50, nullable = false)
    private String methodePaiement;

    public Paiement() {}
    public Paiement(Commande commande, BigDecimal montant, String methodePaiement) {
        this.commande = commande;
        this.montant = montant;
        this.methodePaiement = methodePaiement;
    }
}