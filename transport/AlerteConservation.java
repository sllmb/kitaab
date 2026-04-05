package transport;

import java.time.LocalDateTime;

/**
 * Alerte dclenche automatiquement quand un seuil est dpass.
 * ROLE 5  DJ IMPLMENT
 */
public class AlerteConservation {

    private CapteurConservation capteur;
    private String              type;
    private double              valeurMesuree;
    private double              seuilDepasse;
    private LocalDateTime       dateAlerte;

    public AlerteConservation(CapteurConservation capteur, String type,
                               double valeurMesuree, double seuilDepasse) {
        this.capteur       = capteur;
        this.type          = type;
        this.valeurMesuree = valeurMesuree;
        this.seuilDepasse  = seuilDepasse;
        this.dateAlerte    = LocalDateTime.now();
    }

    public void afficher() {
        System.out.println("ALERTE Conservation");
        System.out.println("  Capteur    : " + capteur.getLocalisation());
        System.out.println("  Type       : " + type);
        System.out.println("  Valeur     : " + valeurMesuree);
        System.out.println("  Seuil      : " + seuilDepasse);
        System.out.println("  Date/Heure : " + dateAlerte);
    }

    public CapteurConservation getCapteur()       { return capteur; }
    public String              getType()          { return type; }
    public double              getValeurMesuree() { return valeurMesuree; }
    public double              getSeuilDepasse()  { return seuilDepasse; }
    public LocalDateTime       getDateAlerte()    { return dateAlerte; }
}
