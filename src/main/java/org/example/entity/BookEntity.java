package org.example.entity;

public class BookEntity {

  private long id;
  private String name;
  private String author;

  public BookEntity(long id, String name, String author) {
    this.id = id;
    this.name = name;
    this.author = author;
  }

  public BookEntity() {
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

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BookEntity)) {
      return false;
    }

    BookEntity that = (BookEntity) o;
    return id != that.id;
  }

  @Override
  public int hashCode() {
    return 37;
  }

}
