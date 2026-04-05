package exceptions;

/**
 * Exception commune  tout le projet bibliothque.
 * Tous les rles utilisent cette mme exception.
 */
public class BibliothequeException extends Exception {
    public BibliothequeException(String message) {
        super(message);
    }
}
