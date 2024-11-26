package org.phinix.lib.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * The XMLManager class provides utility methods to generate an XML file from a list of Java objects.
 * It uses reflection to dynamically map the fields of the objects to XML elements and supports the
 * creation of XML files with a customizable root element.
 */
public class XMLManager {
    /**
     * Generates an XML file from a list of objects and saves it to the specified file path.
     * This method converts each object in the list to an XML element and adds it to a root element.
     * The root element's tag name can be specified, or it will be inferred from the first object in the list.
     * If the objects are not annotated with {@link XMLSerializableModel}, an exception will be thrown.
     *
     * @param <T>                               the type of the objects in the list
     * @param objects                           the list of objects to be converted to XML
     * @param filePath                          the path where the generated XML file will be saved
     * @param rootElementTag                    the tag name for the root element of the XML file. If null, the root
     *                                          element tag is inferred from the first object in the list
     * @throws Exception                        if an error occurs during XML generation or file writing
     * @throws XMLSerializableNotFoundException if the objects in the list are not annotated with {@link XMLSerializableModel}
     */
    public static <T> void generateXmlFromObjects(List<T> objects, String filePath, String rootElementTag) throws Exception, XMLSerializableNotFoundException {

        // Checks if the class of the first object in the list is annotated with the XMLSerializableModel annotation.
        if (!getSpecimenClass(objects).isAnnotationPresent(XMLSerializableModel.class)) {
            throw new XMLSerializableNotFoundException();
        }

        // Establish the XML document and root element
        Document document = createDocument();
        Element rootElement = createRootElement(document, rootElementTag, objects);

        // Add each object to the XML
        appendObjectsToXml(objects, document, rootElement);

        // Save the document to the specified file path
        writeXmlToFile(document, filePath);
    }

    /**
     * Retrieves the class type of the first object in the list.
     * This method is used to infer the class type of the objects being processed, assuming all objects in the list
     * are of the same class type.
     *
     * @param objects the list of objects from which to infer the class type
     * @return        the class type of the first object in the list
     */
    private static Class<?> getSpecimenClass(List<?> objects) {
        Class<?> clazz = objects.get(0).getClass();
        return clazz;
    }

    /**
     * Creates the root element of the XML document.
     * If no root element tag is provided, the tag is inferred from the first object in the list.
     *
     * @param document       the XML document being created
     * @param rootElementTag the tag name for the root element
     * @param objects        the list of objects from which the root element tag is inferred if necessary
     * @param <T>            the type of the objects in the list
     * @return               the root element of the document
     */
    private static <T> Element createRootElement(Document document, String rootElementTag, List<T> objects) {
        if (rootElementTag == null) {
            rootElementTag = inferRootElementTagFromObjects(objects);
        }
        return document.createElement(rootElementTag);
    }

    /**
     * Infers the tag name for the root element based on the class name of the first object in the list.
     * The inferred name is the class name in lowercase, followed by an 's' (e.g., "persons" for a list of Person objects).
     *
     * @param objects the list of objects
     * @param <T>     the type of the objects in the list
     * @return        the inferred root element tag
     */
    private static <T> String inferRootElementTagFromObjects(List<T> objects) {
        Class<?> clazz = getSpecimenClass(objects);
        return clazz.getSimpleName().toLowerCase() + "s";
    }

    /**
     * Creates a new, empty XML document.
     *
     * @return           the created XML document
     * @throws Exception if an error occurs during document creation
     */
    private static Document createDocument() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.newDocument();
    }

    /**
     * Adds the objects in the list to the XML document by calling the appendObjectToXml method for each object.
     *
     * @param objects                 the list of objects to add to the XML
     * @param document                the XML document
     * @param rootElement             the root element of the XML document
     * @param <T>                     the type of the objects in the list
     * @throws IllegalAccessException if the field of an object cannot be accessed via reflection
     */
    private static <T> void appendObjectsToXml(List<T> objects, Document document, Element rootElement) throws IllegalAccessException {
        for (T object : objects) {
            appendObjectToXml(object, document, rootElement);
        }
        document.appendChild(rootElement); // Añadir el rootElement al documento
    }

    /**
     * Converts an individual object to an XML element and appends it to the root element.
     * Each field of the object becomes a child element with the field name as the tag and the field's value as the text content.
     *
     * @param object                  the object to convert to XML
     * @param document                the XML document
     * @param rootElement             the root element to append the object element to
     * @throws IllegalAccessException if the field of the object cannot be accessed via reflection
     */
    private static void appendObjectToXml(Object object, Document document, Element rootElement) throws IllegalAccessException {
        Element objectElement = document.createElement(getObjectTagName(object));
        rootElement.appendChild(objectElement);

        // Reflectively append each field of the object as an XML element
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(object);
            if (value != null) {
                appendFieldToXml(document, objectElement, field.getName(), value);
            }
            field.setAccessible(false);
        }
    }

    /**
     * Retrieves the tag name for an object based on its class name, converted to lowercase.
     *
     * @param object the object to get the tag name for
     * @return       the tag name for the object
     */
    private static String getObjectTagName(Object object) {
        return object.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Adds a field from the object as an XML element.
     * The element's tag is the field's name, and the text content is the field's value.
     *
     * @param document      the XML document
     * @param objectElement the element to append the field to
     * @param fieldName     the name of the field
     * @param value         the value of the field
     */
    private static void appendFieldToXml(Document document, Element objectElement, String fieldName, Object value) {
        Element fieldElement = document.createElement(fieldName);
        fieldElement.setTextContent(value.toString());
        objectElement.appendChild(fieldElement);
    }

    /**
     * Writes the XML document to a file at the specified path.
     *
     * @param document   the XML document to be written to a file
     * @param filePath   the path to save the XML file
     * @throws Exception if an error occurs during the transformation or file writing
     */
    private static void writeXmlToFile(Document document, String filePath) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        configureTransformer(transformer);

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    /**
     * Configures the XML transformer to format the output and set the encoding to UTF-8.
     *
     * @param transformer the transformer to configure
     */
    private static void configureTransformer(Transformer transformer) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    }
}