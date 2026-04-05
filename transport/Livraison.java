package transport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe reprsentant une livraison vers une annexe.
 * ROLE 5  DJ IMPLMENT
 */
public class Livraison {

    private Vehicule     vehicule;
    private List<String> exemplairesTransportes;
    private LocalDate    dateDepart;
    private String       annexeDestination;

    public Livraison(Vehicule vehicule, String annexeDestination) throws LivraisonException {
        if (vehicule == null) {
            throw new LivraisonException("Le vehicule ne peut pas etre null.");
        }
        if (annexeDestination == null || annexeDestination.trim().isEmpty()) {
            throw new LivraisonException("L'annexe de destination ne peut pas etre vide.");
        }
        this.vehicule               = vehicule;
        this.annexeDestination      = annexeDestination;
        this.exemplairesTransportes = new ArrayList<>();
        this.dateDepart             = LocalDate.now();
    }

    public void ajouterExemplaire(String idExemplaire) throws LivraisonException {
        if (idExemplaire == null || idExemplaire.trim().isEmpty()) {
            throw new LivraisonException("L'ID de l'exemplaire ne peut pas etre vide.");
        }
        if (exemplairesTransportes.size() >= vehicule.getCapaciteMax()) {
            throw new LivraisonException(
                "Capacite maximale atteinte : " + vehicule.getCapaciteMax() + " exemplaires max."
            );
        }
        exemplairesTransportes.add(idExemplaire);
        System.out.println("  + Exemplaire [" + idExemplaire + "] ajoute a la livraison.");
    }

    public void demarrerLivraison() throws VehiculeException, LivraisonException {
        if (exemplairesTransportes.isEmpty()) {
            throw new LivraisonException("Impossible de demarrer : aucun exemplaire a livrer.");
        }
        vehicule.demarrer();
        System.out.println("Livraison vers [" + annexeDestination + "] demarree le " + dateDepart);
        System.out.println("  Nombre d'exemplaires : " + exemplairesTransportes.size());
    }

    public void terminerLivraison() throws VehiculeException {
        vehicule.finirTournee();
        System.out.println("Livraison vers [" + annexeDestination + "] terminee.");
    }

    public void afficherDetail() {
        System.out.println("--- Livraison ---");
        System.out.println("  Vehicule    : " + vehicule.getImmatriculation());
        System.out.println("  Destination : " + annexeDestination);
        System.out.println("  Date depart : " + dateDepart);
        System.out.println("  Exemplaires : " + exemplairesTransportes.size() + "/" + vehicule.getCapaciteMax());
        for (String id : exemplairesTransportes) {
            System.out.println("    - " + id);
        }
    }

    public Vehicule     getVehicule()               { return vehicule; }
    public List<String> getExemplairesTransportes() { return exemplairesTransportes; }
    public LocalDate    getDateDepart()             { return dateDepart; }
    public String       getAnnexeDestination()      { return annexeDestination; }
}
