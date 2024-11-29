package org.phinix.example;

import org.phinix.example.model.Book;
import org.phinix.example.model.Poem;
import org.phinix.lib.common.XMLSerializableNotFoundException;
import org.phinix.lib.common.XQueryFactory;
import org.phinix.lib.dao.XQueryDao;
import org.phinix.lib.service.ExistDB;

import java.util.List;

/**
 * <p>The BuildAndExecutionXQueries class demonstrates the use of the XQueryFactory and XQueryDao utility
 * to build, manage and execute queries in eXist-DB database.</p>
 *
 * <p>
 * It shows how to:
 * - Database conection.
 * - Build queries.
 * - Execution remote queries, data mapping and get result queries.
 * </p>
 */
public class BuildAndExecutionXQueries {
    public static void main(String[] args) throws Exception, XMLSerializableNotFoundException {
        ExistDB existDB = ExistDB.getInstance("admin", "admin");
        XQueryDao dao = new XQueryDao(existDB);

        String novelsPath = "/db/bookshop/novels";
        String poemsPath = "/db/bookshop/poems";

        // Query 1
        System.out.println("Query 1");

        String query1 = XQueryFactory.buildQuery(novelsPath, Book.class);

        List<Book> allBooksInNovels = dao.executeQuery(query1, novelsPath, Book.class);

        for (Book book : allBooksInNovels) {
            System.out.println(book.getTitle());
        }

        // Query2
        String query2 = XQueryFactory.buildQuery(poemsPath, Poem.class);

        List<Poem> allPoemsInPoems = dao.executeQuery(query2, poemsPath, Poem.class);

        for (Poem poem : allPoemsInPoems) {
            System.out.println(poem.getTitle());
        }

        // Query 3
        String query3 = XQueryFactory.buildQuery(novelsPath, Book.class, "year < 1950");

        List<Book> booksWithYearLess1950 = dao.executeQuery(query3, novelsPath, Book.class);

        for (Book book : booksWithYearLess1950) {
            System.out.println(book.getAuthor());
        }
    }
}
