-- V2__create_tables.sql

-- Table utilisateur
CREATE SEQUENCE public.utilisateur_id_utilisateur_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE public.utilisateur (
                                    id_utilisateur INTEGER PRIMARY KEY DEFAULT nextval('public.utilisateur_id_utilisateur_seq'),
                                    nom VARCHAR(255),
                                    email VARCHAR(255) UNIQUE,
                                    mdp VARCHAR(255),
                                    role VARCHAR(255) DEFAULT 'CAISSIER'::public.role_enum
);

-- Table produit
CREATE SEQUENCE public.produit_id_produit_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE public.produit (
                                id_produit INTEGER PRIMARY KEY DEFAULT nextval('public.produit_id_produit_seq'),
                                nom VARCHAR(100),
                                prix NUMERIC(15,2),
                                stock INTEGER DEFAULT 0
);

-- Table commande
CREATE SEQUENCE public.commande_id_commande_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE public.commande (
                                 id_commande INTEGER PRIMARY KEY DEFAULT nextval('public.commande_id_commande_seq'),
                                 id_acheteur INTEGER,
                                 total NUMERIC(15,2) DEFAULT 0,
                                 date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 statut VARCHAR(255) DEFAULT 'EN_COURS'::public.commande_statut,
                                 FOREIGN KEY (id_acheteur) REFERENCES public.utilisateur(id_utilisateur) ON DELETE SET NULL
);

-- Table commande_produit
CREATE TABLE public.commande_produit (
                                         id_commande INTEGER NOT NULL,
                                         id_produit INTEGER NOT NULL,
                                         quantite INTEGER,
                                         prix_total NUMERIC(15,2) NOT NULL,
                                         PRIMARY KEY (id_commande, id_produit),
                                         FOREIGN KEY (id_commande) REFERENCES public.commande(id_commande) ON DELETE CASCADE,
                                         FOREIGN KEY (id_produit) REFERENCES public.produit(id_produit) ON DELETE CASCADE
);

-- Table paiement
CREATE SEQUENCE public.paiement_id_paiement_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE public.paiement (
                                 id_paiement INTEGER PRIMARY KEY DEFAULT nextval('public.paiement_id_paiement_seq'),
                                 id_commande INTEGER UNIQUE,
                                 montant NUMERIC(15,2),
                                 date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 methode_paiement VARCHAR(50),
                                 FOREIGN KEY (id_commande) REFERENCES public.commande(id_commande) ON DELETE CASCADE
);