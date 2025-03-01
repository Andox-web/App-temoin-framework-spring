-- Creation de la base de donnees
CREATE DATABASE ticketing;
\c ticketing;

-- Table avions
CREATE TABLE avions (
    id SERIAL PRIMARY KEY,
    numero_avion VARCHAR(100) UNIQUE NOT NULL,
    modele VARCHAR(100) NOT NULL
);

-- Table classes_siege
CREATE TABLE classes_siege (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- Table sieges_avion
CREATE TABLE sieges_avion(
    id SERIAL PRIMARY KEY,
    avion_id INT NOT NULL REFERENCES avions(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    nombre_sieges INT NOT NULL,
    UNIQUE(avion_id, classe_siege_id)
);

-- Table sieges
CREATE TABLE sieges (
    id SERIAL PRIMARY KEY,
    avion_id INT NOT NULL REFERENCES avions(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    numero_siege VARCHAR(100) UNIQUE NOT NULL
);

-- Table vols modifiee
CREATE TABLE vols (
    id SERIAL PRIMARY KEY,
    numero_vol VARCHAR(10) UNIQUE NOT NULL,
    avion_id INT NOT NULL REFERENCES avions(id) ON DELETE CASCADE,
    depart TIMESTAMP NOT NULL,
    arrivee TIMESTAMP,
    duree_estimee DECIMAL(10,2) NOT NULL,
    origine VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    limite_creation TIMESTAMP,
    limite_annulation TIMESTAMP
);

-- Restrictions par vol
CREATE TABLE restrictions_reservation (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL UNIQUE REFERENCES vols(id) ON DELETE CASCADE,
    delai_creation_heures DECIMAL(10,2) NOT NULL,
    delai_annulation_heures DECIMAL(10,2) NOT NULL
);

-- Restrictions globales
CREATE TABLE restrictions_reservation_global(
    id SERIAL PRIMARY KEY,
    delai_creation_heures DECIMAL(10,2) NOT NULL,
    delai_annulation_heures DECIMAL(10,2) NOT NULL,
    date_restriction TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL
);

-- Table utilisateurs
CREATE TABLE utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL
);

-- Table utilisateurs_roles
CREATE TABLE utilisateurs_roles (
    utilisateur_id INT NOT NULL REFERENCES utilisateurs(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (utilisateur_id, role_id)
);

-- Table promotions modifiee
CREATE TABLE promotions (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    description TEXT,
    pourcentage_reduction DECIMAL(10,2) CHECK (pourcentage_reduction BETWEEN 0 AND 100),
    classe_siege_id INT REFERENCES classes_siege(id) ON DELETE CASCADE,
    limite_sieges INT NOT NULL CHECK (limite_sieges > 0),
    promotion_utilisee INT DEFAULT 0 CHECK (promotion_utilisee <= limite_sieges),
    UNIQUE(vol_id, classe_siege_id)
);

-- Table vol_sieges
CREATE TABLE vol_sieges (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    siege_id INT NOT NULL REFERENCES sieges(id) ON DELETE CASCADE,
    est_reserve BOOLEAN DEFAULT FALSE
);

-- Table prix_vols_classes
CREATE TABLE prix_vols_classes (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0),
    UNIQUE(vol_id, classe_siege_id)
);

-- Table prix_classes_global
CREATE TABLE prix_classes_global (
    id SERIAL PRIMARY KEY,
    classe_siege_id INT NOT NULL UNIQUE REFERENCES classes_siege(id) ON DELETE CASCADE,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0)
);

-- Table reservations modifiee
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    siege_id INT NOT NULL REFERENCES sieges(id) ON DELETE CASCADE,
    nom_passager VARCHAR(100) NOT NULL,
    email_passager VARCHAR(255) NOT NULL,
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    promotion_id INT REFERENCES promotions(id) ON DELETE SET NULL,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0),
    reduction DECIMAL(10,2) CHECK (reduction BETWEEN 0 AND 100),
    passport_passager VARCHAR(255)
);

