
-- Fonction BCrypt (PostgreSQL avec pgcrypto)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.utilisateur (nom, email, mdp, role) VALUES
                                                           ('Johny', 'joh@gmail.com', crypt('admin123', gen_salt('bf')), 'ADMIN'),
                                                           ('Vie', 'vie@gmail.com', crypt('caisse123', gen_salt('bf')), 'CAISSIER'),
                                                           ('Xavier', 'xavier@gmail.com', crypt('admin456', gen_salt('bf')), 'ADMIN'),
                                                           ('Alice', 'alice@gmail.com', crypt('caisse456', gen_salt('bf')), 'CAISSIER');

INSERT INTO public.produit (nom, prix, stock) VALUES
                                                  ('Tableaux', 250.00, 200),
                                                  ('Bierre', 4500.00, 244),
                                                  ('Cafe', 1500.00, 113),
                                                  ('PC', 1500.00, 414),
                                                  ('Eponge', 200.00, 200),
                                                  ('TV', 5000.00, 199),
                                                  ('Cube', 7000.00, 299),
                                                  ('Coke', 1500.00, 448),
                                                  ('Router', 15000.00, 87);