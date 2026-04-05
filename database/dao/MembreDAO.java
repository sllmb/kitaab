package database.dao;

import membres.Membre;
import database.DatabaseManager;
import exceptions.BibliothequeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la persistance des membres.
 * Mme principe que LivreDAO  chaque mthode = une opration SQL.
 */
public class MembreDAO implements DAO<Membre> {

    private Connection connection;

    public MembreDAO() throws SQLException {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Sauvegarde un membre en base.
     * SQL : INSERT INTO membres (nom, prenom, email, date_inscription) VALUES (?, ?, ?, ?)
     */
    @Override
    public void save(Membre membre) throws SQLException {
        String sql = "INSERT INTO membres (nom, prenom, email, date_inscription) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getEmail());
            stmt.setString(4, membre.getDateInscription().toString()); // LocalDate  String
            stmt.executeUpdate();
            System.out.println("Membre sauvegarde : " + membre.getPrenom() + " " + membre.getNom());
        }
    }

    /**
     * Rcupre un membre par son ID.
     * SQL : SELECT * FROM membres WHERE id = ?
     */
    @Override
    public Membre findById(int id) throws SQLException {
        String sql = "SELECT * FROM membres WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construireMembre(rs);
            }
        }
        return null;
    }

    /**
     * Rcupre tous les membres.
     * SQL : SELECT * FROM membres
     */
    @Override
    public List<Membre> findAll() throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                membres.add(construireMembre(rs));
            }
        }
        return membres;
    }

    /**
     * Met  jour un membre.
     * SQL : UPDATE membres SET nom = ?, prenom = ?, email = ? WHERE id = ?
     */
    @Override
    public void update(Membre membre) throws SQLException {
        String sql = "UPDATE membres SET nom = ?, prenom = ?, email = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getEmail());
            stmt.setInt   (4, membre.getId());
            stmt.executeUpdate();
            System.out.println("Membre mis a jour : " + membre.getPrenom() + " " + membre.getNom());
        }
    }

    /**
     * Supprime un membre par son ID.
     * SQL : DELETE FROM membres WHERE id = ?
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM membres WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Membre supprime (id=" + id + ")");
        }
    }

    /**
     * Trouve un membre par son email.
     * SQL : SELECT * FROM membres WHERE email = ?
     */
    public Membre findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM membres WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construireMembre(rs);
            }
        }
        return null;
    }

    /**
     * Construit un objet Membre depuis une ligne SQL.
     */
    private Membre construireMembre(ResultSet rs) throws SQLException {
        try {
            return new Membre(
                rs.getInt   ("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email")
            );
        } catch (BibliothequeException e) {
            throw new SQLException("Erreur construction Membre : " + e.getMessage());
        }
    }
}
