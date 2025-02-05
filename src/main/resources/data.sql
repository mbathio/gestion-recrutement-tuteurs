-- Insertion des utilisateurs (Administrateurs et Candidats)
INSERT INTO user (email, password, nom, prenom, role) VALUES
('admin@uvs.sn', '$2a$10$hXrlgk2...', 'Admin', 'Principal', 'ADMIN'),
('candidat1@uvs.sn', '$2a$10$hXrlgk2...', 'Doe', 'John', 'CANDIDAT'),
('candidat2@uvs.sn', '$2a$10$hXrlgk2...', 'Smith', 'Alice', 'CANDIDAT');

-- Insertion des années académiques
INSERT INTO annee_academique (annee) VALUES 
('2023-2024'), 
('2024-2025');

-- Insertion des annonces de recrutement
INSERT INTO annonce (titre, description, annee_academique_id) VALUES 
('Recrutement Tuteurs Informatique', 'Nous recrutons des tuteurs en informatique...', 1),
('Recrutement Tuteurs Mathématiques', 'Nous recrutons des tuteurs en mathématiques...', 2);

-- Insertion de candidatures
INSERT INTO candidature (candidat_id, annonce_id, statut) VALUES 
(2, 1, 'EN_COURS'), 
(3, 2, 'ACCEPTEE');
