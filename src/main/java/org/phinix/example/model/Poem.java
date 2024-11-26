package org.phinix.example.model;

import org.phinix.lib.common.util.XMLSerializableModel;

@XMLSerializableModel
public class Poem {
    private String title;
    private String author;

    public Poem(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Poem() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Poem{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
