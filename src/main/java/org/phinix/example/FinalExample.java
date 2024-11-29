package org.phinix.example;

import org.phinix.example.model.Essay;
import org.phinix.lib.common.util.XMLFileUtil;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;
import org.phinix.lib.common.util.XQueryFactory;
import org.phinix.lib.dao.XQueryDao;
import org.phinix.lib.service.ExistDB;

import java.util.ArrayList;
import java.util.List;

public class FinalExample {
    public static void main(String[] args) throws XMLSerializableNotFoundException, Exception {
        // essays.xml creation
        List<Essay> essays = new ArrayList<>();

        essays.add(new Essay("The Revolt of the Masses", "José Ortega y Gasset", 1930));
        essays.add(new Essay("The Art of War", "Sun Tzu", -500));

        XMLFileUtil.generateXmlFromObjects(essays, "essays.xml");

        // Add XML manually

        // example query
        ExistDB existDB = ExistDB.getInstance("admin", "admin");
        XQueryDao dao = new XQueryDao(existDB);

        String pathCollection = "/db/bookshop/essays";

        String query = XQueryFactory.buildQuery(pathCollection, Essay.class, "year < 0");

        List<Essay> essaysWithYearLessTo0 = dao.executeQuery(query, pathCollection, Essay.class);

        for (Essay essay : essaysWithYearLessTo0) {
            System.out.println(essay);
        }
    }
}
