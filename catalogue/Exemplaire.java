package catalogue;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Reprsente un exemplaire physique d'un livre dans la bibliothque. Un mme
 * livre peut avoir plusieurs exemplaires avec des tats diffrents.
 */
public class Exemplaire {

    private final int id;
    private final Livre livre;
    private EtatExemplaire etat;
    private final LocalDate dateAjout;

    /**
     * Compteur statique pour gnrer des ID uniques
     */
    private static int compteurId = 1;

    /**
     * Cre un nouvel exemplaire d'un livre, disponible  la date d'aujourd'hui.
     *
     * @param livre le livre dont cet exemplaire est une copie
     * @throws IllegalArgumentException si le livre est null
     */
    public Exemplaire(Livre livre) {
        if (livre == null) {
            throw new IllegalArgumentException("Le livre associ  un exemplaire ne peut pas tre null.");
        }
        this.id = compteurId++;
        this.livre = livre;
        this.etat = EtatExemplaire.DISPONIBLE;
        this.dateAjout = LocalDate.now();
    }

    /**
     * Constructeur complet pour reconstruction depuis la base de donnes.
     *
     * @param id identifiant de l'exemplaire
     * @param livre livre associ
     * @param etat tat de l'exemplaire
     * @param dateAjout date d'ajout dans le systme
     */
    public Exemplaire(int id, Livre livre, EtatExemplaire etat, LocalDate dateAjout) {
        if (livre == null) {
            throw new IllegalArgumentException("Le livre ne peut pas tre null.");
        }
        if (etat == null) {
            throw new IllegalArgumentException("L'tat ne peut pas tre null.");
        }
        if (dateAjout == null) {
            throw new IllegalArgumentException("La date d'ajout ne peut pas tre null.");
        }

        this.id = id;
        this.livre = livre;
        this.etat = etat;
        this.dateAjout = dateAjout;
    }

    //  Mthodes mtier 
    /**
     * Indique si l'exemplaire peut tre emprunt.
     *
     * @return true uniquement si l'tat est DISPONIBLE
     */
    public boolean estDisponible() {
        return etat == EtatExemplaire.DISPONIBLE;
    }

    /**
     * Change l'tat de l'exemplaire.
     *
     * @param nouvelEtat le nouvel tat  appliquer (non nul)
     * @throws ExemplaireException si l'tat fourni est null ou si la transition
     * est invalide
     */
    public void changerEtat(EtatExemplaire nouvelEtat) throws ExemplaireException {
        if (nouvelEtat == null) {
            throw new ExemplaireException("L'tat d'un exemplaire ne peut pas tre null.");
        }
        // Rgle mtier : un exemplaire PERDU ne peut plus changer d'tat
        if (this.etat == EtatExemplaire.PERDU) {
            throw new ExemplaireException(
                    "L'exemplaire #" + id + " est marqu PERDU et ne peut plus changer d'tat."
            );
        }
        this.etat = nouvelEtat;
    }

    //  Getters 
    /**
     * @return identifiant unique de l'exemplaire
     */
    public int getId() {
        return id;
    }

    /**
     * @return le livre dont cet exemplaire est une copie
     */
    public Livre getLivre() {
        return livre;
    }

    /**
     * @return tat actuel de l'exemplaire
     */
    public EtatExemplaire getEtat() {
        return etat;
    }

    /**
     * @return date d'ajout de l'exemplaire dans le systme
     */
    public LocalDate getDateAjout() {
        return dateAjout;
    }

    // Equals / HashCode / ToString 
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Exemplaire)) {
            return false;
        }
        Exemplaire autre = (Exemplaire) obj;
        return id == autre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Exemplaire{id=%d, livre='%s', etat=%s, dateAjout=%s}",
                id, livre.getTitre(), etat, dateAjout);
    }
}
