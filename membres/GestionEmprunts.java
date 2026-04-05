package membres;

import catalogue.Exemplaire;
import catalogue.EtatExemplaire;
import catalogue.ExemplaireException;
import caisse.Caisse;
import exceptions.BibliothequeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe grant les oprations d'emprunt et de retour.
 * ROLE 2   complter par le membre responsable des emprunts.
 */
public class GestionEmprunts {

    //  Attributs 
    private List<Emprunt> emprunts;
    private List<Membre>  membres;
    private Caisse        caisse;   // pour gnrer les amendes en cas de retard
    private int           compteurId;

    //  Constructeur 
    public GestionEmprunts(Caisse caisse) {
        this.emprunts    = new ArrayList<>();
        this.membres     = new ArrayList<>();
        this.caisse      = caisse;
        this.compteurId  = 1;
    }

    //  Mthodes 

    /**
     * Cre un emprunt pour un membre et un exemplaire.
     * Vrifie : quota membre, disponibilit exemplaire.
     */
    public Emprunt emprunter(Membre membre, Exemplaire exemplaire)
            throws BibliothequeException {

        // Vrification 1 : le membre peut-il emprunter ?
        if (!membre.peutEmprunter()) {
            throw new BibliothequeException(
                "Le membre " + membre.getPrenom() + " " + membre.getNom() +
                " a atteint le quota maximum de " + Membre.MAX_EMPRUNTS + " emprunts."
            );
        }

        // Vrification 2 : l'exemplaire est-il disponible ?
        if (!exemplaire.estDisponible()) {
            throw new BibliothequeException(
                "L'exemplaire du livre  " + exemplaire.getLivre().getTitre() +
                "  n'est pas disponible."
            );
        }

        // Cration de l'emprunt
        Emprunt emprunt = new Emprunt(compteurId++, membre, exemplaire);

        // Marquer l'exemplaire comme non disponible
        try { exemplaire.changerEtat(EtatExemplaire.EMPRUNTE); } catch (ExemplaireException e) { throw new BibliothequeException(e.getMessage()); }

        // Ajouter l'emprunt  la liste du membre et  la liste globale
        membre.getEmpruntsActifs().add(emprunt);
        emprunts.add(emprunt);

        return emprunt;
    }

    /**
     * Enregistre le retour d'un livre.
     * Si retard  appelle caisse.enregistrerAmende().
     */
    public void rendreLivre(Emprunt emprunt) throws BibliothequeException {

        // Vrification : l'emprunt est-il encore en cours ?
        if (emprunt.getStatut() == Emprunt.StatutEmprunt.RENDU) {
            throw new BibliothequeException(
                "Cet emprunt (ID: " + emprunt.getId() + ") a dj t retourn."
            );
        }

        // Enregistrer la date de retour relle
        emprunt.setDateRetourReelle(LocalDate.now());

        // Calculer le retard
        long joursRetard = emprunt.calculerJoursRetard();

        if (joursRetard > 0) {
            // Mettre  jour le statut avant d'appeler la caisse
            emprunt.setStatut(Emprunt.StatutEmprunt.EN_RETARD);
            // Appeler la caisse pour enregistrer l'amende
            caisse.enregistrerAmende(emprunt.getMembre(), joursRetard);
        } else {
            emprunt.setStatut(Emprunt.StatutEmprunt.RENDU);
        }

        // Rendre l'exemplaire disponible
        try { emprunt.getExemplaire().changerEtat(EtatExemplaire.DISPONIBLE); } catch (ExemplaireException e) { throw new BibliothequeException(e.getMessage()); }

        // Retirer l'emprunt de la liste active du membre
        emprunt.getMembre().getEmpruntsActifs().remove(emprunt);
    }

    /**
     * Retourne la liste des emprunts en retard.
     */
    public List<Emprunt> listerEmpruntsEnRetard() {
        List<Emprunt> enRetard = new ArrayList<>();
        for (Emprunt emprunt : emprunts) {
            if (emprunt.estEnRetard()) {
                enRetard.add(emprunt);
            }
        }
        return enRetard;
    }

    /**
     * Retourne un membre par son ID  utilis par les autres rles.
     */
    public Membre getMembreById(int id) {
        for (Membre membre : membres) {
            if (membre.getId() == id) {
                return membre;
            }
        }
        return null; // non trouv
    }

    //  Getters 
    public List<Emprunt> getEmprunts() { return emprunts; }
    public List<Membre>  getMembres()  { return membres; }
}