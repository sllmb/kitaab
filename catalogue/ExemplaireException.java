package catalogue;

/**
 * Exception personnalise pour les erreurs lies aux exemplaires.
 * Lance si l'tat est invalide ou si un exemplaire est introuvable.
 */
public class ExemplaireException extends Exception {

    /**
     * Construit une ExemplaireException avec un message dtaill.
     *
     * @param message le message dcrivant l'erreur
     */
    public ExemplaireException(String message) {
        super(message);
    }

    /**
     * Construit une ExemplaireException avec un message et une cause.
     *
     * @param message le message dcrivant l'erreur
     * @param cause la cause originelle de l'exception
     */
    public ExemplaireException(String message, Throwable cause) {
        super(message, cause);
    }
}
