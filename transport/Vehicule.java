package transport;

/**
 * Classe reprsentant le vhicule de livraison de la bibliothque.
 * ROLE 5  DJ IMPLMENT
 */
public class Vehicule {

    private String  immatriculation;
    private String  conducteur;
    private int     capaciteMax;
    private boolean tourneeEnCours;

    public Vehicule(String immatriculation, String conducteur, int capaciteMax)
            throws VehiculeException {

        if (immatriculation == null || !immatriculation.matches("[A-Z]{2}-\\d{3}-[A-Z]{2}")) {
            throw new VehiculeException(
                "Immatriculation invalide : doit etre au format XX-000-XX (ex: AB-123-CD)"
            );
        }
        if (conducteur == null || conducteur.trim().isEmpty()) {
            throw new VehiculeException("Le nom du conducteur ne peut pas etre vide.");
        }
        if (capaciteMax <= 0) {
            throw new VehiculeException("La capacite maximale doit etre superieure a 0.");
        }

        this.immatriculation = immatriculation;
        this.conducteur      = conducteur;
        this.capaciteMax     = capaciteMax;
        this.tourneeEnCours  = false;
    }

    public void demarrer() throws VehiculeException {
        if (tourneeEnCours) {
            throw new VehiculeException("Impossible de demarrer : une tournee est deja en cours.");
        }
        tourneeEnCours = true;
        System.out.println("Vehicule " + immatriculation + " - tournee demarree par " + conducteur);
    }

    public void finirTournee() throws VehiculeException {
        if (!tourneeEnCours) {
            throw new VehiculeException("Impossible de terminer : aucune tournee en cours.");
        }
        tourneeEnCours = false;
        System.out.println("Vehicule " + immatriculation + " - tournee terminee.");
    }

    public void afficherEtat() {
        System.out.println("--- Vehicule ---");
        System.out.println("  Immatriculation : " + immatriculation);
        System.out.println("  Conducteur      : " + conducteur);
        System.out.println("  Capacite max    : " + capaciteMax + " exemplaires");
        System.out.println("  En tournee      : " + (tourneeEnCours ? "Oui" : "Non"));
    }

    public String  getImmatriculation() { return immatriculation; }
    public String  getConducteur()      { return conducteur; }
    public int     getCapaciteMax()     { return capaciteMax; }
    public boolean isTourneeEnCours()   { return tourneeEnCours; }
}
