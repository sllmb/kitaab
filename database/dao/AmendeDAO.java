package database.dao;

import caisse.Amende;
import membres.Membre;
import membres.Emprunt;
import catalogue.Exemplaire;
import catalogue.EtatExemplaire;
import catalogue.Livre;
import database.DatabaseManager;
import exceptions.BibliothequeException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la persistance des amendes.
 */
public class AmendeDAO implements DAO<Amende> {

    private Connection connection;

    public AmendeDAO() throws SQLException {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Sauvegarde une amende en base.
     * SQL : INSERT INTO amendes (membre_id, emprunt_id, montant, date_creation, statut) VALUES (?, ?, ?, ?, ?)
     */
    @Override
    public void save(Amende amende) throws SQLException {
        String sql = "INSERT INTO amendes (membre_id, emprunt_id, montant, date_creation, statut) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt   (1, amende.getMembre().getId());
            stmt.setInt   (2, amende.getEmprunt().getId());
            // BigDecimal  double pour SQLite (REAL)
            stmt.setDouble(3, amende.getMontant().doubleValue());
            stmt.setString(4, amende.getDateCreation().toString());
            stmt.setString(5, amende.getStatut().toString());
            stmt.executeUpdate();
            System.out.println("Amende sauvegardee : " + amende.getMontant() + "");
        }
    }

    /**
     * Rcupre une amende par son ID.
     */
    @Override
    public Amende findById(int id) throws SQLException {
        String sql = "SELECT * FROM amendes WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construireAmende(rs);
            }
        }
        return null;
    }

    /**
     * Rcupre toutes les amendes.
     */
    @Override
    public List<Amende> findAll() throws SQLException {
        List<Amende> amendes = new ArrayList<>();
        String sql = "SELECT * FROM amendes";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                amendes.add(construireAmende(rs));
            }
        }
        return amendes;
    }

    /**
     * Met  jour le statut d'une amende (PAYEE ou IMPAYEE).
     */
    @Override
    public void update(Amende amende) throws SQLException {
        String sql = "UPDATE amendes SET statut = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, amende.getStatut().toString());
            stmt.setInt   (2, amende.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Supprime une amende par son ID.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM amendes WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Rcupre toutes les amendes impayes.
     * SQL : SELECT * FROM amendes WHERE statut = 'IMPAYEE'
     */
    public List<Amende> findImpayees() throws SQLException {
        List<Amende> amendes = new ArrayList<>();
        String sql = "SELECT * FROM amendes WHERE statut = 'IMPAYEE'";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                amendes.add(construireAmende(rs));
            }
        }
        return amendes;
    }

    /**
     * Rcupre les amendes impayes d'un membre spcifique.
     * SQL : SELECT * FROM amendes WHERE membre_id = ? AND statut = 'IMPAYEE'
     */
    public List<Amende> findImpayeesByMembre(int membreId) throws SQLException {
        List<Amende> amendes = new ArrayList<>();
        String sql = "SELECT * FROM amendes WHERE membre_id = ? AND statut = 'IMPAYEE'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, membreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                amendes.add(construireAmende(rs));
            }
        }
        return amendes;
    }

    /**
     * Marque une amende comme paye.
     * SQL : UPDATE amendes SET statut = 'PAYEE' WHERE id = ?
     */
    public void markAsPaid(int amendeId) throws SQLException {
        String sql = "UPDATE amendes SET statut = 'PAYEE' WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, amendeId);
            stmt.executeUpdate();
            System.out.println("Amende #" + amendeId + " marquee comme payee.");
        }
    }

    /**
     * Construit un objet Amende depuis une ligne SQL.
     */
    private Amende construireAmende(ResultSet rs) throws SQLException {
        try {
            Membre membre = new Membre(rs.getInt("membre_id"), "?", "?", "placeholder@mail.com");
            Livre livreTemp = new Livre("Titre inconnu", "Auteur inconnu", "978-0000000001", 2020, "?");
            Exemplaire exemplaire = new Exemplaire(0, livreTemp, EtatExemplaire.DISPONIBLE, java.time.LocalDate.now());
            Emprunt emprunt = new Emprunt(rs.getInt("emprunt_id"), membre, exemplaire);

            Amende amende = new Amende(
                rs.getInt   ("id"),
                membre,
                emprunt,
                BigDecimal.valueOf(rs.getDouble("montant"))
            );
            amende.setStatut(Amende.StatutAmende.valueOf(rs.getString("statut")));
            return amende;

        } catch (BibliothequeException e) {
            throw new SQLException("Erreur construction Amende : " + e.getMessage());
        }
    }
}
