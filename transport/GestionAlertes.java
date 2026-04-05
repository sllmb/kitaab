package transport;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe qui gre l'historique de toutes les alertes de conservation.
 * ROLE 5  DJ IMPLMENT
 */
public class GestionAlertes {

    private List<AlerteConservation> historique;

    public GestionAlertes() {
        this.historique = new ArrayList<>();
    }

    public void verifierCapteur(CapteurConservation capteur) {
        AlerteConservation alerte = capteur.verifierSeuils();
        if (alerte != null) {
            historique.add(alerte);
            System.out.println("Alerte enregistree dans l'historique.");
        }
    }

    public void afficherHistorique() {
        System.out.println("--- Historique des alertes (" + historique.size() + " au total) ---");
        if (historique.isEmpty()) {
            System.out.println("  Aucune alerte enregistree.");
            return;
        }
        for (int i = 0; i < historique.size(); i++) {
            System.out.println("\nAlerte #" + (i + 1));
            historique.get(i).afficher();
        }
    }

    public List<AlerteConservation> getAlertesTemperature() {
        List<AlerteConservation> resultat = new ArrayList<>();
        for (AlerteConservation a : historique) {
            if (a.getType().equals("TEMPERATURE")) resultat.add(a);
        }
        return resultat;
    }

    public List<AlerteConservation> getAlertesHumidite() {
        List<AlerteConservation> resultat = new ArrayList<>();
        for (AlerteConservation a : historique) {
            if (a.getType().equals("HUMIDITE")) resultat.add(a);
        }
        return resultat;
    }

    public int getNombreAlertes() { return historique.size(); }
}
