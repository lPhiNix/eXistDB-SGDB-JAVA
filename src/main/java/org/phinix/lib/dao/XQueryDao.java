package org.phinix.lib.dao;

import org.phinix.lib.common.util.XMLSerializableModel;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;
import org.phinix.lib.common.util.XMLFileUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.phinix.lib.service.ExistDB;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * XQueryDao is a class responsible for executing XQuery queries over an eXist-db database.
 * It also maps the results of these queries to Java objects.
 */
public class XQueryDao {
    private static final Logger logger = Logger.getLogger(XQueryDao.class.getName());
    private final ExistDB existDB;

    /**
     * Constructor that initializes the class with an instance of ExistDB.
     *
     * @param existDB The ExistDB instance used to interact with the database.
     */
    public XQueryDao(ExistDB existDB) {
        this.existDB = existDB;
    }

    /**
     * Executes an XQuery query on eXist-db, maps the results, and returns them as a list of objects.
     *
     * @param query           The XQuery string to execute.
     * @param collectionPath  The path of the collection in the database.
     * @param clazz           The class to map the results to.
     * @param <T>             The type of object to return.
     * @return A list of objects mapped from the query results.
     * @throws XMLSerializableNotFoundException if the class is not annotated with @XMLSerializableModel
     */
    public <T> List<T> executeQuery(String query, String collectionPath, Class<T> clazz) throws XMLSerializableNotFoundException {
        // Check if the class is annotated with @XMLSerializableModel
        if (!XMLFileUtil.isXMLSerializable(clazz)) {
            throw new XMLSerializableNotFoundException();
        }

        List<T> results = new ArrayList<>();
        try {
            // Execute the raw XQuery and retrieve the results
            ResourceSet resourceSet = executeRawQuery(query, collectionPath);

            if (resourceSet != null) {
                // Iterate through the result set and process each resource
                for (int i = 0; i < resourceSet.getSize(); i++) {
                    Resource resource = resourceSet.getResource(i);
                    String content = (String) resource.getContent();

                    // Parse the XML content into a Document object
                    Document doc = parseXMLContent(content);

                    // Map the XML document to a list of objects of type T
                    List<T> objList = mapToObjects(clazz, doc);

                    // Add the mapped objects to the results list
                    results.addAll(objList);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error mapping results to class: " + clazz.getSimpleName(), e);
        }
        return results;
    }

    /**
     * Executes a raw XQuery query on eXist-db and returns the result set.
     *
     * @param query           The XQuery string to execute.
     * @param collectionPath  The path of the collection in the database.
     * @return The resource set obtained as the result of the query.
     */
    private ResourceSet executeRawQuery(String query, String collectionPath) {
        // Retrieve the collection from the database
        Collection collection = getCollection(collectionPath);
        if (collection == null) {
            return null;
        }

        try {
            // Get the XPathQueryService to execute the XQuery
            XPathQueryService queryService = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

            // Execute the query and return the result set
            return queryService.query(query);
        } catch (XMLDBException e) {
            logger.log(Level.SEVERE, "Error executing query: " + query, e);
            return null;
        }
    }

    /**
     * Retrieves a collection from the eXist-db database.
     *
     * @param collectionPath The path of the collection.
     * @return The requested collection.
     */
    private Collection getCollection(String collectionPath) {
        try {
            // Attempt to retrieve the collection from the database
            Collection collection = existDB.getCollection(collectionPath);

            // If collection retrieval fails, throw an exception
            if (collection == null) {
                throw new RuntimeException("Failed to retrieve the collection.");
            }
            return collection;
        } catch (XMLDBException e) {
            logger.log(Level.SEVERE, "Error retrieving collection: " + collectionPath, e);
            return null;
        }
    }

    /**
     * Converts an XML string into a DOM Document object.
     *
     * @param content The XML content.
     * @return The parsed Document object.
     * @throws Exception If an error occurs during parsing.
     */
    private Document parseXMLContent(String content) throws Exception {
        try {
            // Create a DocumentBuilderFactory and DocumentBuilder for parsing the XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML content and return the resulting Document
            return builder.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing XML: " + content, e);
            throw new Exception("Error parsing XML", e);
        }
    }

    /**
     * Maps the content of an XML document to a list of objects of the specified class.
     *
     * @param clazz The class to which the objects will be mapped.
     * @param doc   The XML document containing the data.
     * @param <T>   The type of the class to return.
     * @return A list of objects mapped from the XML document.
     * @throws Exception If an error occurs during mapping.
     */
    private <T> List<T> mapToObjects(Class<T> clazz, Document doc) throws Exception {
        List<T> objList = new ArrayList<>();

        // Get all elements with the tag name corresponding to the class name
        NodeList bookNodes = doc.getElementsByTagName(XMLFileUtil.getObjectTagName(clazz));

        // Iterate over the nodes and map each one to an object
        for (int i = 0; i < bookNodes.getLength(); i++) {
            Element bookElement = (Element) bookNodes.item(i);
            T obj = clazz.getDeclaredConstructor().newInstance();  // Create a new instance of the class

            // Map the fields of the object from the XML element
            mapFieldsToObject(clazz, obj, bookElement);

            // Add the mapped object to the list
            objList.add(obj);
        }

        return objList;  // Return the list of mapped objects
    }

    /**
     * Maps the fields of an object from the XML element's content.
     *
     * @param clazz   The class to map the fields to.
     * @param obj     The object to populate with the field values.
     * @param element The XML element containing the data for the object.
     * @param <T>     The type of the class to map the fields to.
     * @throws Exception If an error occurs during the field mapping.
     */
    private <T> void mapFieldsToObject(Class<T> clazz, T obj, Element element) throws Exception {
        // Iterate through each declared field of the class
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);  // Allow access to private fields

            // Retrieve the elements that correspond to the field name
            NodeList nodes = element.getElementsByTagName(field.getName());

            // If the element exists in the XML
            if (nodes.getLength() > 0) {
                String value = nodes.item(0).getTextContent();  // Get the content of the element

                // Convert the string value to the appropriate field type
                Object convertedValue = convertValue(field.getType(), value);

                // Set the converted value to the field of the object
                field.set(obj, convertedValue);
            }

            field.setAccessible(false);  // Revert the accessibility of the field
        }
    }

    /**
     * Converts a string value to the appropriate type based on the field's type.
     *
     * @param fieldType The type of the field to convert the value to.
     * @param value     The string value to be converted.
     * @return The converted value.
     * @throws Exception If the conversion fails.
     */
    private Object convertValue(Class<?> fieldType, String value) throws Exception {
        if (fieldType == String.class) {
            return value;
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == java.util.Date.class) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(value);
        } else {
            throw new Exception("Unsupported field type: " + fieldType.getName());
        }
    }
}