-- Vue occupation_sieges
CREATE OR REPLACE VIEW occupation_sieges AS
SELECT 
    v.id AS vol_id,
    v.numero_vol,
    cs.nom AS classe_siege,
    COUNT(vs.id) AS nombre_total_sieges,
    SUM(CASE WHEN vs.est_reserve THEN 1 ELSE 0 END) AS nombre_occupe,
    (COUNT(vs.id) - SUM(CASE WHEN vs.est_reserve THEN 1 ELSE 0 END)) AS nombre_libre,
    COALESCE(pvc.prix, pcg.prix) AS prix
FROM vols v
JOIN vol_sieges vs ON v.id = vs.vol_id
JOIN sieges s ON vs.siege_id = s.id
JOIN classes_siege cs ON s.classe_siege_id = cs.id
LEFT JOIN prix_vols_classes pvc ON v.id = pvc.vol_id AND cs.id = pvc.classe_siege_id
LEFT JOIN prix_classes_global pcg ON cs.id = pcg.classe_siege_id
GROUP BY v.id, v.numero_vol, cs.nom, pvc.prix, pcg.prix;

CREATE VIEW vue_prix_siege AS
SELECT 
    vs.vol_id,
    s.id AS siege_id,
    s.numero_siege,
    COALESCE(pvc.prix, pcg.prix) AS prix_base,
    prom.id as promotion_id,
    COALESCE(pvc.prix, pcg.prix) * (1 - COALESCE(prom.pourcentage_reduction, 0) / 100) AS prix_final
FROM vol_sieges vs
JOIN sieges s 
    ON vs.siege_id = s.id
LEFT JOIN prix_vols_classes pvc 
    ON vs.vol_id = pvc.vol_id 
    AND s.classe_siege_id = pvc.classe_siege_id
LEFT JOIN (
    SELECT p1.classe_siege_id, p1.prix
    FROM prix_classes_global p1
    INNER JOIN (
         SELECT classe_siege_id, MAX(id) AS max_id
         FROM prix_classes_global
         GROUP BY classe_siege_id
    ) p2 
      ON p1.classe_siege_id = p2.classe_siege_id 
     AND p1.id = p2.max_id
) pcg 
    ON s.classe_siege_id = pcg.classe_siege_id
LEFT JOIN promotions prom 
    ON vs.vol_id = prom.vol_id 
   AND s.classe_siege_id = prom.classe_siege_id
   AND prom.promotion_utilisee < prom.limite_sieges;

-- Fonctions et Triggers

-- Fonction pour les INSERT : Utilise les restrictions globales
CREATE OR REPLACE FUNCTION set_arrivee_on_insert() RETURNS TRIGGER AS $$
DECLARE
    global_restriction RECORD;
BEGIN
    -- Calcul de l'arrivée
    NEW.arrivee := NEW.depart + (NEW.duree_estimee * INTERVAL '1 hour');

    -- Récupérer la dernière restriction globale
    SELECT delai_creation_heures, delai_annulation_heures 
    INTO global_restriction
    FROM restrictions_reservation_global
    ORDER BY date_restriction DESC
    LIMIT 1;

    -- Appliquer les restrictions globales ou les valeurs par défaut
    IF FOUND THEN
        NEW.limite_creation := NEW.depart - (global_restriction.delai_creation_heures * INTERVAL '1 hour');
        NEW.limite_annulation := NEW.depart - (global_restriction.delai_annulation_heures * INTERVAL '1 hour');
    ELSE
        NEW.limite_creation := NEW.depart - (24 * INTERVAL '1 hour');
        NEW.limite_annulation := NEW.depart - (2 * INTERVAL '1 hour');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Déclencheur pour les INSERT
