-- Création de la table avec type XML
CREATE TABLE CVTheque (
    id INT PRIMARY KEY IDENTITY(1,1),
    cv_id VARCHAR(50) UNIQUE NOT NULL,
    cv_data XML NOT NULL,
    date_creation DATETIME DEFAULT GETDATE(),
    derniere_maj DATETIME DEFAULT GETDATE(),
    INDEX idx_cv_id (cv_id)
);

-- Insertion d'un CV
INSERT INTO CVTheque (cv_id, cv_data)
VALUES ('CV001', 
'<cv id="CV001" dateCreation="2025-01-15">
    <entete>
        <nom>Benali</nom>
        <prenom>Karim</prenom>
        <adresseEmail>karim.benali@email.dz</adresseEmail>
    </entete>
    <!-- ... reste du CV ... -->
</cv>');

-- Requêtes XQuery pour interroger les données
-- Rechercher tous les CV avec un salaire > 100000
SELECT 
    cv_id,
    cv_data.value('(/cv/entete/nom)[1]', 'VARCHAR(100)') AS nom,
    cv_data.value('(/cv/entete/prenom)[1]', 'VARCHAR(100)') AS prenom,
    cv_data.value('(/cv/salaire)[1]', 'INT') AS salaire
FROM CVTheque
WHERE cv_data.value('(/cv/salaire)[1]', 'INT') > 100000;

-- Rechercher les CV avec compétence en Java
SELECT 
    cv_id,
    cv_data.value('(/cv/entete/nom)[1]', 'VARCHAR(100)') AS nom,
    cv_data.value('(/cv/entete/prenom)[1]', 'VARCHAR(100)') AS prenom
FROM CVTheque
WHERE cv_data.exist('/cv/competences/competence[text()="Java"]') = 1;

-- Créer des index XML pour améliorer les performances
CREATE PRIMARY XML INDEX idx_cv_data_primary ON CVTheque(cv_data);
CREATE XML INDEX idx_cv_data_path ON CVTheque(cv_data)
    USING XML INDEX idx_cv_data_primary FOR PATH;