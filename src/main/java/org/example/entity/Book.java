package org.example.entity;

public class Book {

  private long id;
  private String title;
  private String author;

  public Book(String title, String author) {
    this.title = title;
    this.author = author;
  }

  public Book() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return title;
  }

  public void setName(String name) {
    this.title = name;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @Override
  public String toString() {
    return "ID = " + id + " | author = " + author + " | title = " + title;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Book book)) {
      return false;
    }

    return id == book.id;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }
}
