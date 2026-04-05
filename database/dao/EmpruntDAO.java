package database.dao;

import membres.Emprunt;
import membres.Membre;
import catalogue.Exemplaire;
import catalogue.EtatExemplaire;
import catalogue.Livre;
import database.DatabaseManager;
import exceptions.BibliothequeException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la persistance des emprunts.
 * Plus complexe que LivreDAO car un emprunt relie un membre ET un exemplaire.
 */
public class EmpruntDAO implements DAO<Emprunt> {

    private Connection connection;

    public EmpruntDAO() throws SQLException {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Sauvegarde un emprunt en base.
     * SQL : INSERT INTO emprunts (...) VALUES (?, ?, ?, ?, ?, ?, ?)
     */
    @Override
    public void save(Emprunt emprunt) throws SQLException {
        String sql = "INSERT INTO emprunts " +
                     "(membre_id, exemplaire_id, date_emprunt, date_retour_prevue, date_retour_reelle, statut) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt   (1, emprunt.getMembre().getId());
            stmt.setInt   (2, emprunt.getExemplaire().getId());
            stmt.setString(3, emprunt.getDateEmprunt().toString());
            stmt.setString(4, emprunt.getDateRetourPrevue().toString());
            // dateRetourReelle peut tre null si le livre n'est pas encore rendu
            stmt.setString(5, emprunt.getDateRetourReelle() != null
                              ? emprunt.getDateRetourReelle().toString()
                              : null);
            stmt.setString(6, emprunt.getStatut().toString());
            stmt.executeUpdate();
            System.out.println("Emprunt sauvegarde : " + emprunt);
        }
    }

    /**
     * Rcupre un emprunt par son ID.
     * SQL : SELECT * FROM emprunts WHERE id = ?
     */
    @Override
    public Emprunt findById(int id) throws SQLException {
        String sql = "SELECT * FROM emprunts WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construireEmprunt(rs);
            }
        }
        return null;
    }

    /**
     * Rcupre tous les emprunts.
     */
    @Override
    public List<Emprunt> findAll() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                emprunts.add(construireEmprunt(rs));
            }
        }
        return emprunts;
    }

    /**
     * Met  jour le statut et la date de retour relle d'un emprunt.
     * Appel quand un membre rend un livre.
     */
    @Override
    public void update(Emprunt emprunt) throws SQLException {
        String sql = "UPDATE emprunts SET statut = ?, date_retour_reelle = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emprunt.getStatut().toString());
            stmt.setString(2, emprunt.getDateRetourReelle() != null
                              ? emprunt.getDateRetourReelle().toString()
                              : null);
            stmt.setInt   (3, emprunt.getId());
            stmt.executeUpdate();
            System.out.println("Emprunt mis a jour (id=" + emprunt.getId() + ")");
        }
    }

    /**
     * Supprime un emprunt par son ID.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM emprunts WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Rcupre tous les emprunts d'un membre donn.
     * SQL : SELECT * FROM emprunts WHERE membre_id = ?
     */
    public List<Emprunt> findByMembre(int membreId) throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE membre_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, membreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(construireEmprunt(rs));
            }
        }
        return emprunts;
    }

    /**
     * Rcupre tous les emprunts en retard.
     * SQL : SELECT emprunts dont la date de retour prvue est dpasse et statut EN_COURS
     */
    public List<Emprunt> findEnRetard() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        // On compare la date du jour avec date_retour_prevue
        String sql = "SELECT * FROM emprunts WHERE statut = 'EN_COURS' " +
                     "AND date_retour_prevue < ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString()); // date d'aujourd'hui
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(construireEmprunt(rs));
            }
        }
        return emprunts;
    }

    /**
     * Construit un objet Emprunt depuis une ligne SQL.
     * On recre des objets Membre et Exemplaire "lgers" juste avec leur ID.
     */
    private Emprunt construireEmprunt(ResultSet rs) throws SQLException {
        try {
            // On cre des objets temporaires pour lier l'emprunt  son membre/exemplaire
            // Le Rle 2 pourra enrichir cela en utilisant MembreDAO et ExemplaireDAO
            Membre membre = new Membre(
                rs.getInt("membre_id"), "?", "?", "placeholder@mail.com"
            );
            Livre livreTemp = new Livre("Titre inconnu", "Auteur inconnu", "978-0000000001", 2020, "?");
            Exemplaire exemplaire = new Exemplaire(rs.getInt("exemplaire_id"), livreTemp, EtatExemplaire.DISPONIBLE, java.time.LocalDate.now());

            Emprunt emprunt = new Emprunt(rs.getInt("id"), membre, exemplaire);

            // Mise  jour du statut et de la date de retour relle si elle existe
            emprunt.setStatut(Emprunt.StatutEmprunt.valueOf(rs.getString("statut")));
            String dateReelle = rs.getString("date_retour_reelle");
            if (dateReelle != null) {
                emprunt.setDateRetourReelle(LocalDate.parse(dateReelle));
            }
            return emprunt;

        } catch (BibliothequeException e) {
            throw new SQLException("Erreur construction Emprunt : " + e.getMessage());
        }
    }
}
