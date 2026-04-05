package membres;

import catalogue.Exemplaire;
import exceptions.BibliothequeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe reprsentant un emprunt d'un exemplaire par un membre.
 * ROLE 2   complter par le membre responsable des emprunts.
 */
public class Emprunt {

    //  Enum statut 
    public enum StatutEmprunt {
        EN_COURS, RENDU, EN_RETARD
    }

    //  Attributs 
    private int           id;
    private Membre        membre;
    private Exemplaire    exemplaire;
    private LocalDate     dateEmprunt;
    private LocalDate     dateRetourPrevue;  // dateEmprunt + 14 jours
    private LocalDate     dateRetourReelle;  // null tant que non rendu
    private StatutEmprunt statut;

    //  Constructeur 
    public Emprunt(int id, Membre membre, Exemplaire exemplaire)
            throws BibliothequeException {

        if (membre == null) {
            throw new BibliothequeException("Le membre ne peut pas tre null.");
        }
        if (exemplaire == null) {
            throw new BibliothequeException("L'exemplaire ne peut pas tre null.");
        }

        this.id               = id;
        this.membre           = membre;
        this.exemplaire       = exemplaire;
        this.dateEmprunt      = LocalDate.now();
        this.dateRetourPrevue = dateEmprunt.plusDays(14); // 14 jours
        this.dateRetourReelle = null;
        this.statut           = StatutEmprunt.EN_COURS;
    }

    //  Mthodes 

    /**
     * Calcule le nombre de jours de retard.
     * Retourne 0 si pas de retard.
     */
    public long calculerJoursRetard() {
        // Si dj rendu, on compare la date de retour relle avec la date prvue
        LocalDate dateReference = (dateRetourReelle != null) ? dateRetourReelle : LocalDate.now();

        if (dateReference.isAfter(dateRetourPrevue)) {
            return ChronoUnit.DAYS.between(dateRetourPrevue, dateReference);
        }
        return 0;
    }

    /**
     * Vrifie si l'emprunt est en retard.
     */
    public boolean estEnRetard() {
        return calculerJoursRetard() > 0;
    }

    //  Getters & Setters 
    public int            getId()               { return id; }
    public Membre         getMembre()           { return membre; }
    public Exemplaire     getExemplaire()       { return exemplaire; }
    public LocalDate      getDateEmprunt()      { return dateEmprunt; }
    public LocalDate      getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate      getDateRetourReelle() { return dateRetourReelle; }
    public StatutEmprunt  getStatut()           { return statut; }

    public void setStatut(StatutEmprunt statut)         { this.statut = statut; }
    public void setDateRetourReelle(LocalDate date)     { this.dateRetourReelle = date; }

    @Override
    public String toString() {
        return "[Emprunt #" + id + "] " + membre.getPrenom() + "  " +
               exemplaire.getLivre().getTitre() + " | Retour prvu : " + dateRetourPrevue;
    }
}