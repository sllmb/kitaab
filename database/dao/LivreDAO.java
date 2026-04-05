package database.dao;

import catalogue.Livre;
import database.DatabaseManager;
import exceptions.BibliothequeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour les livres.
 *
 * Un DAO est une classe qui s'occupe uniquement de sauvegarder
 * et rcuprer des donnes en base. Chaque mthode correspond
 *  une opration SQL : INSERT, SELECT, UPDATE, DELETE.
 *
 * IMPORTANT : On utilise toujours des PreparedStatement (jamais
 * de concatnation de String SQL) pour viter les failles de scurit.
 */
public class LivreDAO implements DAO<Livre> {

    private Connection connection;

    public LivreDAO() throws SQLException {
        // On rcupre la connexion unique via le Singleton
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Sauvegarde un livre en base de donnes.
     * SQL : INSERT INTO livres (titre, auteur, isbn, annee, genre) VALUES (?, ?, ?, ?, ?)
     * Les "?" sont remplacs par les vraies valeurs  c'est a un PreparedStatement.
     */
    @Override
    public void save(Livre livre) throws SQLException {
        String sql = "INSERT INTO livres (titre, auteur, isbn, annee, genre) VALUES (?, ?, ?, ?, ?)";

        // try-with-resources = ferme automatiquement le PreparedStatement aprs utilisation
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getTitre());   // remplace le 1er ?
            stmt.setString(2, livre.getAuteur());  // remplace le 2me ?
            stmt.setString(3, livre.getIsbn());    // remplace le 3me ?
            stmt.setInt   (4, livre.getAnnee());   // remplace le 4me ?
            stmt.setString(5, livre.getGenre());   // remplace le 5me ?
            stmt.executeUpdate(); // excute l'INSERT
            System.out.println("Livre sauvegarde : " + livre.getTitre());
        }
    }

    /**
     * Rcupre un livre par son ID.
     * SQL : SELECT * FROM livres WHERE id = ?
     */
    @Override
    public Livre findById(int id) throws SQLException {
        String sql = "SELECT * FROM livres WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            // ResultSet = le rsultat de la requte SQL (comme un tableau de lignes)
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) { // s'il y a au moins une ligne de rsultat
                return construireLivre(rs);
            }
        }
        return null; // aucun livre trouv avec cet ID
    }

    /**
     * Rcupre tous les livres de la base.
     * SQL : SELECT * FROM livres
     */
    @Override
    public List<Livre> findAll() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // on parcourt toutes les lignes du rsultat
            while (rs.next()) {
                livres.add(construireLivre(rs));
            }
        }
        return livres;
    }

    /**
     * Met  jour un livre existant.
     * SQL : UPDATE livres SET titre = ?, auteur = ? ... WHERE id = ?
     */
    @Override
    public void update(Livre livre) throws SQLException {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, isbn = ?, annee = ?, genre = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt   (4, livre.getAnnee());
            stmt.setString(5, livre.getGenre());
            stmt.setInt   (6, livre.getId());
            stmt.executeUpdate();
            System.out.println("Livre mis a jour : " + livre.getTitre());
        }
    }

    /**
     * Supprime un livre par son ID.
     * SQL : DELETE FROM livres WHERE id = ?
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM livres WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Livre supprime (id=" + id + ")");
        }
    }

    /**
     * Recherche un livre par son ISBN.
     * SQL : SELECT * FROM livres WHERE isbn = ?
     */
    public Livre findByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM livres WHERE isbn = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construireLivre(rs);
            }
        }
        return null;
    }

    /**
     * Mthode prive utilitaire : construit un objet Livre  partir d'une ligne SQL.
     * rs.getString("titre") = rcupre la colonne "titre" de la ligne courante.
     */
    private Livre construireLivre(ResultSet rs) throws SQLException {
        return new Livre(
            rs.getInt   ("id"),
            rs.getString("titre"),
            rs.getString("auteur"),
            rs.getString("isbn"),
            rs.getInt   ("annee"),
            rs.getString("genre")
        );
    }
}
