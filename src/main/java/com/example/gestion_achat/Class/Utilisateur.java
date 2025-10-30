package com.example.gestion_achat.Class;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "utilisateur", indexes = @Index(name = "idx_email", columnList = "email"))
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Column(name = "nom", length = 255)
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email ne peut pas être vide")
    private String email;

    @Column(name = "mdp", length = 255, nullable = false)
    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String mdp;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.CAISSIER;

    public enum Role {
        ADMIN,
        CAISSIER
    }
}