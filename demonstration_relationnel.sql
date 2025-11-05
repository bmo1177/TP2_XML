-- Schéma relationnel complet
CREATE TABLE Candidat (
    candidat_id INT PRIMARY KEY IDENTITY(1,1),
    cv_id VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE,
    lieu_naissance VARCHAR(200),
    adresse_email VARCHAR(200),
    photo_url VARCHAR(500),
    niveau VARCHAR(50),
    salaire_demande DECIMAL(10,2),
    devise VARCHAR(10),
    periode VARCHAR(20),
    date_creation DATETIME DEFAULT GETDATE(),
    derniere_maj DATETIME DEFAULT GETDATE(),
    INDEX idx_email (adresse_email),
    INDEX idx_niveau (niveau)
);

CREATE TABLE Poste (
    poste_id INT PRIMARY KEY IDENTITY(1,1),
    candidat_id INT NOT NULL,
    intitule VARCHAR(200) NOT NULL,
    entreprise VARCHAR(200),
    date_debut DATE,
    date_fin DATE,
    en_cours BIT DEFAULT 0,
    FOREIGN KEY (candidat_id) REFERENCES Candidat(candidat_id) ON DELETE CASCADE
);

CREATE TABLE Diplome (
    diplome_id INT PRIMARY KEY IDENTITY(1,1),
    candidat_id INT NOT NULL,
    intitule VARCHAR(200) NOT NULL,
    etablissement VARCHAR(200) NOT NULL,
    annee_obtention INT,
    niveau VARCHAR(50),
    FOREIGN KEY (candidat_id) REFERENCES Candidat(candidat_id) ON DELETE CASCADE
);

CREATE TABLE Stage (
    stage_id INT PRIMARY KEY IDENTITY(1,1),
    candidat_id INT NOT NULL,
    titre VARCHAR(200) NOT NULL,
    entreprise VARCHAR(200),
    duree VARCHAR(50),
    date_debut DATE,
    date_fin DATE,
    description TEXT,
    FOREIGN KEY (candidat_id) REFERENCES Candidat(candidat_id) ON DELETE CASCADE
);

CREATE TABLE Competence (
    competence_id INT PRIMARY KEY IDENTITY(1,1),
    candidat_id INT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    categorie VARCHAR(50),
    niveau_maitrise VARCHAR(50),
    FOREIGN KEY (candidat_id) REFERENCES Candidat(candidat_id) ON DELETE CASCADE,
    INDEX idx_competence_nom (nom)
);

CREATE TABLE Langue (
    langue_id INT PRIMARY KEY IDENTITY(1,1),
    candidat_id INT NOT NULL,
    nom VARCHAR(50) NOT NULL,
    niveau_oral VARCHAR(10),
    niveau_ecrit VARCHAR(10),
    FOREIGN KEY (candidat_id) REFERENCES Candidat(candidat_id) ON DELETE CASCADE
);

CREATE TABLE Loisir (
    loisir_id INT PRIMARY KEY IDENTITY(1,1),
    candidat_id INT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    FOREIGN KEY (candidat_id) REFERENCES Candidat(candidat_id) ON DELETE CASCADE
);

-- Procédure stockée pour importer un CV depuis XML
CREATE PROCEDURE ImporterCV
    @xml_data XML
AS
BEGIN
    DECLARE @candidat_id INT;
    DECLARE @cv_id VARCHAR(50);
    
    -- Extraction des données principales
    SET @cv_id = @xml_data.value('(/cv/@id)[1]', 'VARCHAR(50)');
    
    -- Insertion du candidat
    INSERT INTO Candidat (cv_id, nom, prenom, date_naissance, lieu_naissance, 
                          adresse_email, niveau, salaire_demande, devise, periode)
    SELECT
        @cv_id,
        @xml_data.value('(/cv/entete/nom)[1]', 'VARCHAR(100)'),
        @xml_data.value('(/cv/entete/prenom)[1]', 'VARCHAR(100)'),
        @xml_data.value('(/cv/entete/dateNaissance)[1]', 'DATE'),
        @xml_data.value('(/cv/entete/lieuNaissance)[1]', 'VARCHAR(200)'),
        @xml_data.value('(/cv/entete/adresseEmail)[1]', 'VARCHAR(200)'),
        @xml_data.value('(/cv/niveau/@valeur)[1]', 'VARCHAR(50)'),
        @xml_data.value('(/cv/salaire)[1]', 'DECIMAL(10,2)'),
        @xml_data.value('(/cv/salaire/@devise)[1]', 'VARCHAR(10)'),
        @xml_data.value('(/cv/salaire/@periode)[1]', 'VARCHAR(20)');
    
    SET @candidat_id = SCOPE_IDENTITY();
    
    -- Insertion des postes
    INSERT INTO Poste (candidat_id, intitule, entreprise, date_debut, date_fin)
    SELECT 
        @candidat_id,
        T.c.value('text()[1]', 'VARCHAR(200)'),
        T.c.value('@entreprise', 'VARCHAR(200)'),
        T.c.value('@dateDebut', 'DATE'),
        T.c.value('@dateFin', 'DATE')
    FROM @xml_data.nodes('/cv/postes/poste') AS T(c);
    
    -- Insertion des compétences
    INSERT INTO Competence (candidat_id, nom, categorie, niveau_maitrise)
    SELECT 
        @candidat_id,
        T.c.value('text()[1]', 'VARCHAR(100)'),
        T.c.value('@categorie', 'VARCHAR(50)'),
        T.c.value('@niveauMaitrise', 'VARCHAR(50)')
    FROM @xml_data.nodes('/cv/competences/competence') AS T(c);
    
    -- [Continuer pour les autres tables...]
END;

-- Requêtes analytiques facilitées
-- Trouver les candidats experts en Java
SELECT c.nom, c.prenom, c.adresse_email, comp.niveau_maitrise
FROM Candidat c
INNER JOIN Competence comp ON c.candidat_id = comp.candidat_id
WHERE comp.nom = 'Java' AND comp.niveau_maitrise IN ('Expert', 'Avance');

-- Statistiques par niveau de candidat
SELECT 
    niveau,
    COUNT(*) as nombre_candidats,
    AVG(salaire_demande) as salaire_moyen
FROM Candidat
GROUP BY niveau; 