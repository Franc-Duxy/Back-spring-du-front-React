-- V1__create_enums.sql
CREATE TYPE public.role_enum AS ENUM ('ADMIN', 'CAISSIER');
CREATE TYPE public.commande_statut AS ENUM ('EN_COURS', 'VALIDEE', 'ANNULEE');