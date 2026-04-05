package caisse;

import catalogue.Catalogue;
import catalogue.Exemplaire;
import catalogue.Livre;

public class Stock {

    //  Constante 
    public static final int SEUIL_STOCK = 2; // alerte si moins de 2 exemplaires dispo

    //  Attributs 
    private Catalogue catalogue;

    //  Constructeur 
    public Stock(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    
    public int getNombreDisponibles(Livre livre) {
        if (livre ==null){ return 0;}
        int compteur = 0;
        for (Exemplaire ex : catalogue.listerTous()) {
            if (ex.getLivre().getId() == livre.getId() && ex.estDisponible()) {
                compteur++;
            }
        }
        return compteur;
    }

    /**
     * Affiche une alerte si le stock d'un livre passe sous le seuil.
     * TODO Rle 3 : implmenter
     */
    public void alerterSiStockBas(Livre livre) {
        
        int dispo = getNombreDisponibles(livre);
        if (dispo < SEUIL_STOCK) {
            System.out.println("ALERTE STOCK  \"" + livre.getTitre() +
                               "\" : seulement " + dispo + " exemplaire(s) disponible(s) !");
        }
    }
}
