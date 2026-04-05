package caisse;
import membres.Membre;
import membres.Emprunt;
import java.math.BigDecimal;
import java.time.LocalDate;


public class Amende {

    //  Enum statut 
    public enum StatutAmende {
        PAYEE, IMPAYEE
    }

    //  Constante 
    public static final BigDecimal TARIF_PAR_JOUR = new BigDecimal("150"); // 0.20/jour

    //  Attributs 
    private int          id;
    private Membre       membre;
    private Emprunt      emprunt;
    private BigDecimal   montant;
    private LocalDate    dateCreation;
    private StatutAmende statut;

    //  Constructeur 

    public Amende(int id, Membre membre, Emprunt emprunt, BigDecimal montant)
            throws exceptions.BibliothequeException {
        if(membre == null){
            throw new exceptions.BibliothequeException("on doit avoir obligatoirement un membre");
        }
        if(montant == null || montant.compareTo(BigDecimal.ZERO)<=0){
            throw new exceptions.BibliothequeException("le montant doit etre superieur a 0");
        }
        this.id           = id;
        this.membre       = membre;
        this.emprunt      = emprunt;
        this.montant      = montant;
        this.dateCreation = LocalDate.now();
        this.statut       = StatutAmende.IMPAYEE;
    }

    //  Getters & Setters 
    public int          getId()           { return id; }
    public Membre       getMembre()       { return membre; }
    public Emprunt      getEmprunt()      { return emprunt; }
    public BigDecimal   getMontant()      { return montant; }
    public LocalDate    getDateCreation() { return dateCreation; }
    public StatutAmende getStatut()       { return statut; }

    public void setStatut(StatutAmende statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "[Amende #" + id + "] " + membre.getPrenom() + "  " +
               montant + "FCFA  " + statut;
    }
}
