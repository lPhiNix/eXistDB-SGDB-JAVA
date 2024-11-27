package org.phinix.lib.service;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ExistDB} class provides a singleton utility for managing connections
 * to an eXist-db database instance. It handles initialization, testing connections,
 * retrieving collections, and shutting down the database driver.
 *
 * <p>This class ensures that only one instance of the database connection is created
 * and reused throughout the application.</p>
 */
public class ExistDB {
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc"; // URI for connecting to the eXist-db server
    private static final Logger logger = Logger.getLogger(ExistDB.class.getName());
    private static volatile ExistDB instance;

    private final Database database;
    private final String user;
    private final String password;

    /**
     * Private constructor to initialize the eXist-db connection.
     *
     * @param user       the username for authentication.
     * @param password   the password for authentication.
     * @throws Exception if initialization or connection to the database fails.
     */
    private ExistDB(String user, String password) throws Exception {
        validateCredentials(user, password); // Validate user credentials

        this.user = user;
        this.password = password;

        // Load and initialize the eXist-db driver
        String driver = "org.exist.xmldb.DatabaseImpl";
        try {
            Class<?> client = Class.forName(driver);
            this.database = (Database) client.getDeclaredConstructor().newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing eXist-db database driver.", e);
            throw new RuntimeException("Failed to initialize eXist-db driver.", e);
        }

        testRootCollection(); // Verify the root collection is accessible
    }

    /**
     * Validates the provided user credentials.
     *
     * @param user                      the username to validate.
     * @param password                  the password to validate.
     * @throws IllegalArgumentException if either the username or password is null or empty.
     */
    private void validateCredentials(String user, String password) {
        if (user == null || user.isEmpty()) {
            throw new IllegalArgumentException("User must not be null or empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
    }

    /**
     * Tests the connection to the root collection of the eXist-db instance.
     *
     * @throws XMLDBException if the connection fails or the user is unauthorized.
     */
    private void testRootCollection() throws XMLDBException {
        try (Collection rootCollection = DatabaseManager.getCollection(URI + "/db", user, password)) {
            if (rootCollection != null) {
                logger.info("Successfully connected to eXist-db.");
            } else {
                logger.warning("Connection to eXist-db failed. Root collection is null.");
                throw new RuntimeException("Connection to eXist-db failed.");
            }
        } catch (XMLDBException e) {
            logger.log(Level.SEVERE, "Failed to connect to root collection. User unauthorized.", e);
            throw e;
        }
    }

    /**
     * Retrieves the singleton instance of the {@code ExistDB}.
     *
     * @param user       the username for authentication.
     * @param password   the password for authentication.
     * @return           the singleton instance of {@code ExistDB}.
     * @throws Exception if initialization of the instance fails.
     */
    public static ExistDB getInstance(String user, String password) throws Exception {
        if (instance == null) {
            synchronized (ExistDB.class) {
                if (instance == null) {
                    instance = new ExistDB(user, password);
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance, allowing a new instance to be created.
     */
    public static void resetInstance() {
        synchronized (ExistDB.class) {
            instance = null;
            logger.info("eXist-db instance has been reset.");
        }
    }

    /**
     * Tests the connection to the eXist-db instance by accessing the root collection.
     *
     * @return {@code true} if the connection is successful, {@code false} otherwise.
     */
    public boolean testConnection() {
        try (Collection testCollection = DatabaseManager.getCollection(URI + "/db", user, password)) {
            boolean success = testCollection != null;
            logger.info("Connection test " + (success ? "succeeded." : "failed."));
            return success;
        } catch (XMLDBException e) {
            logger.log(Level.WARNING, "Connection test failed.", e);
            return false;
        }
    }

    /**
     * Retrieves a collection from the eXist-db database.
     *
     * @param path                      the path to the collection in the database.
     * @return                          the {@code Collection} instance.
     * @throws XMLDBException           if the collection retrieval fails.
     * @throws IllegalArgumentException if the path is null or empty.
     */
    public Collection getCollection(String path) throws XMLDBException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection path must not be null or empty.");
        }
        try {
            return DatabaseManager.getCollection(URI + path, user, password);
        } catch (XMLDBException e) {
            logger.log(Level.SEVERE, "Failed to retrieve collection at path: " + path, e);
            throw e;
        }
    }

    /**
     * Shuts down the eXist-db connection and deregisters the database driver.
     */
    public void shutdown() {
        synchronized (this) {
            try {
                if (database != null) {
                    database.setProperty("create-database", "false");
                    DatabaseManager.deregisterDatabase(database);
                    logger.info("eXist-db connection has been shut down.");
                }
            } catch (XMLDBException e) {
                logger.log(Level.WARNING, "Error during eXist-db shutdown.", e);
            }
        }
    }
}
