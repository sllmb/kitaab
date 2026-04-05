package transport;

/**
 * Capteur de conservation mesurant temprature et humidit.
 * ROLE 5  DJ IMPLMENT
 */
public class CapteurConservation {

    public static final double SEUIL_TEMPERATURE = 25.0;
    public static final double SEUIL_HUMIDITE    = 70.0;

    private int    id;
    private String localisation;
    private double temperature;
    private double humidite;

    public CapteurConservation(int id, String localisation) throws CapteurException {
        if (localisation == null || localisation.trim().isEmpty()) {
            throw new CapteurException("La localisation du capteur ne peut pas etre vide.");
        }
        this.id           = id;
        this.localisation = localisation;
        this.temperature  = 0.0;
        this.humidite     = 0.0;
    }

    public void lireTemperature(double nouvelleTemp) throws CapteurException {
        if (nouvelleTemp < -10 || nouvelleTemp > 40) {
            throw new CapteurException(
                "Temperature invalide : doit etre entre -10 et 40 degres. Valeur recue : " + nouvelleTemp
            );
        }
        this.temperature = nouvelleTemp;
        System.out.println("Capteur [" + localisation + "] - Temperature : " + temperature + " degres");
    }

    public void lireHumidite(double nouvelleHumidite) throws CapteurException {
        if (nouvelleHumidite < 0 || nouvelleHumidite > 100) {
            throw new CapteurException(
                "Humidite invalide : doit etre entre 0 et 100%. Valeur recue : " + nouvelleHumidite
            );
        }
        this.humidite = nouvelleHumidite;
        System.out.println("Capteur [" + localisation + "] - Humidite : " + humidite + "%");
    }

    public AlerteConservation verifierSeuils() {
        if (temperature > SEUIL_TEMPERATURE) {
            System.out.println("ALERTE - Temperature trop elevee : " + temperature + " degres");
            return new AlerteConservation(this, "TEMPERATURE", temperature, SEUIL_TEMPERATURE);
        }
        if (humidite > SEUIL_HUMIDITE) {
            System.out.println("ALERTE - Humidite trop elevee : " + humidite + "%");
            return new AlerteConservation(this, "HUMIDITE", humidite, SEUIL_HUMIDITE);
        }
        System.out.println("OK - Capteur [" + localisation + "] - Conditions normales.");
        return null;
    }

    public void afficherEtat() {
        System.out.println("--- Capteur #" + id + " ---");
        System.out.println("  Localisation : " + localisation);
        System.out.println("  Temperature  : " + temperature + " degres");
        System.out.println("  Humidite     : " + humidite + "%");
    }

    public int    getId()           { return id; }
    public String getLocalisation() { return localisation; }
    public double getTemperature()  { return temperature; }
    public double getHumidite()     { return humidite; }
}
