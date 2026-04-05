package caisse;

import membres.Membre;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe reprsentant la caisse de la bibliothque.
 * ROLE 3   complter par le membre responsable de la caisse.
 */
public class Caisse {

    //  Attributs 
    private BigDecimal   solde;
    private List<Amende> amendes;
    private int          compteurId;

    //  Constructeur 
    public Caisse(BigDecimal soldeInitial) throws exceptions.BibliothequeException {
        if (soldeInitial.compareTo(BigDecimal.ZERO) < 0) {
            throw new exceptions.BibliothequeException("Le solde initial ne peut pas etre negatif.");
        }
        this.solde       = soldeInitial;
        this.amendes     = new ArrayList<>();
        this.compteurId  = 1;
    }

    //  Mthodes   implmenter (Rle 3) 

    /**
     * Enregistre une amende dans la caisse.
     * Appele automatiquement par GestionEmprunts lors d'un retard.
     * TODO Rle 3 : implmenter
     */
    public void enregistrerAmende(membres.Membre membre, long joursRetard) {
        try {
            BigDecimal montant = Amende.TARIF_PAR_JOUR.multiply(new BigDecimal(joursRetard));
            Amende amende = new Amende(compteurId++, membre, null, montant);
            amendes.add(amende);
            System.out.println("Amende enregistree : " + amende);
        } catch (exceptions.BibliothequeException e) {
            System.out.println("Erreur creation amende : " + e.getMessage());
        }
    }

    /**
     * Encaisse le paiement d'une amende.
     * TODO Rle 3 : vrifier que l'amende n'est pas dj paye
     */
    public void encaisser(Amende amende) throws exceptions.BibliothequeException {
        if (amende.getStatut() == Amende.StatutAmende.PAYEE) {
            throw new exceptions.BibliothequeException("L'amende est dj paye.");
        }
        amende.setStatut(Amende.StatutAmende.PAYEE);
        solde = solde.add(amende.getMontant());
        System.out.println("Amende encaisse : " + amende);
    }

    
    // Retourne les amendes impayes d'un membre.
     
    public List<Amende> getAmendesImpayeesParMembre(Membre membre) {
        List<Amende> impayees = new ArrayList<>();
        for (Amende amende : amendes) {
            if (amende.getStatut() == Amende.StatutAmende.IMPAYEE && amende.getMembre().getId() == membre.getId()) {
                impayees.add(amende);
            }
        }
        return impayees;
    }

    
     //Affiche un rapport de la caisse.
    
    public void afficherRapport() {
        int payees = 0;
        int impayees = 0;
        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalImpaye = BigDecimal.ZERO;
        
        for (Amende amende : amendes) {
            if (amende.getStatut() == Amende.StatutAmende.PAYEE) {
                payees++;
                totalPaye = totalPaye.add(amende.getMontant());
            } else {
                impayees++;
                totalImpaye = totalImpaye.add(amende.getMontant());
            }
        }
        
        System.out.println("--- Rapport Caisse ---");
        System.out.println("  Solde : " + solde + "");
        System.out.println("  Amendes totales : " + amendes.size());
        System.out.println("  Amendes payes : " + payees + " (Total : " + totalPaye + ")");
        System.out.println("  Amendes impayes : " + impayees + " (Total : " + totalImpaye + ")");
    }

    //  Getters 
    public BigDecimal   getSolde()   { return solde; }
    public List<Amende> getAmendes() { return amendes; }
}
