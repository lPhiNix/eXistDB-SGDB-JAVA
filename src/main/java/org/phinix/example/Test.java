package org.phinix.example;

import org.phinix.example.model.Book;
import org.phinix.example.model.Poem;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;
import org.phinix.lib.common.util.XQueryFactory;
import org.phinix.lib.dao.XQueryDao;
import org.phinix.lib.service.ExistDB;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception, XMLSerializableNotFoundException {
        // Instanciar las clases
        ExistDB existDB = ExistDB.getInstance("admin", "admin");
        XQueryDao dao = new XQueryDao(existDB);

        String pathCollection = "/db/bookshop/novels";

        String query = XQueryFactory.buildQuery(pathCollection, Book.class, "year < 1950");

        System.out.println(query);

        // Ejecutar la consulta y mapear los resultados
        List<Book> poems = dao.executeQuery(query, pathCollection, Book.class);

        // Imprimir los resultados
        for (Book book : poems) {
            System.out.println(book);
        }
    }
}





