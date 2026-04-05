package database.dao;

import catalogue.Exemplaire;
import catalogue.EtatExemplaire;
import catalogue.Livre;
import database.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la persistance des exemplaires.
 * ROLE 4  Implment.
 */
public class ExemplaireDAO implements DAO<Exemplaire> {

    private Connection connection;

    public ExemplaireDAO() throws SQLException {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void save(Exemplaire exemplaire) throws SQLException {
        String sql = "INSERT INTO exemplaires (id, livre_id, etat, date_ajout) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplaire.getId());
            stmt.setInt(2, exemplaire.getLivre().getId());
            stmt.setString(3, exemplaire.getEtat().name());
            stmt.setDate(4, Date.valueOf(exemplaire.getDateAjout()));
            stmt.executeUpdate();
        }
    }

    @Override
    public Exemplaire findById(int id) throws SQLException {
        String sql = "SELECT * FROM exemplaires WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Exemplaire> findAll() throws SQLException {
        List<Exemplaire> exemplaires = new ArrayList<>();
        String sql = "SELECT * FROM exemplaires";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                exemplaires.add(mapResultSet(rs));
            }
        }
        return exemplaires;
    }

    @Override
    public void update(Exemplaire exemplaire) throws SQLException {
        String sql = "UPDATE exemplaires SET livre_id = ?, etat = ?, date_ajout = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplaire.getLivre().getId());
            stmt.setString(2, exemplaire.getEtat().name());
            stmt.setDate(3, Date.valueOf(exemplaire.getDateAjout()));
            stmt.setInt(4, exemplaire.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM exemplaires WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Retourne tous les exemplaires disponibles d'un livre donn.
     */
    public List<Exemplaire> findDisponiblesByLivre(int livreId) throws SQLException {
        List<Exemplaire> exemplaires = new ArrayList<>();
        String sql = "SELECT * FROM exemplaires WHERE livre_id = ? AND etat = 'DISPONIBLE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, livreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    exemplaires.add(mapResultSet(rs));
                }
            }
        }
        return exemplaires;
    }

    //  Mthode utilitaire 

    private Exemplaire mapResultSet(ResultSet rs) throws SQLException {
        int livreId = rs.getInt("livre_id");
        LivreDAO livreDAO = new LivreDAO();
        Livre livre = livreDAO.findById(livreId);
        return new Exemplaire(
            rs.getInt("id"),
            livre,
            EtatExemplaire.valueOf(rs.getString("etat")),
            java.time.LocalDate.parse(rs.getString("date_ajout"))
        );
    }
}
