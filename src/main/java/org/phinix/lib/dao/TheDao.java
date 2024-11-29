package org.phinix.lib.dao;

import org.phinix.lib.common.XMLFileUtil;
import org.phinix.lib.common.XMLSerializableNotFoundException;
import org.phinix.lib.service.ExistDB;
import org.w3c.dom.Document;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import java.io.File;
import java.util.logging.Logger;
import java.util.List;

/**
 * TheDao encapsulates CRUD (Create, Read, Update, Delete) operations
 * on an eXist-db database. It is modular and compatible with objects serialized to XML.
 */
public class TheDao implements Dao {
    private static final Logger logger = Logger.getLogger(TheDao.class.getName());
    private static final String DB_ROOT = "/db"; // We use /db as root, but the directory separator will be dynamic
    private final ExistDB existDB;

    /**
     * The constructor of TheDao which uses an instance of ExistDB.
     *
     * @param existDB the connection instance to eXist-db.
     */
    public TheDao(ExistDB existDB) {
        this.existDB = existDB;
    }

    /**
     * Creates a collection in the database if it does not exist.
     *
     * @param path            the path of the collection to create (e.g., "/db/books").
     * @throws XMLDBException if an error occurs during the database operation.
     */
    @Override
    public void createCollection(String path) throws XMLDBException {
        // Get the parent collection path to check if it exists before creating the new collection
        Collection parent = existDB.getCollection(getParentPath(path));

        if (parent == null) {
            // If the parent collection doesn't exist, log an error and throw an exception
            logger.severe("Parent collection does not exist: " + getParentPath(path));
            throw new XMLDBException();
        }

        // Check if the collection already exists
        Collection collection = existDB.getCollection(path);
        if (collection == null) {
            // If the collection doesn't exist, create it
            Collection root = existDB.getCollection(DB_ROOT);
            CollectionManagementService cms = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
            cms.createCollection(getCollectionName(path)); // Create the collection using the name extracted from the path
            logger.info("Collection created: " + path);
        } else {
            // If the collection exists, log a message
            logger.info("Collection already exists: " + path);
        }
    }

    /**
     * Adds an XML file to a collection using the XMLFileManager utility.
     *
     * @param collectionPath  the path of the collection.
     * @param fileName        the name of the file to save.
     * @param document        the XML document to add.
     * @throws XMLDBException if an error occurs when adding the file.
     */
    @Override
    public void addFileToCollection(String collectionPath, String fileName, Document document) throws XMLDBException, XMLSerializableNotFoundException {
        // Get the collection, or throw an exception if it doesn't exist
        Collection collection = getCollectionOrThrow(collectionPath);

        // Use XMLFileManager to save the document as an XML file
        try {
            String filePath = collectionPath + File.separator + fileName;
            XMLFileUtil.generateXmlFromObjects(List.of(document), filePath);
            logger.info("File added to collection: " + fileName);
        } catch (Exception e) {
            logger.severe("Error adding file to collection: " + fileName + " - " + e.getMessage());
            throw new XMLDBException();
        }
    }

    /**
     * Retrieves an XML file from a collection.
     *
     * @param collectionPath  the path of the collection.
     * @param fileName        the name of the file to retrieve.
     * @return                the content of the XML file as a string.
     * @throws XMLDBException if an error occurs when retrieving the file.
     */
    @Override
    public String getFileFromCollection(String collectionPath, String fileName) throws XMLDBException {
        // Get the collection, or throw an exception if it doesn't exist
        Collection collection = getCollectionOrThrow(collectionPath);

        // Retrieve the resource (file) from the collection
        XMLResource resource = (XMLResource) collection.getResource(fileName);

        if (resource == null) {
            // If the file is not found, log a warning and throw an exception
            logger.warning("File not found: " + fileName);
            throw new XMLDBException();
        }

        // Return the content of the XML file as a string
        return (String) resource.getContent();
    }

    /**
     * Deletes a file from a collection.
     *
     * @param collectionPath  the path of the collection.
     * @param fileName        the name of the file to delete.
     * @throws XMLDBException if an error occurs when deleting the file.
     */
    @Override
    public void deleteFileFromCollection(String collectionPath, String fileName) throws XMLDBException {
        // Get the collection, or throw an exception if it doesn't exist
        Collection collection = getCollectionOrThrow(collectionPath);

        // Retrieve the resource (file) to be deleted
        Resource resource = collection.getResource(fileName);

        if (resource != null) {
            // If the file exists, remove it from the collection
            collection.removeResource(resource);
            logger.info("File deleted: " + fileName);
        } else {
            // If the file is not found, log a warning
            logger.warning("File not found for deletion: " + fileName);
        }
    }

    /**
     * Updates an XML file in a collection using the XMLFileManager utility.
     *
     * @param collectionPath  the path of the collection.
     * @param fileName        the name of the file to update.
     * @param document        the new XML document.
     * @throws XMLDBException if an error occurs when updating the file.
     */
    @Override
    public void updateFileInCollection(String collectionPath, String fileName, Document document) throws XMLDBException {
        // Get the collection, or throw an exception if it doesn't exist
        Collection collection = getCollectionOrThrow(collectionPath);

        // Retrieve the resource (file) to be updated
        XMLResource resource = (XMLResource) collection.getResource(fileName);

        if (resource != null) {
            // If the file exists, set its content to the new XML document
            resource.setContentAsDOM(document);

            // Store the updated resource in the collection
            collection.storeResource(resource);
            logger.info("File updated: " + fileName);
        } else {
            // If the file is not found, log a warning and throw an exception
            logger.warning("File not found for update: " + fileName);
            throw new XMLDBException();
        }
    }

    /**
     * Checks if a file exists in the collection.
     *
     * @param collectionPath the path of the collection.
     * @param fileName       the name of the file.
     * @return               true if the file exists, false otherwise.
     */
    public boolean fileExistsInCollection(String collectionPath, String fileName) throws XMLDBException {
        // Get the collection, or throw an exception if it doesn't exist
        Collection collection = getCollectionOrThrow(collectionPath);

        // Retrieve the resource (file) from the collection
        XMLResource resource = (XMLResource) collection.getResource(fileName);

        // Return true if the resource exists, false otherwise
        return resource != null;
    }

    /**
     * Gets the parent path of a collection.
     *
     * @param path the path of the collection.
     * @return     the path of the parent collection.
     */
    private String getParentPath(String path) {
        // Find the last directory separator in the path and return the substring before it
        int lastSeparatorIndex = path.lastIndexOf(File.separator);
        return lastSeparatorIndex > 0 ? path.substring(0, lastSeparatorIndex) : DB_ROOT;
    }

    /**
     * Gets the name of the collection from the full path.
     *
     * @param path the path of the collection.
     * @return     the name of the collection.
     */
    private String getCollectionName(String path) {
        // Find the last directory separator in the path and return the substring after it as the collection name
        int lastSeparatorIndex = path.lastIndexOf(File.separator);
        return lastSeparatorIndex > 0 ? path.substring(lastSeparatorIndex + 1) : path;
    }

    /**
     * Gets a collection from the database or throws an exception if it does not exist.
     *
     * @param path            the path of the collection.
     * @return                the collection.
     * @throws XMLDBException if the collection does not exist.
     */
    private Collection getCollectionOrThrow(String path) throws XMLDBException {
        // Retrieve the collection from the database
        Collection collection = existDB.getCollection(path);

        // If the collection doesn't exist, log an error and throw an exception
        if (collection == null) {
            logger.severe("Collection not found: " + path);
            throw new XMLDBException();
        }

        // Return the found collection
        return collection;
    }
}