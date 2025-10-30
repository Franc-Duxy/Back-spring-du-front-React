-- V4__create_functions.sql

CREATE OR REPLACE FUNCTION public.creer_nouvelle_commande(_id_acheteur INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
_id_commande INT;
BEGIN
INSERT INTO commande (id_acheteur, total, date_commande)
VALUES (_id_acheteur, 0, NOW())
    RETURNING id_commande INTO _id_commande;

RETURN _id_commande;
END;
$$;