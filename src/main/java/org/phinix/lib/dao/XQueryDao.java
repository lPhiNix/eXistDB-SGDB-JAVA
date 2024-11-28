package org.phinix.lib.dao;

import org.phinix.lib.common.util.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.phinix.lib.service.ExistDB;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * XQueryExecutor es una clase responsable de ejecutar consultas XQuery sobre una base de datos eXist-db.
 * También se encarga de mapear los resultados de las consultas a objetos.
 */
public class XQueryDao {
    private static final Logger logger = Logger.getLogger(XQueryDao.class.getName());
    private final ExistDB existDB;

    /**
     * Constructor que inicializa la clase con una instancia de ExistDB.
     *
     * @param existDB La instancia de ExistDB que se utilizará para interactuar con la base de datos.
     */
    public XQueryDao(ExistDB existDB) {
        this.existDB = existDB;
    }

    /**
     * Ejecuta una consulta XQuery en eXist-db, mapea los resultados y los devuelve como una lista de objetos.
     *
     * @param query           La cadena XQuery que se ejecutará.
     * @param collectionPath  El path de la colección en la base de datos.
     * @param clazz           La clase a la que mapear los resultados.
     * @param <T>             El tipo de objeto a devolver.
     * @return Una lista de objetos mapeados desde los resultados de la consulta.
     */
    public <T> List<T> executeQuery(String query, String collectionPath, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        try {
            ResourceSet resourceSet = executeRawQuery(query, collectionPath);  // Ejecutar la consulta

            if (resourceSet == null) {
                return results;
            }

            // Mapear los resultados a objetos
            for (int i = 0; i < resourceSet.getSize(); i++) {
                Resource resource = resourceSet.getResource(i);
                String content = (String) resource.getContent();

                Document doc = parseXMLContent(content);  // Parsear el XML a un objeto DOM

                List<T> objList = mapToObject(clazz, doc);  // Mapear el XML a una lista de objetos de tipo T
                results.addAll(objList);  // Agregar todos los objetos mapeados a la lista de resultados
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error mapping results to class: " + clazz.getSimpleName(), e);
        }
        return results;
    }

    /**
     * Ejecuta una consulta XQuery en eXist-db y obtiene el conjunto de resultados.
     *
     * @param query           La cadena XQuery que se ejecutará.
     * @param collectionPath  El path de la colección en la base de datos.
     * @return El conjunto de recursos obtenido como resultado de la consulta.
     */
    private ResourceSet executeRawQuery(String query, String collectionPath) {
        try {
            Collection collection = existDB.getCollection(collectionPath);
            if (collection == null) {
                throw new RuntimeException("Failed to retrieve the collection.");
            }

            XPathQueryService queryService = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

            return queryService.query(query);
        } catch (XMLDBException e) {
            logger.log(Level.SEVERE, "Error executing query: " + query, e);
            return null;
        }
    }

    /**
     * Convierte una cadena XML en un objeto DOM.
     *
     * @param content El contenido XML.
     * @return El objeto Document parseado.
     * @throws Exception Si ocurre un error durante el parseo.
     */
    private Document parseXMLContent(String content) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            // Imprimir el contenido para ver si el XML tiene errores
            System.out.println("Error parsing XML: " + content);
            throw new Exception("Error parsing XML", e);  // Re-lanzar para obtener el stack trace
        }
    }

    /**
     * Mapea un objeto DOM a una instancia de la clase proporcionada.
     *
     * @param clazz La clase a la que se mapeará el contenido.
     * @param doc   El documento XML que contiene los datos.
     * @param <T>   El tipo de la clase a devolver.
     * @return La instancia de la clase mapeada.
     * @throws Exception Si ocurre un error durante el mapeo.
     */
    private <T> List<T> mapToObject(Class<T> clazz, Document doc) throws Exception {
        List<T> objList = new ArrayList<>();

        // Obtener todos los elementos <book>
        NodeList bookNodes = doc.getElementsByTagName(XMLFileManager.getObjectTagName(clazz));

        for (int i = 0; i < bookNodes.getLength(); i++) {
            Element bookElement = (Element) bookNodes.item(i);
            T obj = clazz.getDeclaredConstructor().newInstance();  // Crear una nueva instancia de Book

            // Para cada campo de la clase, buscar el nodo correspondiente en el XML
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                // Buscar el nodo correspondiente al campo dentro de cada <book>
                NodeList nodes = bookElement.getElementsByTagName(field.getName());

                if (nodes.getLength() > 0) {
                    Element element = (Element) nodes.item(0); // Solo tomamos el primer nodo del campo
                    String value = element.getTextContent();

                    // Convertir el valor al tipo adecuado según el tipo de campo
                    if (field.getType().equals(int.class)) {
                        field.set(obj, Integer.parseInt(value)); // Si es un int
                    } else {
                        field.set(obj, value); // Si es un String
                    }
                }

                field.setAccessible(false);
            }

            objList.add(obj); // Agregar el objeto creado a la lista
        }

        return objList; // Devolver la lista de objetos mapeados
    }
}