CREATE TRIGGER trigger_set_arrivee_on_insert
BEFORE INSERT ON vols
FOR EACH ROW
EXECUTE PROCEDURE set_arrivee_on_insert();

-- Fonction pour les UPDATE : Utilise les restrictions spécifiques (si elles existent)
CREATE OR REPLACE FUNCTION set_arrivee_on_update() RETURNS TRIGGER AS $$
DECLARE
    specific_restriction RECORD;
BEGIN
    -- Recalculer l'arrivée si la durée estimée change
    IF NEW.duree_estimee IS DISTINCT FROM OLD.duree_estimee THEN
        NEW.arrivee := NEW.depart + (NEW.duree_estimee * INTERVAL '1 hour');
    END IF;

    -- Vérifier s'il existe une restriction spécifique pour ce vol
    SELECT delai_creation_heures, delai_annulation_heures 
    INTO specific_restriction
    FROM restrictions_reservation
    WHERE vol_id = NEW.id;

    -- Appliquer les restrictions spécifiques si elles existent
    IF FOUND THEN
        NEW.limite_creation := NEW.depart - (specific_restriction.delai_creation_heures * INTERVAL '1 hour');
        NEW.limite_annulation := NEW.depart - (specific_restriction.delai_annulation_heures * INTERVAL '1 hour');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Déclencheur pour les UPDATE
CREATE TRIGGER trigger_set_arrivee_on_update
BEFORE UPDATE ON vols
FOR EACH ROW
EXECUTE PROCEDURE set_arrivee_on_update();

-- Gestion des restrictions par vol
CREATE OR REPLACE FUNCTION set_global_restriction_defaults() RETURNS TRIGGER AS $$
DECLARE
    last_global RECORD;
