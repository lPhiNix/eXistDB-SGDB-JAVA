package org.phinix.lib.dao;

import org.phinix.lib.common.util.XMLSerializableNotFoundException;
import org.w3c.dom.Document;
import org.xmldb.api.base.XMLDBException;

public interface Dao {
    void createCollection(String path) throws XMLDBException;
    void addFileToCollection(String collectionPath, String fileName, Document document) throws XMLDBException, XMLSerializableNotFoundException;
    String getFileFromCollection(String collectionPath, String fileName) throws XMLDBException;
    void deleteFileFromCollection(String collectionPath, String fileName) throws XMLDBException;
    void updateFileInCollection(String collectionPath, String fileName, Document document) throws XMLDBException;
}
