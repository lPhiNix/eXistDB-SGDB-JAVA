package org.phinix.lib.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation is used to mark classes that are serializable to XML.
 * It serves as a flag indicating that the class can be processed by the XMLManager
 * to generate XML representations of its instances.</p>
 *
 * <p>The annotation is retained at runtime, allowing reflection-based checks during
 * XML generation. The annotation is applied at the class level.</p>
 *
 * Example usage:
 *
 * <pre>
 * @XMLSerializableModel
 * public class Person {
 *     private String name;
 *     private int age;
 *     // Getters and setters
 * }
 * </pre>
 *
 * Classes without this annotation will trigger an exception when the XMLManager
 * tries to serialize them, ensuring that only valid, annotated classes are processed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XMLSerializableModel {}
