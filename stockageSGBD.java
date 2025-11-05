import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Classe pour gérer le stockage des CV XML dans un SGBD relationnel (SQL Server)
 * Conforme au TP2 - Question 6
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class StockageSGBD {
    
    // Configuration de la base de données
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=CVTHEQUE;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "VotreMotDePasse";
    
    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     STOCKAGE CV XML EN BASE DE DONNÉES SQL SERVER     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        while (true) {
            afficherMenu();
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            switch (choix) {
                case 1:
                    creerTablesRelationnelles();
                    break;
                case 2:
                    creerTablesAvecXML();
                    break;
                case 3:
                    importerCVRelationnels("Cv.xml");
                    break;
                case 4:
                    importerCVAvecXML("Cv.xml");
                    break;
                case 5:
                    rechercherCVParCompetence();
                    break;
                case 6:
                    afficherTousLesCV();
                    break;
                case 0:
                    System.out.println("\n✓ Au revoir !");
                    scanner.close();
                    return;
                default:
                    System.out.println("\n✗ Choix invalide !\n");
            }
        }
    }
    
    private static void afficherMenu() {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│              MENU STOCKAGE SGBD                 │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│ APPROCHE 1 : Décomposition relationnelle       │");
        System.out.println("│   1. Créer les tables relationnelles           │");
        System.out.println("│   3. Importer CV (approche relationnelle)      │");
        System.out.println("│                                                 │");
        System.out.println("│ APPROCHE 2 : Stockage XML natif                │");
        System.out.println("│   2. Créer table avec colonne XML              │");
        System.out.println("│   4. Importer CV (stockage XML complet)        │");
        System.out.println("│                                                 │");
        System.out.println("│ REQUÊTES :                                      │");
        System.out.println("│   5. Rechercher CV par compétence              │");
        System.out.println("│   6. Afficher tous les CV                      │");
        System.out.println("│                                                 │");
        System.out.println("│   0. Quitter                                    │");
        System.out.println("└─────────────────────────────────────────────────┘");
    }
    
    /**
     * APPROCHE 1 : Création des tables relationnelles
     * Décomposition du XML en tables normalisées
     */
    public static void creerTablesRelationnelles() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  CRÉATION TABLES RELATIONNELLES");
        System.out.println("═══════════════════════════════════════\n");
        
        String[] sqlStatements = {
            // Table principale CV
            """
            CREATE TABLE CV (
                id INT PRIMARY KEY IDENTITY(1,1),
                code VARCHAR(20) UNIQUE NOT NULL,
                nom VARCHAR(50) NOT NULL,
                prenom VARCHAR(50) NOT NULL,
                dateNaissance DATE,
                lieuNaissance VARCHAR(100),
                email VARCHAR(100),
                image VARCHAR(255),
                niveau VARCHAR(20),
                typeNiveau VARCHAR(20),
                salaire DECIMAL(10,2),
                devise VARCHAR(10),
                periodeSalaire VARCHAR(20),
                dateCreation DATETIME DEFAULT GETDATE()
            )
            """,
            
            // Table Postes
            """
            CREATE TABLE Poste (
                id INT PRIMARY KEY IDENTITY(1,1),
                cv_id INT NOT NULL,
                intitule VARCHAR(100) NOT NULL,
                entreprise VARCHAR(100),
                dateDebut VARCHAR(20),
                dateFin VARCHAR(20),
                FOREIGN KEY (cv_id) REFERENCES CV(id) ON DELETE CASCADE
            )
            """,
            
            // Table Diplômes
            """
            CREATE TABLE Diplome (
                id INT PRIMARY KEY IDENTITY(1,1),
                cv_id INT NOT NULL,
                intitule VARCHAR(150) NOT NULL,
                annee VARCHAR(10),
                etablissement VARCHAR(150),
                specialite VARCHAR(100),
                FOREIGN KEY (cv_id) REFERENCES CV(id) ON DELETE CASCADE
            )
            """,
            
            // Table Stages
            """
            CREATE TABLE Stage (
                id INT PRIMARY KEY IDENTITY(1,1),
                cv_id INT NOT NULL,
                intitule VARCHAR(150) NOT NULL,
                entreprise VARCHAR(100),
                dateDebut VARCHAR(20),
                dateFin VARCHAR(20),
                description VARCHAR(500),
                FOREIGN KEY (cv_id) REFERENCES CV(id) ON DELETE CASCADE
            )
            """,
            
            // Table Compétences
            """
            CREATE TABLE Competence (
                id INT PRIMARY KEY IDENTITY(1,1),
                cv_id INT NOT NULL,
                nom VARCHAR(100) NOT NULL,
                niveauMaitrise VARCHAR(20),
                FOREIGN KEY (cv_id) REFERENCES CV(id) ON DELETE CASCADE
            )
            """,
            
            // Table Langues
            """
            CREATE TABLE Langue (
                id INT PRIMARY KEY IDENTITY(1,1),
                cv_id INT NOT NULL,
                nom VARCHAR(50) NOT NULL,
                niveau VARCHAR(10),
                FOREIGN KEY (cv_id) REFERENCES CV(id) ON DELETE CASCADE
            )
            """,
            
            // Table Loisirs
            """
            CREATE TABLE Loisir (
                id INT PRIMARY KEY IDENTITY(1,1),
                cv_id INT NOT NULL,
                nom VARCHAR(100) NOT NULL,
                FOREIGN KEY (cv_id) REFERENCES CV(id) ON DELETE CASCADE
            )
            """
        };
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Supprimer les tables existantes
            System.out.println("Suppression des anciennes tables...");
            String[] dropTables = {"Loisir", "Langue", "Competence", "Stage", "Diplome", "Poste", "CV"};
            for (String table : dropTables) {
                try {
                    stmt.execute("DROP TABLE IF EXISTS " + table);
                } catch (SQLException e) {
                    // Ignorer les erreurs si la table n'existe pas
                }
            }
            
            // Créer les nouvelles tables
            System.out.println("Création des nouvelles tables...\n");
            for (String sql : sqlStatements) {
                stmt.execute(sql);
                String tableName = sql.substring(sql.indexOf("TABLE") + 6, sql.indexOf("(")).trim();
                System.out.println("✓ Table créée : " + tableName);
            }
            
            System.out.println("\n✓ Toutes les tables ont été créées avec succès !");
            System.out.println("  Structure relationnelle complète établie.\n");
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la création des tables :");
            System.err.println("  " + e.getMessage());
            System.err.println("\n  Assurez-vous que :");
            System.err.println("  - SQL Server est en cours d'exécution");
            System.err.println("  - La base CVTHEQUE existe");
            System.err.println("  - Les identifiants sont corrects");
        }
    }
    
    /**
     * APPROCHE 2 : Création de table avec colonne XML
     * Stockage du XML complet dans une colonne de type XML
     */
    public static void creerTablesAvecXML() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  CRÉATION TABLE AVEC COLONNE XML");
        System.out.println("═══════════════════════════════════════\n");
        
        String[] sqlStatements = {
            // Supprimer table existante
            "DROP TABLE IF EXISTS CV_XML",
            
            // Créer table avec colonne XML
            """
            CREATE TABLE CV_XML (
                id INT PRIMARY KEY IDENTITY(1,1),
                code VARCHAR(20) UNIQUE NOT NULL,
                xml_content XML NOT NULL,
                dateCreation DATETIME DEFAULT GETDATE()
            )
            """,
            
            // Créer index sur le code
            "CREATE INDEX idx_cv_code ON CV_XML(code)",
            
            // Créer index XML pour requêtes XQuery
            """
            CREATE PRIMARY XML INDEX idx_xml_primary 
            ON CV_XML(xml_content)
            """
        };
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            
            System.out.println("✓ Table CV_XML créée avec succès !");
            System.out.println("  - Colonne XML pour stockage complet");
            System.out.println("  - Index XML pour requêtes XQuery optimisées");
            System.out.println("\nAvantages de cette approche :");
            System.out.println("  ✓ Préserve la structure XML originale");
            System.out.println("  ✓ Requêtes XQuery possibles");
            System.out.println("  ✓ Pas de perte d'information\n");
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la création de la table XML :");
            System.err.println("  " + e.getMessage());
        }
    }
    
    /**
     * APPROCHE 1 : Importer les CV avec décomposition relationnelle
     */
    public static void importerCVRelationnels(String xmlFile) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  IMPORT CV (APPROCHE RELATIONNELLE)");
        System.out.println("═══════════════════════════════════════\n");
        
        try {
            // Parser le fichier XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFile));
            document.getDocumentElement().normalize();
            
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Transaction
            
            NodeList cvList = document.getElementsByTagName("cv");
            int nbCVImportes = 0;
            
            for (int i = 0; i < cvList.getLength(); i++) {
                Element cv = (Element) cvList.item(i);
                String code = cv.getAttribute("code");
                
                System.out.println("Import du CV : " + code);
                
                // 1. Insérer le CV principal
                int cvId = insererCVPrincipal(conn, cv);
                
                if (cvId > 0) {
                    // 2. Insérer les postes
                    insererPostes(conn, cvId, cv);
                    
                    // 3. Insérer les diplômes
                    insererDiplomes(conn, cvId, cv);
                    
                    // 4. Insérer les stages
                    insererStages(conn, cvId, cv);
                    
                    // 5. Insérer les compétences
                    insererCompetences(conn, cvId, cv);
                    
                    // 6. Insérer les langues
                    insererLangues(conn, cvId, cv);
                    
                    // 7. Insérer les loisirs
                    insererLoisirs(conn, cvId, cv);
                    
                    nbCVImportes++;
                    System.out.println("  ✓ CV importé avec succès\n");
                }
            }
            
            conn.commit();
            conn.close();
            
            System.out.println("═══════════════════════════════════════");
            System.out.println("✓ Import terminé !");
            System.out.println("  Nombre de CV importés : " + nbCVImportes);
            System.out.println("═══════════════════════════════════════\n");
            
        } catch (Exception e) {
            System.err.println("✗ Erreur lors de l'import :");
            e.printStackTrace();
        }
    }
    
    private static int insererCVPrincipal(Connection conn, Element cv) throws SQLException {
        String sql = """
            INSERT INTO CV (code, nom, prenom, dateNaissance, lieuNaissance, 
                          email, image, niveau, typeNiveau, salaire, devise, periodeSalaire)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        Element entete = (Element) cv.getElementsByTagName("entete").item(0);
        Element niveau = (Element) cv.getElementsByTagName("niveau").item(0);
        Element salaire = (Element) cv.getElementsByTagName("salairedemande").item(0);
        
        ps.setString(1, cv.getAttribute("code"));
        ps.setString(2, getTextContent(entete, "nom"));
        ps.setString(3, getTextContent(entete, "prenom"));
        ps.setString(4, getTextContent(entete, "dateNaissance"));
        ps.setString(5, getTextContent(entete, "lieuNaissance"));
        ps.setString(6, getTextContent(entete, "email"));
        ps.setString(7, getTextContent(entete, "image"));
        ps.setString(8, niveau.getTextContent());
        ps.setString(9, niveau.getAttribute("type"));
        
        try {
            ps.setDouble(10, Double.parseDouble(salaire.getTextContent()));
        } catch (NumberFormatException e) {
            ps.setDouble(10, 0.0);
        }
        
        ps.setString(11, salaire.getAttribute("devise"));
        ps.setString(12, salaire.getAttribute("periode"));
        
        ps.executeUpdate();
        
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }
    
    private static void insererPostes(Connection conn, int cvId, Element cv) throws SQLException {
        String sql = "INSERT INTO Poste (cv_id, intitule, entreprise, dateDebut, dateFin) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        NodeList postes = cv.getElementsByTagName("poste");
        for (int i = 0; i < postes.getLength(); i++) {
            Element poste = (Element) postes.item(i);
            ps.setInt(1, cvId);
            ps.setString(2, poste.getTextContent());
            ps.setString(3, poste.getAttribute("entreprise"));
            ps.setString(4, poste.getAttribute("dateDebut"));
            ps.setString(5, poste.getAttribute("dateFin"));
            ps.executeUpdate();
        }
    }
    
    private static void insererDiplomes(Connection conn, int cvId, Element cv) throws SQLException {
        String sql = "INSERT INTO Diplome (cv_id, intitule, annee, etablissement, specialite) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        NodeList diplomes = cv.getElementsByTagName("diplome");
        for (int i = 0; i < diplomes.getLength(); i++) {
            Element diplome = (Element) diplomes.item(i);
            ps.setInt(1, cvId);
            ps.setString(2, diplome.getTextContent());
            ps.setString(3, diplome.getAttribute("annee"));
            ps.setString(4, diplome.getAttribute("etablissement"));
            ps.setString(5, diplome.getAttribute("specialite"));
            ps.executeUpdate();
        }
    }
    
    private static void insererStages(Connection conn, int cvId, Element cv) throws SQLException {
        String sql = "INSERT INTO Stage (cv_id, intitule, entreprise, dateDebut, dateFin, description) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        NodeList stages = cv.getElementsByTagName("stage");
        for (int i = 0; i < stages.getLength(); i++) {
            Element stage = (Element) stages.item(i);
            if (!stage.getTextContent().trim().isEmpty()) {
                ps.setInt(1, cvId);
                ps.setString(2, stage.getTextContent());
                ps.setString(3, stage.getAttribute("entreprise"));
                ps.setString(4, stage.getAttribute("dateDebut"));
                ps.setString(5, stage.getAttribute("dateFin"));
                ps.setString(6, stage.getAttribute("description"));
                ps.executeUpdate();
            }
        }
    }
    
    private static void insererCompetences(Connection conn, int cvId, Element cv) throws SQLException {
        String sql = "INSERT INTO Competence (cv_id, nom, niveauMaitrise) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        NodeList competences = cv.getElementsByTagName("competence");
        for (int i = 0; i < competences.getLength(); i++) {
            Element competence = (Element) competences.item(i);
            ps.setInt(1, cvId);
            ps.setString(2, competence.getTextContent());
            ps.setString(3, competence.getAttribute("niveauMaitrise"));
            ps.executeUpdate();
        }
    }
    
    private static void insererLangues(Connection conn, int cvId, Element cv) throws SQLException {
        String sql = "INSERT INTO Langue (cv_id, nom, niveau) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        NodeList langues = cv.getElementsByTagName("langue");
        for (int i = 0; i < langues.getLength(); i++) {
            Element langue = (Element) langues.item(i);
            ps.setInt(1, cvId);
            ps.setString(2, langue.getTextContent());
            ps.setString(3, langue.getAttribute("niveau"));
            ps.executeUpdate();
        }
    }
    
    private static void insererLoisirs(Connection conn, int cvId, Element cv) throws SQLException {
        String sql = "INSERT INTO Loisir (cv_id, nom) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        NodeList loisirs = cv.getElementsByTagName("loisir");
        for (int i = 0; i < loisirs.getLength(); i++) {
            Element loisir = (Element) loisirs.item(i);
            ps.setInt(1, cvId);
            ps.setString(2, loisir.getTextContent());
            ps.executeUpdate();
        }
    }
    
    /**
     * APPROCHE 2 : Importer les CV avec stockage XML complet
     */
    public static void importerCVAvecXML(String xmlFile) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  IMPORT CV (STOCKAGE XML COMPLET)");
        System.out.println("═══════════════════════════════════════\n");
        
        try {
            // Parser le fichier XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFile));
            document.getDocumentElement().normalize();
            
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "INSERT INTO CV_XML (code, xml_content) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            NodeList cvList = document.getElementsByTagName("cv");
            int nbCVImportes = 0;
            
            for (int i = 0; i < cvList.getLength(); i++) {
                Element cv = (Element) cvList.item(i);
                String code = cv.getAttribute("code");
                
                // Convertir l'élément CV en String XML
                String cvXml = elementToString(cv);
                
                ps.setString(1, code);
                ps.setString(2, cvXml);
                ps.executeUpdate();
                
                nbCVImportes++;
                System.out.println("✓ CV importé : " + code);
            }
            
            ps.close();
            conn.close();
            
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("✓ Import terminé !");
            System.out.println("  Nombre de CV importés : " + nbCVImportes);
            System.out.println("═══════════════════════════════════════\n");
            
        } catch (Exception e) {
            System.err.println("✗ Erreur lors de l'import :");
            e.printStackTrace();
        }
    }
    
    /**
     * Rechercher des CV par compétence
     */
    public static void rechercherCVParCompetence() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEntrez la compétence recherchée : ");
        String competence = scanner.nextLine();
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  RECHERCHE : " + competence);
        System.out.println("═══════════════════════════════════════\n");
        
        String sql = """
            SELECT DISTINCT c.code, c.nom, c.prenom, c.email, c.niveau
            FROM CV c
            INNER JOIN Competence comp ON c.id = comp.cv_id
            WHERE comp.nom LIKE ?
            ORDER BY c.nom
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + competence + "%");
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("CV #" + count);
                System.out.println("  Code    : " + rs.getString("code"));
                System.out.println("  Nom     : " + rs.getString("nom") + " " + rs.getString("prenom"));
                System.out.println("  Email   : " + rs.getString("email"));
                System.out.println("  Niveau  : " + rs.getString("niveau"));
                System.out.println();
            }
            
            if (count == 0) {
                System.out.println("Aucun CV trouvé avec cette compétence.\n");
            } else {
                System.out.println("Total : " + count + " CV trouvé(s)\n");
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la recherche :");
            System.err.println("  " + e.getMessage());
        }
    }
    
    /**
     * Afficher tous les CV
     */
    public static void afficherTousLesCV() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  LISTE DE TOUS LES CV");
        System.out.println("═══════════════════════════════════════\n");
        
        String sql = "SELECT code, nom, prenom, email, niveau, salaire, devise FROM CV ORDER BY code";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("┌─ CV #" + count + " ─────────────────────────");
                System.out.println("│ Code    : " + rs.getString("code"));
                System.out.println("│ Nom     : " + rs.getString("nom") + " " + rs.getString("prenom"));
                System.out.println("│ Email   : " + rs.getString("email"));
                System.out.println("│ Niveau  : " + rs.getString("niveau"));
                System.out.println("│ Salaire : " + rs.getDouble("salaire") + " " + rs.getString("devise"));
                System.out.println("└────────────────────────────────────────\n");
            }
            
            System.out.println("Total : " + count + " CV dans la base\n");
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'affichage :");
            System.err.println("  " + e.getMessage());
        }
    }
    
    /**
     * Utilitaires
     */
    private static String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
    
    private static String elementToString(Element element) {
        try {
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(element), 
                                new javax.xml.transform.stream.StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}