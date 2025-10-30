-- V3__create_views.sql

CREATE VIEW public.vue_commande_detaillee AS
SELECT
    c.id_commande,
    c.id_acheteur,
    p.nom AS produit,
    cp.quantite,
    p.prix,
    (cp.quantite::NUMERIC * p.prix) AS total_produit,
    c.total
FROM commande c
         JOIN commande_produit cp ON c.id_commande = cp.id_commande
         JOIN produit p ON cp.id_produit = p.id_produit;

CREATE VIEW public.vue_commandes_details AS
SELECT
    c.id_commande, c.total, c.date_commande, c.statut,
    u.id_utilisateur AS id_caissier, u.nom AS nom_caissier, u.email AS email_caissier,
    cp.id_produit, p.nom AS nom_produit, p.prix AS prix_unitaire,
    cp.quantite, cp.prix_total AS prix_total_produit
FROM commande c
         JOIN utilisateur u ON c.id_acheteur = u.id_utilisateur
         LEFT JOIN commande_produit cp ON c.id_commande = cp.id_commande
         LEFT JOIN produit p ON cp.id_produit = p.id_produit;

CREATE VIEW public.vue_commandes_en_cours AS
SELECT id_commande, id_acheteur, total, date_commande, statut
FROM commande c
WHERE statut::text = 'EN_COURS'::text;

CREATE VIEW public.vue_commandes_validees_paiements AS
SELECT
    c.id_commande, c.total, c.date_commande, c.statut,
    u.nom AS nom_caissier,
    p.id_paiement, p.montant AS montant_paiement, p.date_paiement, p.methode_paiement
FROM commande c
         JOIN utilisateur u ON c.id_acheteur = u.id_utilisateur
         JOIN paiement p ON c.id_commande = p.id_commande
WHERE c.statut::text = 'VALIDEE'::text;

CREATE VIEW public.vue_paiements_details AS
SELECT
    p.id_paiement, p.id_commande, p.montant, p.date_paiement, p.methode_paiement,
    c.total AS total_commande, c.statut AS statut_commande,
    u.nom AS nom_caissier, u.email AS email_caissier
FROM paiement p
         JOIN commande c ON p.id_commande = c.id_commande
         JOIN utilisateur u ON c.id_acheteur = u.id_utilisateur;

CREATE VIEW public.vue_stocks AS
SELECT id_produit, nom AS nom_produit, prix, stock
FROM produit;