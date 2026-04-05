package catalogue;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Reprsente un livre dans le catalogue de la bibliothque. Contient les
 * informations bibliographiques et valide les donnes  la cration.
 */
public class Livre {

    /**
     * Format ISBN-13 attendu : 978- suivi de 10 chiffres
     */
    private static final Pattern ISBN_PATTERN = Pattern.compile("^978-\\d{10}$");

    private final int id;
    private String titre;
    private String auteur;
    private String isbn;
    private int annee;
    private String genre;

    /**
     * Compteur statique pour gnrer des ID uniques
     */
    private static int compteurId = 1;

    /**
     * Construit un livre avec validation de toutes les donnes.
     *
     * @param titre titre du livre (non vide)
     * @param auteur auteur du livre (non vide)
     * @param isbn code ISBN au format 978-XXXXXXXXXX
     * @param annee anne de publication (suprieure  1800)
     * @param genre genre littraire du livre
     * @throws IllegalArgumentException si une donne est invalide
     */
    public Livre(String titre, String auteur, String isbn, int annee, String genre) {
        validerTitre(titre);
        validerAuteur(auteur);
        validerIsbn(isbn);
        validerAnnee(annee);

        this.id = compteurId++;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.annee = annee;
        this.genre = (genre != null && !genre.isBlank()) ? genre : "Non class";
    }

    /**
     * Constructeur avec ID explicite (utilis lors de la reconstruction depuis
     * la BDD).
     */
    public Livre(int id, String titre, String auteur, String isbn, int annee, String genre) {
        validerTitre(titre);
        validerAuteur(auteur);
        validerIsbn(isbn);
        validerAnnee(annee);

        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.annee = annee;
        this.genre = (genre != null && !genre.isBlank()) ? genre : "Non class";
    }

    //  Validation
    private static void validerTitre(String titre) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre ne peut pas tre vide.");
        }
    }

    private static void validerAuteur(String auteur) {
        if (auteur == null || auteur.isBlank()) {
            throw new IllegalArgumentException("L'auteur ne peut pas tre vide.");
        }
    }

    /**
     *
     * @param isbn code ISBN  valider
     * @throws IllegalArgumentException si le format est incorrect
     */
    public static void validerIsbn(String isbn) {
        if (isbn == null || !ISBN_PATTERN.matcher(isbn).matches()) {
            throw new IllegalArgumentException(
                    "ISBN invalide : '" + isbn + "'. Format attendu : 978-XXXXXXXXXX (10 chiffres aprs 978-)."
            );
        }
    }

    private static void validerAnnee(int annee) {
        int anneeActuelle = java.time.Year.now().getValue();
        if (annee <= 1800 || annee > anneeActuelle) {
            throw new IllegalArgumentException(
                    "Anne invalide : " + annee + ". Doit tre comprise entre 1801 et " + anneeActuelle + "."
            );
        }
    }

    // Getters 
    /**
     * @return identifiant unique du livre
     */
    public int getId() {
        return id;
    }

    /**
     * @return titre du livre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * @return auteur du livre
     */
    public String getAuteur() {
        return auteur;
    }

    /**
     * @return code ISBN du livre
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @return anne de publication
     */
    public int getAnnee() {
        return annee;
    }

    /**
     * @return genre littraire
     */
    public String getGenre() {
        return genre;
    }

    // Setters avec validation 
    /**
     * Modifie le titre du livre.
     *
     * @param titre nouveau titre (non vide)
     * @throws IllegalArgumentException si le titre est vide
     */
    public void setTitre(String titre) {
        validerTitre(titre);
        this.titre = titre;
    }

    /**
     * Modifie l'auteur du livre.
     *
     * @param auteur nouvel auteur (non vide)
     * @throws IllegalArgumentException si l'auteur est vide
     */
    public void setAuteur(String auteur) {
        validerAuteur(auteur);
        this.auteur = auteur;
    }

    /**
     * Modifie l'ISBN du livre.
     *
     * @param isbn nouvel ISBN au format 978-XXXXXXXXXX
     * @throws IllegalArgumentException si le format est invalide
     */
    public void setIsbn(String isbn) {
        validerIsbn(isbn);
        this.isbn = isbn;
    }

    /**
     * Modifie l'anne de publication.
     *
     * @param annee nouvelle anne (> 1800)
     * @throws IllegalArgumentException si l'anne est invalide
     */
    public void setAnnee(int annee) {
        validerAnnee(annee);
        this.annee = annee;
    }

    /**
     * @param genre nouveau genre littraire
     */
    public void setGenre(String genre) {
        this.genre = (genre != null && !genre.isBlank()) ? genre : "Non class";
    }

    // Equals / HashCode / ToString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Livre)) {
            return false;
        }
        Livre autre = (Livre) obj;
        return Objects.equals(isbn, autre.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Livre{id=%d, titre='%s', auteur='%s', isbn='%s', annee=%d, genre='%s'}",
                id, titre, auteur, isbn, annee, genre);
    }
}
