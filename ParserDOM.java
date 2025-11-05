import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.io.File;

public class ParserDOM {
    
    public static void main(String[] args) {
        try {
            // Création du parser DOM
            File xmlFile = new File("Cv.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true); // Validation avec DTD
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            
            // Normaliser le document
            document.getDocumentElement().normalize();
            
            System.out.println("=== LECTURE DES CV AVEC DOM ===\n");
            
            // Récupérer tous les CV
            NodeList cvList = document.getElementsByTagName("cv");
            
            System.out.println("Nombre de CV trouvés : " + cvList.getLength() + "\n");
            
            // Parcourir chaque CV
            for (int i = 0; i < cvList.getLength(); i++) {
                Element cvElement = (Element) cvList.item(i);
                String code = cvElement.getAttribute("code");
                
                System.out.println("========================================");
                System.out.println("CV N° " + (i + 1) + " - Code: " + code);
                System.out.println("========================================");
                
                // Entête
                afficherEntete(cvElement);
                
                // Postes occupés
                afficherPostesOccupes(cvElement);
                
                // Diplômes
                afficherDiplomes(cvElement);
                
                // Stages
                afficherStages(cvElement);
                
                // Compétences techniques
                afficherCompetences(cvElement);
                
                // Langues
                afficherLangues(cvElement);
                
                // Loisirs
                afficherLoisirs(cvElement);
                
                // Niveau
                afficherNiveau(cvElement);
                
                // Salaire demandé
                afficherSalaire(cvElement);
                
                System.out.println("\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void afficherEntete(Element cv) {
        Element entete = (Element) cv.getElementsByTagName("entete").item(0);
        
        System.out.println("\n--- ENTÊTE ---");
        System.out.println("Nom: " + getTextContent(entete, "nom"));
        System.out.println("Prénom: " + getTextContent(entete, "prenom"));
        System.out.println("Date de naissance: " + getTextContent(entete, "dateNaissance"));
        System.out.println("Lieu de naissance: " + getTextContent(entete, "lieuNaissance"));
        System.out.println("Email: " + getTextContent(entete, "email"));
        
        NodeList images = entete.getElementsByTagName("image");
        if (images.getLength() > 0) {
            System.out.println("Image: " + images.item(0).getTextContent());
        }
    }
    
    private static void afficherPostesOccupes(Element cv) {
        System.out.println("\n--- POSTES OCCUPÉS ---");
        Element postesOccupes = (Element) cv.getElementsByTagName("postesOccupes").item(0);
        NodeList postes = postesOccupes.getElementsByTagName("poste");
        
        for (int i = 0; i < postes.getLength(); i++) {
            Element poste = (Element) postes.item(i);
            System.out.println("  • " + poste.getTextContent());
            if (poste.hasAttribute("entreprise")) {
                System.out.println("    Entreprise: " + poste.getAttribute("entreprise"));
            }
            if (poste.hasAttribute("dateDebut")) {
                System.out.println("    Période: " + poste.getAttribute("dateDebut") + 
                                 " - " + poste.getAttribute("dateFin"));
            }
        }
    }
    
    private static void afficherDiplomes(Element cv) {
        System.out.println("\n--- DIPLÔMES ---");
        Element diplomes = (Element) cv.getElementsByTagName("diplomes").item(0);
        NodeList diplomeList = diplomes.getElementsByTagName("diplome");
        
        for (int i = 0; i < diplomeList.getLength(); i++) {
            Element diplome = (Element) diplomeList.item(i);
            System.out.println("  • " + diplome.getTextContent());
            if (diplome.hasAttribute("annee")) {
                System.out.println("    Année: " + diplome.getAttribute("annee"));
                System.out.println("    Établissement: " + diplome.getAttribute("etablissement"));
                System.out.println("    Spécialité: " + diplome.getAttribute("specialite"));
            }
        }
    }
    
    private static void afficherStages(Element cv) {
        System.out.println("\n--- STAGES ---");
        Element stages = (Element) cv.getElementsByTagName("stages").item(0);
        NodeList stageList = stages.getElementsByTagName("stage");
        
        if (stageList.getLength() == 0) {
            System.out.println("  Aucun stage");
            return;
        }
        
        for (int i = 0; i < stageList.getLength(); i++) {
            Element stage = (Element) stageList.item(i);
            System.out.println("  • " + stage.getTextContent());
            if (stage.hasAttribute("entreprise")) {
                System.out.println("    Entreprise: " + stage.getAttribute("entreprise"));
                System.out.println("    Période: " + stage.getAttribute("dateDebut") + 
                                 " - " + stage.getAttribute("dateFin"));
                System.out.println("    Description: " + stage.getAttribute("description"));
            }
        }
    }
    
    private static void afficherCompetences(Element cv) {
        System.out.println("\n--- COMPÉTENCES TECHNIQUES ---");
        Element competences = (Element) cv.getElementsByTagName("competencesTechniques").item(0);
        NodeList competenceList = competences.getElementsByTagName("competence");
        
        for (int i = 0; i < competenceList.getLength(); i++) {
            Element competence = (Element) competenceList.item(i);
            String niveau = competence.hasAttribute("niveauMaitrise") ? 
                          " (" + competence.getAttribute("niveauMaitrise") + ")" : "";
            System.out.println("  • " + competence.getTextContent() + niveau);
        }
    }
    
    private static void afficherLangues(Element cv) {
        System.out.println("\n--- LANGUES ---");
        Element langues = (Element) cv.getElementsByTagName("langues").item(0);
        NodeList langueList = langues.getElementsByTagName("langue");
        
        for (int i = 0; i < langueList.getLength(); i++) {
            Element langue = (Element) langueList.item(i);
            String niveau = langue.hasAttribute("niveau") ? 
                          " - Niveau " + langue.getAttribute("niveau") : "";
            System.out.println("  • " + langue.getTextContent() + niveau);
        }
    }
    
    private static void afficherLoisirs(Element cv) {
        System.out.println("\n--- LOISIRS ---");
        Element loisirs = (Element) cv.getElementsByTagName("loisirs").item(0);
        NodeList loisirList = loisirs.getElementsByTagName("loisir");
        
        for (int i = 0; i < loisirList.getLength(); i++) {
            System.out.println("  • " + loisirList.item(i).getTextContent());
        }
    }
    
    private static void afficherNiveau(Element cv) {
        System.out.println("\n--- NIVEAU GLOBAL ---");
        Element niveau = (Element) cv.getElementsByTagName("niveau").item(0);
        System.out.println("Niveau: " + niveau.getTextContent() + 
                         " (" + niveau.getAttribute("type") + ")");
    }
    
    private static void afficherSalaire(Element cv) {
        System.out.println("\n--- SALAIRE DEMANDÉ ---");
        Element salaire = (Element) cv.getElementsByTagName("salairedemande").item(0);
        String devise = salaire.hasAttribute("devise") ? salaire.getAttribute("devise") : "";
        String periode = salaire.hasAttribute("periode") ? salaire.getAttribute("periode") : "";
        System.out.println("Salaire: " + salaire.getTextContent() + " " + devise + 
                         " (" + periode + ")");
    }
    
    private static String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}