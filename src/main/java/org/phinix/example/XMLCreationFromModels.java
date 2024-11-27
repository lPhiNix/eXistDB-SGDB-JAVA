package org.phinix.example;

import org.phinix.example.model.Book;
import org.phinix.example.model.Poem;
import org.phinix.lib.common.util.XMLFileManager;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The XMLCreationFromModels class demonstrates the use of the XMLFileManager utility
 * to convert lists of model objects (Book and Poem) into XML files.</p>
 *
 * It shows how to:
 * - Create lists of objects.
 * - Serialize those objects to XML using the XMLFileManager.
 * - Specify custom root element tags for the generated XML files.
 */
public class XMLCreationFromModels {
    public static void main(String[] args) throws XMLSerializableNotFoundException, Exception {
        // Create a list of Book objects
        List<Book> books = new ArrayList<>();

        // Add books to the list
        books.add(new Book("Don Quijote", "Miguel de Cervantes", 1605)); // Add "Don Quijote" to the list
        books.add(new Book("1984", "George Orwell", 1949)); // Add "1984" to the list

        // Generate an XML file for the books, with a custom root element <library>
        XMLFileManager.generateXmlFromObjects(books, "novels.xml", "library");

        // Create a list of Poem objects
        List<Poem> poems = new ArrayList<>();

        // Add poems to the list
        poems.add(new Poem("Rima I", "Gustavo Adolfo Bécquer")); // Add "Rima I" to the list
        poems.add(new Poem("Altazor", "Vicente Huidobro")); // Add "Altazor" to the list

        // Generate an XML file for the poems, with a custom root element <poetry_collection>
        XMLFileManager.generateXmlFromObjects(poems, "poems.xml", "poetry_collection");
    }
}
