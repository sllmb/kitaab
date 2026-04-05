package transport;

/**
 * Exception personnalise pour les erreurs lies aux livraisons.
 * ROLE 5  DJ IMPLMENT
 */
public class LivraisonException extends Exception {
    public LivraisonException(String message) {
        super(message);
    }
}
