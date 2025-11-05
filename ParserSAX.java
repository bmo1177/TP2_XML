import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class ParserSAX {
    
    public static void main(String[] args) {
        try {
            File xmlFile = new File("Cv.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true); // Validation avec DTD
            
            SAXParser saxParser = factory.newSAXParser();
            CVHandler handler = new CVHandler();
            
            System.out.println("=== LECTURE DES CV AVEC SAX ===\n");
            
            saxParser.parse(xmlFile, handler);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class CVHandler extends DefaultHandler {
    
    private StringBuilder currentValue = new StringBuilder();
    private int cvCount = 0;
    private String currentElement = "";
    private Attributes currentAttributes;
    
    @Override
    public void startDocument() throws SAXException {
        System.out.println("Début du parsing du document XML...\n");
    }
    
    @Override
    public void endDocument() throws SAXException {
        System.out.println("\n========================================");
        System.out.println("Fin du parsing - Total CV traités: " + cvCount);
        System.out.println("========================================");
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, 
                           Attributes attributes) throws SAXException {
        currentElement = qName;
        currentAttributes = attributes;
        currentValue.setLength(0); // Réinitialiser le contenu
        
        if (qName.equals("cv")) {
            cvCount++;
            String code = attributes.getValue("code");
            System.out.println("========================================");
            System.out.println("CV N° " + cvCount + " - Code: " + code);
            System.out.println("========================================");
        }
        
        if (qName.equals("entete")) {
            System.out.println("\n--- ENTÊTE ---");
        } else if (qName.equals("postesOccupes")) {
            System.out.println("\n--- POSTES OCCUPÉS ---");
        } else if (qName.equals("diplomes")) {
            System.out.println("\n--- DIPLÔMES ---");
        } else if (qName.equals("stages")) {
            System.out.println("\n--- STAGES ---");
        } else if (qName.equals("competencesTechniques")) {
            System.out.println("\n--- COMPÉTENCES TECHNIQUES ---");
        } else if (qName.equals("langues")) {
            System.out.println("\n--- LANGUES ---");
        } else if (qName.equals("loisirs")) {
            System.out.println("\n--- LOISIRS ---");
        } else if (qName.equals("niveau")) {
            System.out.println("\n--- NIVEAU GLOBAL ---");
        } else if (qName.equals("salairedemande")) {
            System.out.println("\n--- SALAIRE DEMANDÉ ---");
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentValue.append(ch, start, length);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String content = currentValue.toString().trim();
        
        switch (qName) {
            // Entête
            case "nom":
                System.out.println("Nom: " + content);
                break;
            case "prenom":
                System.out.println("Prénom: " + content);
                break;
            case "dateNaissance":
                System.out.println("Date de naissance: " + content);
                break;
            case "lieuNaissance":
                System.out.println("Lieu de naissance: " + content);
                break;
            case "email":
                System.out.println("Email: " + content);
                break;
            case "image":
                System.out.println("Image: " + content);
                break;
                
            // Postes occupés
            case "poste":
                System.out.println("  • " + content);
                if (currentAttributes != null) {
                    String entreprise = currentAttributes.getValue("entreprise");
                    String dateDebut = currentAttributes.getValue("dateDebut");
                    String dateFin = currentAttributes.getValue("dateFin");
                    if (entreprise != null) {
                        System.out.println("    Entreprise: " + entreprise);
                        System.out.println("    Période: " + dateDebut + " - " + dateFin);
                    }
                }
                break;
                
            // Diplômes
            case "diplome":
                System.out.println("  • " + content);
                if (currentAttributes != null) {
                    String annee = currentAttributes.getValue("annee");
                    String etablissement = currentAttributes.getValue("etablissement");
                    String specialite = currentAttributes.getValue("specialite");
                    if (annee != null) {
                        System.out.println("    Année: " + annee);
                        System.out.println("    Établissement: " + etablissement);
                        System.out.println("    Spécialité: " + specialite);
                    }
                }
                break;
                
            // Stages
            case "stage":
                if (!content.isEmpty()) {
                    System.out.println("  • " + content);
                    if (currentAttributes != null) {
                        String entreprise = currentAttributes.getValue("entreprise");
                        String dateDebut = currentAttributes.getValue("dateDebut");
                        String dateFin = currentAttributes.getValue("dateFin");
                        String description = currentAttributes.getValue("description");
                        if (entreprise != null) {
                            System.out.println("    Entreprise: " + entreprise);
                            System.out.println("    Période: " + dateDebut + " - " + dateFin);
                            System.out.println("    Description: " + description);
                        }
                    }
                }
                break;
            
            case "stages":
                if (content.isEmpty()) {
                    System.out.println("  Aucun stage");
                }
                break;
                
            // Compétences techniques
            case "competence":
                String niveauComp = "";
                if (currentAttributes != null && currentAttributes.getValue("niveauMaitrise") != null) {
                    niveauComp = " (" + currentAttributes.getValue("niveauMaitrise") + ")";
                }
                System.out.println("  • " + content + niveauComp);
                break;
                
            // Langues
            case "langue":
                String niveauLang = "";
                if (currentAttributes != null && currentAttributes.getValue("niveau") != null) {
                    niveauLang = " - Niveau " + currentAttributes.getValue("niveau");
                }
                System.out.println("  • " + content + niveauLang);
                break;
                
            // Loisirs
            case "loisir":
                System.out.println("  • " + content);
                break;
                
            // Niveau
            case "niveau":
                String typeNiveau = "";
                if (currentAttributes != null && currentAttributes.getValue("type") != null) {
                    typeNiveau = " (" + currentAttributes.getValue("type") + ")";
                }
                System.out.println("Niveau: " + content + typeNiveau);
                break;
                
            // Salaire demandé
            case "salairedemande":
                String devise = "";
                String periode = "";
                if (currentAttributes != null) {
                    if (currentAttributes.getValue("devise") != null) {
                        devise = currentAttributes.getValue("devise");
                    }
                    if (currentAttributes.getValue("periode") != null) {
                        periode = " (" + currentAttributes.getValue("periode") + ")";
                    }
                }
                System.out.println("Salaire: " + content + " " + devise + periode);
                break;
                
            case "cv":
                System.out.println("\n");
                break;
        }
    }
    
    @Override
    public void error(SAXParseException e) throws SAXException {
        System.err.println("Erreur: " + e.getMessage());
    }
    
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        System.err.println("Erreur fatale: " + e.getMessage());
        throw e;
    }
    
    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.err.println("Avertissement: " + e.getMessage());
    }
}