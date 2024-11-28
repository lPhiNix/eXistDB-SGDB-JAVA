package org.phinix.example;

import org.phinix.example.model.Book;
import org.phinix.example.model.Poem;
import org.phinix.lib.common.util.XQueryFactory;
import org.phinix.lib.dao.XQueryDao;
import org.phinix.lib.service.ExistDB;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        // Instanciar las clases
        ExistDB existDB = ExistDB.getInstance("admin", "admin");
        XQueryDao dao = new XQueryDao(existDB);

        String pathCollection = "/db/bookshop/poems";

        // Construir la consulta XQuery
        String query = XQueryFactory.buildQuery(pathCollection);

        // Ejecutar la consulta y mapear los resultados
        List<Poem> poems = dao.executeQuery(query, pathCollection, Poem.class);

        // Imprimir los resultados
        for (Poem poem : poems) {
            System.out.println(poem);
        }
    }
}





