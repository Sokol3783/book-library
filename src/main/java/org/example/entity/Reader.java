package org.example.entity;

import java.util.Objects;

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

      return id == reader.id && Objects.equals(name, reader.name);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
