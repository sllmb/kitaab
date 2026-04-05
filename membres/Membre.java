package membres;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import exceptions.BibliothequeException;

/**
 * Classe reprsentant un membre de la bibliothque.
 * ROLE 2   complter par le membre responsable des emprunts.
 */
public class Membre {

    //  Constante 
    public static final int MAX_EMPRUNTS = 3; // max 3 livres simultans

    //  Attributs 
    private int             id;
    private String          nom;
    private String          prenom;
    private String          email;
    private LocalDate       dateInscription;
    private List<Emprunt>   empruntsActifs;

    //  Constructeur 
    public Membre(int id, String nom, String prenom, String email)
            throws BibliothequeException {

        // Validation : nom non vide
        if (nom == null || nom.trim().isEmpty()) {
            throw new BibliothequeException("Le nom du membre ne peut pas tre vide.");
        }

        // Validation : email format simple (contient @ et un point aprs)
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new BibliothequeException("L'adresse email est invalide : " + email);
        }

        this.id              = id;
        this.nom             = nom.trim();
        this.prenom          = (prenom != null) ? prenom.trim() : "";
        this.email           = email.trim();
        this.dateInscription = LocalDate.now();
        this.empruntsActifs  = new ArrayList<>();
    }

    //  Mthodes 

    /**
     * Vrifie si le membre a des emprunts en retard.
     */
    public boolean aDesEmpruntsEnRetard() {
        for (Emprunt emprunt : empruntsActifs) {
            if (emprunt.estEnRetard()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vrifie si le membre peut encore emprunter (quota non atteint).
     */
    public boolean peutEmprunter() {
        return empruntsActifs.size() < MAX_EMPRUNTS;
    }

    //  Getters & Setters 
    public int            getId()              { return id; }
    public String         getNom()             { return nom; }
    public String         getPrenom()          { return prenom; }
    public String         getEmail()           { return email; }
    public LocalDate      getDateInscription() { return dateInscription; }
    public List<Emprunt>  getEmpruntsActifs()  { return empruntsActifs; }

    @Override
    public String toString() {
        return "[Membre #" + id + "] " + prenom + " " + nom + "  " + email;
    }
}