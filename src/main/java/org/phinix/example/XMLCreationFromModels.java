package org.phinix.example;

import org.phinix.example.model.Book;
import org.phinix.example.model.Poem;
import org.phinix.lib.common.util.XMLFileManager;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class XMLCreationFromModels {
    public static void main(String[] args) throws XMLSerializableNotFoundException, Exception {
        List<Book> books = new ArrayList<>();

        books.add(new Book("Don Quijote", "Miguel de Cervantes", 1605));
        books.add(new Book("1984", "George Orwell", 1949));

        XMLFileManager.generateXmlFromObjects(books, "novels.xml", "library");

        List<Poem> poems = new ArrayList<>();

        poems.add(new Poem("Rima I", "Gustavo Adolfo Bécquer"));
        poems.add(new Poem("Altazor", "Vicente Huidobro"));

        XMLFileManager.generateXmlFromObjects(poems, "poems.xml", "poetry_collection");
    }
}


