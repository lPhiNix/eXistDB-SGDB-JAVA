package org.phinix.lib.service;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

import java.util.logging.Logger;

public class ExistDB {
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static final Logger logger = Logger.getLogger(ExistDB.class.getName());
    private static volatile ExistDB instance;

    private final Database database;
    private final String user;
    private final String password;

    private ExistDB(String user, String password) throws Exception {
        this.user = user;
        this.password = password;

        if (user == null || user.isEmpty()) {
            throw new IllegalArgumentException("User must not be null or empty");
        }

        String driver = "org.exist.xmldb.DatabaseImpl";
        Class<?> client = Class.forName(driver);
        database = (Database) client.getDeclaredConstructor().newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        try (Collection rootCollection = DatabaseManager.getCollection(URI + "/db", user, password)) {
            if (rootCollection != null) {
                logger.info("Successfully Connected To eXist-db.");
            } else {
                logger.warning("Connection To eXist-db Has Failed.");
                throw new RuntimeException("Connection to eXist-db has failed.");
            }
        } catch (XMLDBException e) {
            logger.warning("User Unauthorized!");
            throw e;
        }
    }

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

    public static void resetInstance() {
        synchronized (ExistDB.class) {
            instance = null;
        }
    }

    public boolean testConnection() {
        try (Collection testCollection = DatabaseManager.getCollection(URI + "/db", user, password)) {
            return testCollection != null;
        } catch (XMLDBException e) {
            logger.warning("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    public Collection getCollection(String path) throws XMLDBException {
        return DatabaseManager.getCollection(URI + path, user, password);
    }

    public void shutdown() throws XMLDBException {
        logger.info("Shutting Down eXist-db Connection.");
        if (database != null) {
            database.setProperty("create-database", "false");
            DatabaseManager.deregisterDatabase(database);
        }
    }
}
