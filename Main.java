import catalogue.Catalogue;
import catalogue.Exemplaire;
import catalogue.EtatExemplaire;
import catalogue.ExemplaireException;
import catalogue.Livre;
import membres.Emprunt;
import membres.GestionEmprunts;
import membres.Membre;
import caisse.Amende;
import caisse.Caisse;
import caisse.Stock;
import transport.Vehicule;
import transport.VehiculeException;
import transport.Livraison;
import transport.LivraisonException;
import transport.CapteurConservation;
import transport.CapteurException;
import transport.GestionAlertes;
import exceptions.BibliothequeException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe principale  simule un scnario complet et raliste
 * du systme de gestion de la bibliothque municipale Kitaab.
 * ROLE 5  Implment au Jour 3
 */
public class Main {

    public static void main(String[] args) {

        separateur("BIBLIOTHEQUE MUNICIPALE KITAAB  Simulation complte");

        try {

            // 
            // TAPE 1  Cration du catalogue et ajout des livres
            // 
            separateur("ETAPE 1 : Cration du catalogue de livres");

            Catalogue catalogue = new Catalogue();

            Livre livre1 = new Livre("Le Petit Prince",  "Antoine de Saint-Exupery", "978-2070612758", 1943, "Roman");
            Livre livre2 = new Livre("1984",              "George Orwell",            "978-2070368228", 1949, "Dystopie");
            Livre livre3 = new Livre("L'Alchimiste",      "Paulo Coelho",             "978-2290004449", 1988, "Roman");

            // Ajout de plusieurs exemplaires par livre
            Exemplaire ex1a = catalogue.ajouterLivre(livre1); // 1er exemplaire du Petit Prince
            Exemplaire ex1b = catalogue.ajouterLivre(livre1); // 2me exemplaire du Petit Prince
            Exemplaire ex2a = catalogue.ajouterLivre(livre2); // 1er exemplaire de 1984
            Exemplaire ex3a = catalogue.ajouterLivre(livre3); // 1er exemplaire de L'Alchimiste

            catalogue.afficherResume();

            // 
            // TAPE 2  Cration de la caisse et inscription des membres
            // 
            separateur("ETAPE 2 : Inscription des membres");

            Caisse caisse = new Caisse(new BigDecimal("500.00"));
            GestionEmprunts gestionEmprunts = new GestionEmprunts(caisse);

            Membre membre1 = new Membre(1, "Diallo", "Moussa", "moussa.diallo@email.com");
            Membre membre2 = new Membre(2, "Ndiaye", "Fatou",  "fatou.ndiaye@email.com");
            Membre membre3 = new Membre(3, "Sarr",   "Ibou",   "ibou.sarr@email.com");

            gestionEmprunts.getMembres().add(membre1);
            gestionEmprunts.getMembres().add(membre2);
            gestionEmprunts.getMembres().add(membre3);

            System.out.println("Inscrit : " + membre1);
            System.out.println("Inscrit : " + membre2);
            System.out.println("Inscrit : " + membre3);

            // 
            // TAPE 3  Emprunts de livres
            // 
            separateur("ETAPE 3 : Emprunts de livres");

            Emprunt emprunt1 = gestionEmprunts.emprunter(membre1, ex1a);
            System.out.println("OK : " + emprunt1);

            Emprunt emprunt2 = gestionEmprunts.emprunter(membre2, ex2a);
            System.out.println("OK : " + emprunt2);

            Emprunt emprunt3 = gestionEmprunts.emprunter(membre3, ex3a);
            System.out.println("OK : " + emprunt3);

            // Test erreur : exemplaire dj emprunt
            System.out.println("\n-- Test exemplaire indisponible --");
            try {
                gestionEmprunts.emprunter(membre2, ex1a);
            } catch (BibliothequeException e) {
                System.out.println("ERREUR attendue : " + e.getMessage());
            }

            catalogue.afficherResume();

            // 
            // TAPE 4  Retour de livres (dont un en retard)
            // 
            separateur("ETAPE 4 : Retours de livres");

            // Fatou rend 1984 dans les dlais
            gestionEmprunts.rendreLivre(emprunt2);
            System.out.println("Fatou a rendu 1984 dans les delais  pas d'amende.");

            // Ibou rend L'Alchimiste dans les dlais
            gestionEmprunts.rendreLivre(emprunt3);
            System.out.println("Ibou a rendu L'Alchimiste dans les delais  pas d'amende.");

            // Simulation d'un retard pour Moussa :
            // On cre un emprunt dont la date de retour prvue est dj dpasse
            System.out.println("\n-- Simulation retard : Moussa emprunte le 2eme exemplaire du Petit Prince --");
            Emprunt empruntRetard = new Emprunt(99, membre1, ex1b);
            // On force la dateRetourPrevue dans le pass via dateRetourReelle
            empruntRetard.setDateRetourReelle(LocalDate.now());
            // La dateRetourPrevue = dateEmprunt + 14j = aujourd'hui + 14j
            // Pour simuler le retard, on utilise un emprunt dont on set
            // la date de retour relle aprs la date prvue
            // Mthode : on cre l'emprunt et on attend que calculerJoursRetard() > 0
            // En pratique pour la dmo, on affiche directement les emprunts en retard
            System.out.println("Emprunts en retard detectes : " + gestionEmprunts.listerEmpruntsEnRetard().size());

            System.out.println();
            caisse.afficherRapport();

            // 
            // TAPE 5  Paiement d'une amende
            // 
            separateur("ETAPE 5 : Paiement des amendes");

            List<Amende> amendesMoussa = caisse.getAmendesImpayeesParMembre(membre1);
            if (!amendesMoussa.isEmpty()) {
                Amende amende = amendesMoussa.get(0);
                System.out.println("Amende trouvee : " + amende);
                caisse.encaisser(amende);

                // Test erreur : payer une amende dj paye
                System.out.println("\n-- Test amende deja payee --");
                try {
                    caisse.encaisser(amende);
                } catch (BibliothequeException e) {
                    System.out.println("ERREUR attendue : " + e.getMessage());
                }
            } else {
                System.out.println("Aucune amende impayee pour Moussa.");
            }

            System.out.println();
            caisse.afficherRapport();

            // 
            // TAPE 6  Vrification du stock
            // 
            separateur("ETAPE 6 : Verification du stock");

            Stock stock = new Stock(catalogue);
            System.out.println("Disponibles 'Le Petit Prince' : " + stock.getNombreDisponibles(livre1));
            System.out.println("Disponibles '1984'             : " + stock.getNombreDisponibles(livre2));
            System.out.println("Disponibles 'L'Alchimiste'     : " + stock.getNombreDisponibles(livre3));

            stock.alerterSiStockBas(livre2); // 1 seul exemplaire  alerte !
            stock.alerterSiStockBas(livre3); // 1 seul exemplaire  alerte !
            stock.alerterSiStockBas(livre1); // 2 exemplaires  pas d'alerte

            // 
            // TAPE 7  Livraison vers une annexe
            // 
            separateur("ETAPE 7 : Livraison vers une annexe");

            Vehicule vehicule = new Vehicule("AB-123-CD", "Amadou Ba", 5);
            vehicule.afficherEtat();

            Livraison livraison = new Livraison(vehicule, "Annexe Nord");
            livraison.ajouterExemplaire("EX-001");
            livraison.ajouterExemplaire("EX-002");
            livraison.ajouterExemplaire("EX-003");
            livraison.afficherDetail();
            livraison.demarrerLivraison();
            livraison.terminerLivraison();

            // Test erreur : capacit dpasse
            System.out.println("\n-- Test vehicule capacite limitee --");
            try {
                Vehicule petitVehicule = new Vehicule("CD-456-EF", "Oumar Sy", 2);
                Livraison petiteLivraison = new Livraison(petitVehicule, "Annexe Sud");
                petiteLivraison.ajouterExemplaire("EX-010");
                petiteLivraison.ajouterExemplaire("EX-011");
                petiteLivraison.ajouterExemplaire("EX-012"); // dpasse la capacit !
            } catch (LivraisonException e) {
                System.out.println("ERREUR attendue : " + e.getMessage());
            }

            // 
            // TAPE 8  Surveillance des capteurs de conservation
            // 
            separateur("ETAPE 8 : Surveillance des capteurs de conservation");

            GestionAlertes gestionAlertes = new GestionAlertes();

            CapteurConservation capteur1 = new CapteurConservation(1, "Salle principale");
            capteur1.lireTemperature(20.0);
            capteur1.lireHumidite(55.0);
            gestionAlertes.verifierCapteur(capteur1); // conditions normales

            CapteurConservation capteur2 = new CapteurConservation(2, "Reserve livres anciens");
            capteur2.lireTemperature(32.0); // trop chaud !
            capteur2.lireHumidite(60.0);
            gestionAlertes.verifierCapteur(capteur2); // alerte temperature

            CapteurConservation capteur3 = new CapteurConservation(3, "Cave archives");
            capteur3.lireTemperature(22.0);
            capteur3.lireHumidite(85.0); // trop humide !
            gestionAlertes.verifierCapteur(capteur3); // alerte humidite

            System.out.println();
            gestionAlertes.afficherHistorique();
            System.out.println("Alertes temperature : " + gestionAlertes.getAlertesTemperature().size());
            System.out.println("Alertes humidite    : " + gestionAlertes.getAlertesHumidite().size());

            // 
            // RSUM FINAL
            // 
            separateur("RESUME FINAL DU SYSTEME");
            catalogue.afficherResume();
            System.out.println();
            caisse.afficherRapport();
            System.out.println("\nEmprunts en retard  : " + gestionEmprunts.listerEmpruntsEnRetard().size());
            System.out.println("Alertes conservation : " + gestionAlertes.getNombreAlertes());

            separateur("FIN DE LA SIMULATION");

        } catch (BibliothequeException e) {
            System.out.println("ERREUR METIER : " + e.getMessage());
            e.printStackTrace();
        } catch (ExemplaireException e) {
            System.out.println("ERREUR EXEMPLAIRE : " + e.getMessage());
            e.printStackTrace();
        } catch (VehiculeException e) {
            System.out.println("ERREUR VEHICULE : " + e.getMessage());
            e.printStackTrace();
        } catch (LivraisonException e) {
            System.out.println("ERREUR LIVRAISON : " + e.getMessage());
            e.printStackTrace();
        } catch (CapteurException e) {
            System.out.println("ERREUR CAPTEUR : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERREUR INATTENDUE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Mthode utilitaire pour sparer visuellement les tapes */
    private static void separateur(String titre) {
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  " + titre);
        System.out.println("=".repeat(55));
    }
}
