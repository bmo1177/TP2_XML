import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.query.*;
import java.io.*;
import java.util.Scanner;

/**
 * Classe pour gérer le stockage des CV XML dans BaseX (SGBD XML natif)
 * Conforme au TP2 - Question 6
 * 
 * INSTALLATION BaseX:
 * 1. Télécharger depuis https://basex.org/download/
 * 2. Ajouter la dépendance Maven:
 *    <dependency>
 *      <groupId>org.basex</groupId>
 *      <artifactId>basex</artifactId>
 *      <version>10.7</version>
 *    </dependency>
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class StockageBaseX {
    
    private static final String DB_NAME = "CVTHEQUE";
    private static final String XML_FILE = "Cv.xml";
    
    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     STOCKAGE CV XML EN BASE DE DONNÉES BASEX          ║");
        System.out.println("║              (SGBD XML NATIF)                          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        while (true) {
            afficherMenu();
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            switch (choix) {
                case 1:
                    creerBase();
                    break;
                case 2:
                    afficherTousLesCV();
                    break;
                case 3:
                    rechercherParCompetence(scanner);
                    break;
                case 4:
                    rechercherParNiveau(scanner);
                    break;
                case 5:
                    rechercherParSalaire(scanner);
                    break;
                case 6:
                    statistiquesCompetences();
                    break;
                case 7:
                    afficherInfoBase();
                    break;
                case 8:
                    exemples XQuery();
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
        System.out.println("│           MENU STOCKAGE BASEX                   │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│ GESTION BASE :                                  │");
        System.out.println("│   1. Créer/Importer base de données            │");
        System.out.println("│   7. Informations sur la base                  │");
        System.out.println("│                                                 │");
        System.out.println("│ REQUÊTES XQUERY :                               │");
        System.out.println("│   2. Afficher tous les CV                      │");
        System.out.println("│   3. Rechercher par compétence                 │");
        System.out.println("│   4. Rechercher par niveau                     │");
        System.out.println("│   5. Rechercher par salaire                    │");
        System.out.println("│   6. Statistiques des compétences              │");
        System.out.println("│                                                 │");
        System.out.println("│ DOCUMENTATION :                                 │");
        System.out.println("│   8. Exemples de requêtes XQuery               │");
        System.out.println("│                                                 │");
        System.out.println("│   0. Quitter                                    │");
        System.out.println("└─────────────────────────────────────────────────┘");
    }
    
    /**
     * Créer la base de données et importer le fichier XML
     */
    public static void creerBase() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  CRÉATION BASE DE DONNÉES BASEX");
        System.out.println("═══════════════════════════════════════\n");
        
        try {
            // Créer un contexte BaseX
            Context context = new Context();
            
            System.out.println("Vérification du fichier XML...");
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                System.err.println("✗ Fichier " + XML_FILE + " introuvable !");
                return;
            }
            
            System.out.println("✓ Fichier trouvé : " + XML_FILE);
            System.out.println("\nCréation de la base '" + DB_NAME + "'...");
            
            // Supprimer la base si elle existe déjà
            new DropDB(DB_NAME).execute(context);
            
            // Créer la nouvelle base avec le fichier XML
            new CreateDB(DB_NAME, XML_FILE).execute(context);
            
            // Créer des index pour optimiser les recherches
            new Open(DB_NAME).execute(context);
            new CreateIndex("text").execute(context);
            new CreateIndex("attribute").execute(context);
            
            // Obtenir des statistiques
            String infoQuery = "count(//cv)";
            try (QueryProcessor proc = new QueryProcessor(infoQuery, context)) {
                String nbCV = proc.value().toString();
                
                System.out.println("\n✓ Base de données créée avec succès !");
                System.out.println("  Nom de la base : " + DB_NAME);
                System.out.println("  Nombre de CV   : " + nbCV);
                System.out.println("  Index créés    : Text, Attribute");
                System.out.println("\nAvantages BaseX :");
                System.out.println("  ✓ Requêtes XQuery optimisées");
                System.out.println("  ✓ Validation DTD/XSD intégrée");
                System.out.println("  ✓ Performance excellente");
                System.out.println("  ✓ Support REST API\n");
            }
            
            context.close();
            
        } catch (Exception e) {
            System.err.println("\n✗ Erreur lors de la création de la base :");
            System.err.println("  " + e.getMessage());
            System.err.println("\nAssurez-vous que :");
            System.err.println("  - BaseX est bien installé");
            System.err.println("  - La dépendance Maven est ajoutée");
            System.err.println("  - Le fichier " + XML_FILE + " existe");
        }
    }
    
    /**
     * Afficher tous les CV
     */
    public static void afficherTousLesCV() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  LISTE DE TOUS LES CV");
        System.out.println("═══════════════════════════════════════\n");
        
        String xquery = """
            for $cv in //cv
            return
              <resultat>
                <code>{data($cv/@code)}</code>
                <nom>{$cv/entete/nom/text()}</nom>
                <prenom>{$cv/entete/prenom/text()}</prenom>
                <email>{$cv/entete/email/text()}</email>
                <niveau>{$cv/niveau/text()}</niveau>
                <salaire>{$cv/salairedemande/text()} {data($cv/salairedemande/@devise)}</salaire>
              </resultat>
            """;
        
        executerRequete(xquery, "Liste des CV");
    }
    
    /**
     * Rechercher des CV par compétence
     */
    public static void rechercherParCompetence(Scanner scanner) {
        System.out.print("\nEntrez la compétence recherchée : ");
        String competence = scanner.nextLine();
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  RECHERCHE PAR COMPÉTENCE : " + competence);
        System.out.println("═══════════════════════════════════════\n");
        
        String xquery = String.format("""
            for $cv in //cv[.//competence[contains(text(), '%s')]]
            let $comp := $cv//competence[contains(text(), '%s')]
            return
              <resultat>
                <code>{data($cv/@code)}</code>
                <nom>{$cv/entete/nom/text()} {$cv/entete/prenom/text()}</nom>
                <competence>{$comp/text()}</competence>
                <niveau_competence>{data($comp/@niveauMaitrise)}</niveau_competence>
                <email>{$cv/entete/email/text()}</email>
              </resultat>
            """, competence, competence);
        
        executerRequete(xquery, "Résultats de recherche");
    }
    
    /**
     * Rechercher des CV par niveau
     */
    public static void rechercherParNiveau(Scanner scanner) {
        System.out.println("\nNiveaux disponibles : Débutant, Intermédiaire, Expert");
        System.out.print("Entrez le niveau recherché : ");
        String niveau = scanner.nextLine();
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  RECHERCHE PAR NIVEAU : " + niveau);
        System.out.println("═══════════════════════════════════════\n");
        
        String xquery = String.format("""
            for $cv in //cv[niveau/@type='%s']
            return
              <resultat>
                <code>{data($cv/@code)}</code>
                <nom>{$cv/entete/nom/text()} {$cv/entete/prenom/text()}</nom>
                <email>{$cv/entete/email/text()}</email>
                <niveau>{$cv/niveau/text()}</niveau>
                <nb_competences>{count($cv//competence)}</nb_competences>
              </resultat>
            """, niveau);
        
        executerRequete(xquery, "Résultats de recherche");
    }
    
    /**
     * Rechercher des CV par salaire minimum
     */
    public static void rechercherParSalaire(Scanner scanner) {
        System.out.print("\nEntrez le salaire minimum : ");
        double salaireMin = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  RECHERCHE PAR SALAIRE ≥ " + salaireMin);
        System.out.println("═══════════════════════════════════════\n");
        
        String xquery = String.format("""
            for $cv in //cv[number(salairedemande) >= %f]
            order by number($cv/salairedemande) descending
            return
              <resultat>
                <code>{data($cv/@code)}</code>
                <nom>{$cv/entete/nom/text()} {$cv/entete/prenom/text()}</nom>
                <salaire>{$cv/salairedemande/text()} {data($cv/salairedemande/@devise)}</salaire>
                <periode>{data($cv/salairedemande/@periode)}</periode>
                <niveau>{$cv/niveau/text()}</niveau>
              </resultat>
            """, salaireMin);
        
        executerRequete(xquery, "Résultats de recherche");
    }
    
    /**
     * Statistiques sur les compétences
     */
    public static void statistiquesCompetences() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  STATISTIQUES DES COMPÉTENCES");
        System.out.println("═══════════════════════════════════════\n");
        
        String xquery = """
            for $comp in distinct-values(//competence/text())
            let $count := count(//competence[text() = $comp])
            order by $count descending
            return
              <resultat>
                <competence>{$comp}</competence>
                <occurrences>{$count}</occurrences>
                <pourcentage>{round(($count div count(//cv)) * 100)}%</pourcentage>
              </resultat>
            """;
        
        executerRequete(xquery, "Statistiques");
    }
    
    /**
     * Afficher les informations sur la base
     */
    public static void afficherInfoBase() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  INFORMATIONS SUR LA BASE");
        System.out.println("═══════════════════════════════════════\n");
        
        try {
            Context context = new Context();
            new Open(DB_NAME).execute(context);
            
            // Nombre total de CV
            String nbCVQuery = "count(//cv)";
            QueryProcessor proc1 = new QueryProcessor(nbCVQuery, context);
            String nbCV = proc1.value().toString();
            proc1.close();
            
            // Nombre de compétences uniques
            String nbCompQuery = "count(distinct-values(//competence/text()))";
            QueryProcessor proc2 = new QueryProcessor(nbCompQuery, context);
            String nbComp = proc2.value().toString();
            proc2.close();
            
            // Répartition par niveau
            String niveauxQuery = """
                for $niveau in distinct-values(//niveau/@type)
                let $count := count(//cv[niveau/@type = $niveau])
                return concat($niveau, ': ', $count)
                """;
            QueryProcessor proc3 = new QueryProcessor(niveauxQuery, context);
            
            System.out.println("📊 STATISTIQUES GÉNÉRALES");
            System.out.println("  Base de données    : " + DB_NAME);
            System.out.println("  Nombre de CV       : " + nbCV);
            System.out.println("  Compétences uniques: " + nbComp);
            System.out.println("\n📈 RÉPARTITION PAR NIVEAU");
            
            String result = proc3.execute();
            for (String line : result.split("\n")) {
                if (!line.trim().isEmpty()) {
                    System.out.println("  " + line.trim());
                }
            }
            proc3.close();
            
            // Taille de la base
            String infoCmd = new InfoDB().execute(context);
            System.out.println("\n💾 INFORMATIONS TECHNIQUES");
            System.out.println(infoCmd);
            
            context.close();
            
        } catch (Exception e) {
            System.err.println("✗ Erreur : " + e.getMessage());
            System.err.println("  La base n'existe peut-être pas. Créez-la d'abord (option 1).");
        }
    }
    
    /**
     * Afficher des exemples de requêtes XQuery
     */
    public static void exemplesXQuery() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         EXEMPLES DE REQUÊTES XQUERY                   ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        System.out.println("1️⃣ Récupérer tous les noms et prénoms :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("for $cv in //cv");
        System.out.println("return concat($cv/entete/nom, ' ', $cv/entete/prenom)");
        System.out.println();
        
        System.out.println("2️⃣ CV avec compétence Java niveau Expert :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("//cv[.//competence[text()='Java' and @niveauMaitrise='Expert']]");
        System.out.println();
        
        System.out.println("3️⃣ Compter les CV par niveau :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("for $niveau in distinct-values(//niveau/@type)");
        System.out.println("return <niveau type=\"{$niveau}\">");
        System.out.println("  {count(//cv[niveau/@type = $niveau])}");
        System.out.println("</niveau>");
        System.out.println();
        
        System.out.println("4️⃣ CV avec salaire > 100000 DZD :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("for $cv in //cv[number(salairedemande) > 100000]");
        System.out.println("order by number($cv/salairedemande) descending");
        System.out.println("return $cv");
        System.out.println();
        
        System.out.println("5️⃣ Liste des compétences par CV :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("for $cv in //cv");
        System.out.println("return <cv code=\"{$cv/@code}\">");
        System.out.println("  {$cv//competence}");
        System.out.println("</cv>");
        System.out.println();
        
        System.out.println("6️⃣ CV parlant Anglais niveau B2 ou supérieur :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("//cv[.//langue[text()='Anglais' and ");
        System.out.println("     (@niveau='B2' or @niveau='C1' or @niveau='C2')]]");
        System.out.println();
        
        System.out.println("7️⃣ Moyenne des salaires demandés :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("avg(//salairedemande/number(.))");
        System.out.println();
        
        System.out.println("8️⃣ CV avec plus de 5 compétences :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("for $cv in //cv");
        System.out.println("where count($cv//competence) > 5");
        System.out.println("return $cv");
        System.out.println();
        
        System.out.println("9️⃣ Recherche full-text (avec index) :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("//cv[. contains text 'développeur' ftand 'Java']");
        System.out.println();
        
        System.out.println("🔟 Grouper par diplômes :");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("for $diplome in distinct-values(//diplome/text())");
        System.out.println("return <diplome nom=\"{$diplome}\">");
        System.out.println("  {//cv[.//diplome/text() = $diplome]/@code}");
        System.out.println("</diplome>");
        System.out.println();
    }
    
    /**
     * Exécuter une requête XQuery et afficher les résultats
     */
    private static void executerRequete(String xquery, String titre) {
        try {
            Context context = new Context();
            new Open(DB_NAME).execute(context);
            
            try (QueryProcessor proc = new QueryProcessor(xquery, context)) {
                String result = proc.execute();
                
                if (result.trim().isEmpty()) {
                    System.out.println("Aucun résultat trouvé.\n");
                } else {
                    // Afficher les résultats de manière formatée
                    System.out.println(result);
                    System.out.println();
                }
            }
            
            context.close();
            
        } catch (BaseXException e) {
            System.err.println("✗ Erreur lors de l'exécution de la requête :");
            System.err.println("  " + e.getMessage());
            
            if (e.getMessage().contains("Database") && e.getMessage().contains("not found")) {
                System.err.println("\n  La base n'existe pas. Créez-la d'abord (option 1).");
            }
        } catch (Exception e) {
            System.err.println("✗ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}