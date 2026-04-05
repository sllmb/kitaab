package catalogue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Catalogue {

    /**
     * Liste interne de tous les exemplaires enregistrs dans le catalogue
     */
    private final List<Exemplaire> exemplaires;
//crer un catalogue vide

    public Catalogue() {
        this.exemplaires = new ArrayList<>();
    }

    //  Ajout et suppression
    /**
     * Ajoute un exemplaire au catalogue.
     *
     * @param exemplaire l'exemplaire  ajouter (non null)
     * @throws IllegalArgumentException si l'exemplaire est null
     * @throws ExemplaireException si un exemplaire avec le mme ID existe dj
     */
    public void ajouterExemplaire(Exemplaire exemplaire) throws ExemplaireException {
        if (exemplaire == null) {
            throw new IllegalArgumentException("Impossible d'ajouter un exemplaire null au catalogue.");
        }
        boolean dejaPresent = exemplaires.stream()
                .anyMatch(e -> e.getId() == exemplaire.getId());
        if (dejaPresent) {
            throw new ExemplaireException(
                    "Un exemplaire avec l'ID " + exemplaire.getId() + " existe dj dans le catalogue."
            );
        }
        exemplaires.add(exemplaire);
        System.out.println(" Exemplaire ajout : " + exemplaire);
    }

    /**
     * Raccourci pratique : cre un exemplaire pour un livre et l'ajoute au
     * catalogue.
     *
     * @param livre le livre dont on ajoute un exemplaire
     * @return l'exemplaire cr et ajout
     * @throws ExemplaireException si l'ajout choue
     */
    public Exemplaire ajouterLivre(Livre livre) throws ExemplaireException {
        Exemplaire nouvelExemplaire = new Exemplaire(livre);
        ajouterExemplaire(nouvelExemplaire);
        return nouvelExemplaire;
    }

    /**
     * Retire un exemplaire du catalogue par son ID.
     *
     * @param idExemplaire identifiant de l'exemplaire  retirer
     * @throws ExemplaireException si aucun exemplaire ne correspond  cet ID
     */
    public void retirerExemplaire(int idExemplaire) throws ExemplaireException {
        Exemplaire cible = trouverExemplaireParId(idExemplaire);
        exemplaires.remove(cible);
        System.out.println(" Exemplaire #" + idExemplaire + " retir du catalogue.");
    }

    //Recherche
    /**
     * a recherche les exemplaires dont le titre du livre correspond
     *
     *
     * @param titre fragment de titre  rechercher
     * @return liste des exemplaires correspondants (vide si aucun rsultat)
     */
    public List<Exemplaire> rechercherParTitre(String titre) {
        if (titre == null || titre.isBlank()) {
            return Collections.emptyList();
        }
        String titreLower = titre.toLowerCase().trim();
        return exemplaires.stream()
                .filter(e -> e.getLivre().getTitre().toLowerCase().contains(titreLower))
                .collect(Collectors.toList());
    }

    /**
     * Recherche les exemplaires dont l'auteur du livre correspond (insensible 
     * la casse, recherche partielle).
     *
     * @param auteur fragment du nom d'auteur  rechercher
     * @return liste des exemplaires correspondants (vide si aucun rsultat)
     */
    public List<Exemplaire> rechercherParAuteur(String auteur) {
        if (auteur == null || auteur.isBlank()) {
            return Collections.emptyList();
        }
        String auteurLower = auteur.toLowerCase().trim();
        return exemplaires.stream()
                .filter(e -> e.getLivre().getAuteur().toLowerCase().contains(auteurLower))
                .collect(Collectors.toList());
    }

    /**
     * Recherche les exemplaires dont le livre correspond  l'ISBN exact.
     *
     * @param isbn code ISBN  rechercher (format 978-XXXXXXXXXX)
     * @return liste des exemplaires correspondants (plusieurs copies possibles)
     * @throws IllegalArgumentException si l'ISBN est invalide
     */
    public List<Exemplaire> rechercherParIsbn(String isbn) {
        Livre.validerIsbn(isbn); // lve IllegalArgumentException si invalide
        return exemplaires.stream()
                .filter(e -> e.getLivre().getIsbn().equals(isbn))
                .collect(Collectors.toList());
    }

    /**
     * Recherche un exemplaire prcis par son ID.
     *
     * @param id identifiant de l'exemplaire
     * @return l'exemplaire trouv
     * @throws ExemplaireException si aucun exemplaire ne correspond
     */
    public Exemplaire trouverExemplaireParId(int id) throws ExemplaireException {
        return exemplaires.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ExemplaireException(
                "Aucun exemplaire trouv avec l'ID : " + id
        ));
    }

    /**
     * Recherche un exemplaire disponible pour un titre donn.
     *
     * @param titre titre du livre recherch
     * @return un Optional contenant un exemplaire disponible, ou vide si aucun
     */
    public Optional<Exemplaire> trouverExemplaireDisponible(String titre) {
        return rechercherParTitre(titre).stream()
                .filter(Exemplaire::estDisponible)
                .findFirst();
    }

    /**
     * Retourne la liste de tous les exemplaires disponibles  l'emprunt.
     *
     * @return liste non modifiable des exemplaires disponibles
     */
    public List<Exemplaire> listerDisponibles() {
        return exemplaires.stream()
                .filter(Exemplaire::estDisponible)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retourne la liste de tous les exemplaires du catalogue.
     *
     * @return vue non modifiable de la liste complte
     */
    public List<Exemplaire> listerTous() {
        return Collections.unmodifiableList(exemplaires);
    }

    // Statistiques
    /**
     * Retourne le nombre total d'exemplaires dans le catalogue.
     *
     * @return nombre d'exemplaires
     */
    public int getNombreExemplaires() {
        return exemplaires.size();
    }

    /**
     * Retourne le nombre d'exemplaires disponibles.
     *
     * @return nombre d'exemplaires avec l'tat DISPONIBLE
     */
    public int getNombreDisponibles() {
        return (int) exemplaires.stream().filter(Exemplaire::estDisponible).count();
    }

    /**
     * Affiche un rsum du catalogue dans la console.
     */
    public void afficherResume() {
        System.out.println("=".repeat(50));
        System.out.println(" CATALOGUE  " + exemplaires.size() + " exemplaire(s) au total");
        System.out.println("    Disponibles : " + getNombreDisponibles());
        System.out.println("    Emprunts   : " + compterParEtat(EtatExemplaire.EMPRUNTE));
        System.out.println("     Abms      : " + compterParEtat(EtatExemplaire.ABIME));
        System.out.println("    Perdus      : " + compterParEtat(EtatExemplaire.PERDU));
        System.out.println("=".repeat(50));
    }

    private long compterParEtat(EtatExemplaire etat) {
        return exemplaires.stream().filter(e -> e.getEtat() == etat).count();
    }

    @Override
    public String toString() {
        return "Catalogue{" + exemplaires.size() + " exemplaire(s), " + getNombreDisponibles() + " disponible(s)}";
    }
}
