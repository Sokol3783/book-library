package org.example.entity;

public class Reader {

    private long id;
    private String name;

    public Reader(String name){
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
        return "ID = " + id + " | name = " + name;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reader reader)) {
            return false;
        }

      return id == reader.id;

    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