BEGIN
    -- Recuperer la derniere restriction globale
    SELECT * INTO last_global 
    FROM restrictions_reservation_global 
    ORDER BY date_restriction DESC 
    LIMIT 1;

    -- Appliquer les valeurs par defaut si necessaire
    NEW.delai_creation_heures := COALESCE(
        NEW.delai_creation_heures,
        last_global.delai_creation_heures,
        24 -- Valeur par defaut absolue
    );
    
    NEW.delai_annulation_heures := COALESCE(
        NEW.delai_annulation_heures,
        last_global.delai_annulation_heures,
        2 -- Valeur par defaut absolue
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_global_restriction_defaults
BEFORE INSERT ON restrictions_reservation_global
FOR EACH ROW
EXECUTE PROCEDURE set_global_restriction_defaults();

CREATE OR REPLACE FUNCTION set_specific_restriction_defaults() RETURNS TRIGGER AS $$
DECLARE
    last_global RECORD;
BEGIN
    -- Recuperer la derniere restriction globale
    SELECT * INTO last_global 
    FROM restrictions_reservation_global 
    ORDER BY date_restriction DESC 
    LIMIT 1;

    -- Appliquer les valeurs par defaut si necessaire
    NEW.delai_creation_heures := COALESCE(
        NEW.delai_creation_heures,
        last_global.delai_creation_heures,
        24 -- Valeur par defaut si aucune restriction globale
    );
    
    NEW.delai_annulation_heures := COALESCE(
        NEW.delai_annulation_heures,
        last_global.delai_annulation_heures,
        2 -- Valeur par defaut si aucune restriction globale
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_specific_restriction_defaults
BEFORE INSERT ON restrictions_reservation
FOR EACH ROW
EXECUTE PROCEDURE set_specific_restriction_defaults();

CREATE OR REPLACE FUNCTION set_restriction_vol() RETURNS TRIGGER AS $$
BEGIN
    UPDATE vols SET
        limite_creation = depart - (NEW.delai_creation_heures * INTERVAL '1 hour'),
        limite_annulation = depart - (NEW.delai_annulation_heures * INTERVAL '1 hour')
    WHERE id = NEW.vol_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_restriction_vol
AFTER INSERT OR UPDATE ON restrictions_reservation
FOR EACH ROW
EXECUTE PROCEDURE set_restriction_vol();

-- Generation des sieges
CREATE OR REPLACE FUNCTION generate_sieges_from_avion() RETURNS TRIGGER AS $$
DECLARE
    classe_nom VARCHAR(50);
    avion_nom VARCHAR(100);
BEGIN
    SELECT numero_avion INTO avion_nom FROM avions WHERE id = NEW.avion_id;
    SELECT nom INTO classe_nom FROM classes_siege WHERE id = NEW.classe_siege_id;

    FOR i IN 1..NEW.nombre_sieges LOOP
        INSERT INTO sieges (avion_id, classe_siege_id, numero_siege)
        VALUES (NEW.avion_id, NEW.classe_siege_id, CONCAT(avion_nom, '-', classe_nom, '-S', i));
    END LOOP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_generate_sieges
AFTER INSERT ON sieges_avion
FOR EACH ROW
EXECUTE PROCEDURE generate_sieges_from_avion();

-- Attribution des sieges aux vols
CREATE OR REPLACE FUNCTION attribuer_sieges_vol() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO vol_sieges (vol_id, siege_id)
    SELECT NEW.id, s.id 
    FROM sieges s 
    WHERE s.avion_id = NEW.avion_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_attribuer_sieges_vol
AFTER INSERT ON vols
FOR EACH ROW
EXECUTE PROCEDURE attribuer_sieges_vol();

-- Gestion des reservations avec promotion
CREATE OR REPLACE FUNCTION reserver_siege() RETURNS TRIGGER AS $$
DECLARE
    promotion_classe_id INT;
    prix_base DECIMAL(10,2);
    siege_reserve BOOLEAN;
BEGIN
    -- Verifier si le siege est deja reserve
    SELECT est_reserve INTO siege_reserve
    FROM vol_sieges
    WHERE siege_id = NEW.siege_id
    FOR UPDATE;

    IF siege_reserve THEN
        RAISE EXCEPTION 'Ce siege est deja reserve';
    END IF;

    -- Verifier la date limite de reservation
    PERFORM 1 FROM vols 
    WHERE id = NEW.vol_id 
    AND CURRENT_TIMESTAMP <= limite_creation;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Reservation impossible : vol ferme aux reservations';
    END IF;

    -- Verifier la promotion
    IF NEW.promotion_id IS NOT NULL THEN
        SELECT classe_siege_id INTO promotion_classe_id 
        FROM promotions 
        WHERE id = NEW.promotion_id 
        FOR UPDATE;

        IF promotion_classe_id IS NOT NULL AND promotion_classe_id != NEW.classe_siege_id THEN
            RAISE EXCEPTION 'Promotion non valable pour cette classe de siege';
        END IF;
    END IF;

    -- Calcul du prix de base
    SELECT COALESCE(
        (SELECT prix FROM prix_vols_classes 
         WHERE vol_id = NEW.vol_id AND classe_siege_id = NEW.classe_siege_id),
        (SELECT prix FROM prix_classes_global 
         WHERE classe_siege_id = NEW.classe_siege_id)
    ) INTO prix_base;

    -- Appliquer la promotion si disponible
    IF NEW.promotion_id IS NOT NULL THEN
        IF (SELECT promotion_utilisee < limite_sieges FROM promotions WHERE id = NEW.promotion_id) THEN
            NEW.reduction := (SELECT pourcentage_reduction FROM promotions WHERE id = NEW.promotion_id);
            NEW.prix := prix_base - (prix_base * NEW.reduction / 100);
            UPDATE promotions SET promotion_utilisee = promotion_utilisee + 1 WHERE id = NEW.promotion_id;
        ELSE
            NEW.promotion_id := NULL;
            NEW.reduction := NULL;
            NEW.prix := prix_base;
        END IF;
    ELSE
        NEW.prix := prix_base;
        NEW.reduction := NULL;
    END IF;

    -- Marquer le siege comme reserve
    UPDATE vol_sieges SET est_reserve = TRUE WHERE siege_id = NEW.siege_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_reserver_siege
BEFORE INSERT ON reservations
FOR EACH ROW
EXECUTE PROCEDURE reserver_siege();

-- Liberation des sieges
CREATE OR REPLACE FUNCTION liberer_siege() RETURNS TRIGGER AS $$
BEGIN
    UPDATE vol_sieges SET est_reserve = FALSE WHERE siege_id = OLD.siege_id;
    IF OLD.promotion_id IS NOT NULL THEN
        UPDATE promotions SET promotion_utilisee = promotion_utilisee - 1 WHERE id = OLD.promotion_id;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_liberer_siege
AFTER DELETE ON reservations
FOR EACH ROW
EXECUTE PROCEDURE liberer_siege();

-- Gestion des promotions (UPSERT)
CREATE OR REPLACE FUNCTION check_existing_promotion() RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM promotions 
               WHERE vol_id = NEW.vol_id 
               AND classe_siege_id = NEW.classe_siege_id) THEN
        UPDATE promotions SET
            description = NEW.description,
            pourcentage_reduction = NEW.pourcentage_reduction,
            limite_sieges = NEW.limite_sieges
        WHERE vol_id = NEW.vol_id AND classe_siege_id = NEW.classe_siege_id;
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_existing_promotion
BEFORE INSERT ON promotions
FOR EACH ROW
EXECUTE PROCEDURE check_existing_promotion();

-- Validation modification promotion
CREATE OR REPLACE FUNCTION check_promotion_limite() RETURNS TRIGGER AS $$
BEGIN
    IF OLD.promotion_utilisee > NEW.limite_sieges THEN
        RAISE EXCEPTION 'Impossible de reduire la limite : % sieges deja utilises', OLD.promotion_utilisee;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_promotion_limite
BEFORE UPDATE ON promotions
FOR EACH ROW
EXECUTE PROCEDURE check_promotion_limite();

-- Verification annulation
CREATE OR REPLACE FUNCTION verifier_annulation() RETURNS TRIGGER AS $$
BEGIN
    PERFORM 1 FROM vols 
    WHERE id = OLD.vol_id 
    AND CURRENT_TIMESTAMP <= limite_annulation;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Annulation impossible : delai d''annulation depasse';
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verifier_annulation
BEFORE DELETE ON reservations
FOR EACH ROW
EXECUTE PROCEDURE verifier_annulation();

-- Insertions initiales
INSERT INTO avions (numero_avion, modele) VALUES
('AV001', 'Boeing 737'),
('AV002', 'Airbus A320'),
('AV003', 'Boeing 787');

INSERT INTO classes_siege (nom, description) VALUES
('Economique', 'Sieges standards avec bon rapport confort/prix'),
('Affaires', 'Sieges plus larges avec espace supplementaire'),
('Premiere', 'Service haut de gamme et sieges premium');

INSERT INTO sieges_avion (avion_id, classe_siege_id, nombre_sieges) VALUES
(1, 1, 150), (1, 2, 30), (1, 3, 10),
(2, 1, 120), (2, 2, 20), (2, 3, 8),
(3, 1, 200), (3, 2, 40), (3, 3, 15);

INSERT INTO prix_classes_global (classe_siege_id, prix) VALUES
(1, 100.00), (2, 300.00), (3, 600.00);

INSERT INTO restrictions_reservation_global (delai_creation_heures, delai_annulation_heures) 
VALUES (24, 2);

INSERT INTO roles (nom) VALUES ('admin');

INSERT INTO utilisateurs (nom, email, mot_de_passe) VALUES
('Admin', 'admin@example.com', 'pwd123');

INSERT INTO utilisateurs_roles (utilisateur_id, role_id) VALUES
(1, 1);
