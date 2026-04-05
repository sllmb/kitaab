package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe Singleton grant la connexion  la base de donnes SQLite.
 *
 * Singleton = une seule instance de cette classe existe dans tout le programme.
 * SQLite = base de donnes stocke dans un fichier local (bibliotheque.db)
 * Pas besoin d'installer un serveur  juste ajouter sqlite-jdbc.jar au projet.
 */
public class DatabaseManager {

    // L'unique instance de cette classe (Singleton)
    private static DatabaseManager instance = null;

    // URL du fichier de base de donnes (cr automatiquement s'il n'existe pas)
    private static final String URL = "jdbc:sqlite:bibliotheque.db";

    // La connexion active vers la base de donnes
    private Connection connection;

    // Constructeur PRIV  empche de faire "new DatabaseManager()" depuis l'extrieur
    private DatabaseManager() throws SQLException {
        this.connection = DriverManager.getConnection(URL);
        System.out.println("Connexion a la base de donnees etablie : bibliotheque.db");
        creerTables();
    }

    /**
     * Retourne l'unique instance du DatabaseManager.
     * Si elle n'existe pas encore, on la cre.
     */
    public static DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Cre toutes les tables de la base de donnes au dmarrage.
     * "IF NOT EXISTS" = pas d'erreur si les tables existent dj.
     */
    private void creerTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Table livres
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS livres (" +
                "    id      INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    titre   TEXT NOT NULL," +
                "    auteur  TEXT NOT NULL," +
                "    isbn    TEXT UNIQUE NOT NULL," +
                "    annee   INTEGER," +
                "    genre   TEXT" +
                ")"
            );

            // Table membres
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS membres (" +
                "    id               INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    nom              TEXT NOT NULL," +
                "    prenom           TEXT NOT NULL," +
                "    email            TEXT UNIQUE NOT NULL," +
                "    date_inscription TEXT NOT NULL" +
                ")"
            );

            // Table exemplaires (chaque copie physique d'un livre)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS exemplaires (" +
                "    id         INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    livre_id   INTEGER NOT NULL," +
                "    etat       TEXT NOT NULL DEFAULT 'DISPONIBLE'," +
                "    date_ajout TEXT NOT NULL," +
                "    FOREIGN KEY (livre_id) REFERENCES livres(id)" +
                ")"
            );

            // Table emprunts
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS emprunts (" +
                "    id                 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    membre_id          INTEGER NOT NULL," +
                "    exemplaire_id      INTEGER NOT NULL," +
                "    date_emprunt       TEXT NOT NULL," +
                "    date_retour_prevue TEXT NOT NULL," +
                "    date_retour_reelle TEXT," +
                "    statut             TEXT NOT NULL DEFAULT 'EN_COURS'," +
                "    FOREIGN KEY (membre_id)     REFERENCES membres(id)," +
                "    FOREIGN KEY (exemplaire_id) REFERENCES exemplaires(id)" +
                ")"
            );

            // Table amendes
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS amendes (" +
                "    id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    membre_id     INTEGER NOT NULL," +
                "    emprunt_id    INTEGER NOT NULL," +
                "    montant       REAL NOT NULL," +
                "    date_creation TEXT NOT NULL," +
                "    statut        TEXT NOT NULL DEFAULT 'IMPAYEE'," +
                "    FOREIGN KEY (membre_id)  REFERENCES membres(id)," +
                "    FOREIGN KEY (emprunt_id) REFERENCES emprunts(id)" +
                ")"
            );

            // Index pour acclrer les recherches frquentes
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_isbn   ON livres(isbn)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_email  ON membres(email)");

            System.out.println("Tables creees avec succes.");
        }
    }

    /** Retourne la connexion active */
    public Connection getConnection() {
        return connection;
    }

    /** Ferme proprement la connexion */
    public void fermer() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            instance = null;
            System.out.println("Connexion fermee.");
        }
    }
}
