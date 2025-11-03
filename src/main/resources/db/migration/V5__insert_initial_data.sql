
-- Fonction BCrypt (PostgreSQL avec pgcrypto)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.utilisateur (nom, email, mdp, role) VALUES
                                                           ('Johny', 'johny@gmail.com', crypt('johny123', gen_salt('bf')), 'ADMIN'),
                                                           ('Kezia', 'kezia@gmail.com', crypt('kezia123', gen_salt('bf')), 'CAISSIER'),
                                                           ('Saira', 'saira@gmail.com', crypt('saira456', gen_salt('bf')), 'CAISSIER'),
                                                           ('Mamy', 'mamy@gmail.com', crypt('mamy1234', gen_salt('bf')), 'CAISSIER'),
                                                           ('Xavier', 'xavier@gmail.com', crypt('dera1234', gen_salt('bf')), 'ADMIN')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO public.produit (nom, prix, stock) VALUES
                                                  ('Tableaux', 2500.00, 200),
                                                  ('Bierre', 5000.00, 500),
                                                  ('Cafe', 15000.00, 100),
                                                  ('PC', 1000000.00, 20),
                                                  ('Eponge', 1000.00, 200),
                                                  ('TV', 500000.00, 80),
                                                  ('Cube', 8000.00, 80),
                                                  ('Coca', 2000.00, 300),
                                                  ('Sac', 15000.00, 50),
                                                  ('Guitar', 150000.00, 10),
                                                  ('Eau vive', 3000.00, 250),
                                                  ('Router', 100000.00, 60)
    ON CONFLICT (nom) DO NOTHING;