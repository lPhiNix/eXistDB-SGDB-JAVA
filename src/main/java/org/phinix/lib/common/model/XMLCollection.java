package org.phinix.lib.common.model;

import org.phinix.lib.common.util.XMLFileManager;
import org.phinix.lib.common.util.XMLSerializableNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class XMLCollection<T> {
    private String name;
    private final List<T> objects;

    @SuppressWarnings("unchecked")
    public XMLCollection(String name, List<T> objects) throws XMLSerializableNotFoundException {

        Class<T> clazz = (Class<T>) XMLFileManager.getSpecimenClass(objects);

        if (!XMLFileManager.isXMLSerializable(clazz)) {
            throw new XMLSerializableNotFoundException();
        }

        this.name = name;
        this.objects = objects;
    }

    public XMLCollection(String name, Class<T> clazz) throws XMLSerializableNotFoundException {

        if (!XMLFileManager.isXMLSerializable(clazz)) {
            throw new XMLSerializableNotFoundException();
        }

        this.name = name;
        objects = new ArrayList<>();
    }

    public boolean addObject(T object) {
        return objects.add(object);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getObjects() {
        return objects;
    }
}
