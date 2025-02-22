-- Creation de la base de donnees
CREATE DATABASE ticketing;
\c ticketing;

CREATE TABLE avions (
    id SERIAL PRIMARY KEY,
    numero_avion VARCHAR(100) UNIQUE NOT NULL,
    modele VARCHAR(100) NOT NULL
);

CREATE TABLE classes_siege (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE sieges_avion(
    id SERIAL PRIMARY KEY,
    avion_id INT NOT NULL REFERENCES avions(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    nombre_sieges INT NOT NULL,
    UNIQUE(avion_id, classe_siege_id)
);

CREATE TABLE sieges (
    id SERIAL PRIMARY KEY,
    avion_id INT NOT NULL REFERENCES avions(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    numero_siege VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE vols (
    id SERIAL PRIMARY KEY,
    numero_vol VARCHAR(10) UNIQUE NOT NULL,
    avion_id INT NOT NULL REFERENCES avions(id) ON DELETE CASCADE,
    depart TIMESTAMP NOT NULL,
    arrivee TIMESTAMP DEFAULT NULL,
    limite_activiter TIMESTAMP DEFAULT NULL,
    duree_estimee INT NOT NULL,
    origine VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL
);

CREATE TABLE restrictions_reservation (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL UNIQUE REFERENCES vols(id) ON DELETE CASCADE,
    duree INT NOT NULL
);

CREATE TABLE restrictions_reservation_global(
    id SERIAL PRIMARY KEY,
    duree INT NOT NULL,
    date_restriction TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION set_arrivee() RETURNS TRIGGER AS $$
DECLARE
    restriction_globale INT;
BEGIN
    NEW.arrivee := NEW.depart + (NEW.duree_estimee * INTERVAL '1 minute');

    SELECT duree INTO restriction_globale
    FROM restrictions_reservation_global
    ORDER BY date_restriction DESC
    LIMIT 1;

    IF restriction_globale IS NOT NULL THEN
        NEW.limite_activiter := NEW.depart - (restriction_globale * INTERVAL '1 minute');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_arrivee
BEFORE INSERT OR UPDATE ON vols
FOR EACH ROW
EXECUTE FUNCTION set_arrivee();

CREATE OR REPLACE FUNCTION set_restriction_vol() RETURNS TRIGGER AS $$
BEGIN
    PERFORM 1 FROM vols 
    WHERE id = NEW.vol_id 
    AND CURRENT_TIMESTAMP > limite_activiter;

    IF FOUND THEN
        RAISE EXCEPTION 'Modification impossible : limite depassee';
    END IF;

    UPDATE vols 
    SET limite_activiter = depart - (NEW.duree * INTERVAL '1 minute')
    WHERE id = NEW.vol_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_restriction_vol
AFTER INSERT OR UPDATE ON restrictions_reservation
FOR EACH ROW
EXECUTE FUNCTION set_restriction_vol();

CREATE TABLE promotions (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    description TEXT,
    pourcentage_reduction DECIMAL(5,2) CHECK (pourcentage_reduction BETWEEN 0 AND 100),
    classe_siege_id INT REFERENCES classes_siege(id) ON DELETE CASCADE,
    limite_sieges INT NOT NULL CHECK (limite_sieges > 0),
    promotion_utilisee INT DEFAULT 0 CHECK (promotion_utilisee <= limite_sieges)
);

CREATE TABLE vol_sieges (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    siege_id INT NOT NULL REFERENCES sieges(id) ON DELETE CASCADE,
    est_reserve BOOLEAN DEFAULT FALSE
);

CREATE TABLE prix_vols_classes (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0),
    UNIQUE(vol_id, classe_siege_id)
);

CREATE TABLE prix_classes_global (
    id SERIAL PRIMARY KEY,
    classe_siege_id INT NOT NULL UNIQUE REFERENCES classes_siege(id) ON DELETE CASCADE,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0)
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    vol_id INT NOT NULL REFERENCES vols(id) ON DELETE CASCADE,
    classe_siege_id INT NOT NULL REFERENCES classes_siege(id) ON DELETE CASCADE,
    siege_id INT NOT NULL REFERENCES sieges(id) ON DELETE CASCADE,
    nom_passager VARCHAR(100) NOT NULL,
    email_passager VARCHAR(255) NOT NULL,
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    promotion_id INT REFERENCES promotions(id) ON DELETE SET NULL,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0)
);

CREATE VIEW occupation_sieges AS
SELECT 
    v.id AS vol_id,
    v.numero_vol,
    cs.nom AS classe_siege,
    COUNT(vs.id) AS nombre_total_sieges,
    SUM(CASE WHEN vs.est_reserve THEN 1 ELSE 0 END) AS nombre_occupe,
    (COUNT(vs.id) - SUM(CASE WHEN vs.est_reserve THEN 1 ELSE 0 END)) AS nombre_libre
FROM vols v
JOIN vol_sieges vs ON v.id = vs.vol_id
JOIN sieges s ON vs.siege_id = s.id
JOIN classes_siege cs ON s.classe_siege_id = cs.id
GROUP BY v.id, v.numero_vol, cs.nom;

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

CREATE OR REPLACE FUNCTION reserver_siege() RETURNS TRIGGER AS $$
DECLARE
    promotion_classe_id INT;
    prix_base DECIMAL(10,2);
BEGIN
    PERFORM 1 FROM vols 
    WHERE id = NEW.vol_id 
    AND CURRENT_TIMESTAMP <= limite_activiter;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Reservation impossible : vol ferme';
    END IF;

    IF NEW.promotion_id IS NOT NULL THEN
        SELECT classe_siege_id INTO promotion_classe_id 
        FROM promotions 
        WHERE id = NEW.promotion_id 
        FOR UPDATE;

        IF promotion_classe_id IS NOT NULL AND promotion_classe_id != NEW.classe_siege_id THEN
            RAISE EXCEPTION 'Promotion non applicable a cette classe';
        END IF;
    END IF;

    SELECT COALESCE(
        (SELECT prix FROM prix_vols_classes 
         WHERE vol_id = NEW.vol_id AND classe_siege_id = NEW.classe_siege_id),
        (SELECT prix FROM prix_classes_global 
         WHERE classe_siege_id = NEW.classe_siege_id)
    ) INTO prix_base;

    IF NEW.promotion_id IS NOT NULL THEN
        IF (SELECT promotion_utilisee < limite_sieges FROM promotions WHERE id = NEW.promotion_id) THEN
            NEW.prix := prix_base - (prix_base * (SELECT pourcentage_reduction FROM promotions WHERE id = NEW.promotion_id) / 100);
            UPDATE promotions SET promotion_utilisee = promotion_utilisee + 1 WHERE id = NEW.promotion_id;
        ELSE
            NEW.promotion_id := NULL;
            NEW.prix := prix_base;
        END IF;
    ELSE
        NEW.prix := prix_base;
    END IF;

    UPDATE vol_sieges SET est_reserve = TRUE WHERE id = NEW.siege_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_reserver_siege
BEFORE INSERT ON reservations
FOR EACH ROW
EXECUTE PROCEDURE reserver_siege();

CREATE OR REPLACE FUNCTION liberer_siege() RETURNS TRIGGER AS $$
BEGIN
    UPDATE vol_sieges SET est_reserve = FALSE WHERE id = OLD.siege_id;
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

INSERT INTO avions (numero_avion, modele) VALUES
('AV001', 'Boeing 737'),
('AV002', 'Airbus A320'),
('AV003', 'Boeing 787');

INSERT INTO classes_siege (nom, description) VALUES
('Economique', 'Sieges standards avec un bon rapport confort/prix'),
('Affaires', 'Sieges plus larges avec plus despace pour les jambes'),
('Premiere', 'Sieges haut de gamme avec service personnalise');

-- Pour l'avion AV001 (Boeing 737)
INSERT INTO sieges_avion (avion_id, classe_siege_id, nombre_sieges) VALUES
(1, 1, 150), -- 150 sieges Economique
(1, 2, 30),  -- 30 sieges Affaires
(1, 3, 10);  -- 10 sieges Premiere

-- Pour l'avion AV002 (Airbus A320)
INSERT INTO sieges_avion (avion_id, classe_siege_id, nombre_sieges) VALUES
(2, 1, 120), -- 120 sieges Economique
(2, 2, 20),  -- 20 sieges Affaires
(2, 3, 8);   -- 8 sieges Premiere

-- Pour l'avion AV003 (Boeing 787)
INSERT INTO sieges_avion (avion_id, classe_siege_id, nombre_sieges) VALUES
(3, 1, 200), -- 200 sieges Economique
(3, 2, 40),  -- 40 sieges Affaires
(3, 3, 15);  -- 15 sieges Premiere

INSERT INTO prix_classes_global (classe_siege_id, prix) VALUES
(1, 100.00), -- Prix de base pour la classe Economique
(2, 300.00), -- Prix de base pour la classe Affaires
(3, 600.00); -- Prix de base pour la classe Premiere

INSERT INTO restrictions_reservation_global (duree) VALUES
(1440); -- 1440 minutes = 24 heures (restriction globale de 24 heures avant le depart)