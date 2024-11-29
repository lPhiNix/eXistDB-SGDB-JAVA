package org.phinix.example.model;

import org.phinix.lib.common.util.XMLSerializableModel;

@XMLSerializableModel
public class Essay {
    private String title;
    private String author;
    private int year;

    public Essay(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public Essay() {}

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Essay{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                '}';
    }
}
