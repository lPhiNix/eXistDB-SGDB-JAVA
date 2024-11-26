package org.phinix.example;

import org.phinix.example.model.Book;
import org.phinix.lib.common.util.XMLFileManager;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;
import org.phinix.lib.service.ExistDB;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import java.util.Collections;

public class Test {
    public static void main(String[] args) {
        try {
            String user = "admin";
            String password = "admin";
            ExistDB existDB = ExistDB.getInstance(user, password);

            // Crear un objeto Book
            Book book = new Book("1984", "George Orwell", "1949");

            // Generar XML a partir del objeto
            String filePath = "book.xml";
            XMLFileManager.generateXmlFromObjects(Collections.singletonList(book), filePath);

            // Verificar o crear la colección
            String collectionPath = "/db/books";
            Collection collection = existDB.getCollection(collectionPath);
            if (collection == null) {
                Collection root = existDB.getCollection("/db");
                CollectionManagementService cms = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
                collection = cms.createCollection("books");
            }

            // Subir el archivo XML a la colección
            if (collection != null) {
                XMLResource resource = (XMLResource) collection.createResource("book.xml", XMLResource.RESOURCE_TYPE);
                resource.setContent(new java.io.File(filePath));
                collection.storeResource(resource);
                System.out.println("El libro ha sido añadido a la base de datos.");
            } else {
                System.err.println("No se pudo acceder o crear la colección.");
            }

        } catch (Exception | XMLSerializableNotFoundException e) {
            e.printStackTrace();
        }
    }
}


