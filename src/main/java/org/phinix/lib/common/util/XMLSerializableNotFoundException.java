package org.phinix.lib.common.util;

/**
 * <p>Exception thrown when an object that is not annotated with {@link XMLSerializableModel}
 * is attempted to be serialized to XML.
 * This exception is used to indicate that the class of the object does not meet the
 * required criteria for XML serialization, which is the presence of the
 * {@link XMLSerializableModel} annotation.</p>
 *
 * Example use case:
 *
 * <pre>
 * if (!object.getClass().isAnnotationPresent(XMLSerializableModel.class)) {
 *     throw new XMLSerializableNotFoundException();
 * }
 * </pre>
 *
 * This exception ensures that only objects marked as serializable are processed by
 * the XMLManager class, preventing errors during the XML generation process.
 */
public class XMLSerializableNotFoundException extends Throwable {
    public XMLSerializableNotFoundException() {
        super("Class is not serializable to XML.");
    }
}