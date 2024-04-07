package org.example.entity;

public class Reader {

    private long id;
    private String name;

    public Reader(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Reader() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ID = " + id + "| name = " + name;
    }
}
