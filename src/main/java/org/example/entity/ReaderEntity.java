package org.example.entity;

public class ReaderEntity {

  private long id;
  private String name;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReaderEntity)) {
      return false;
    }

    ReaderEntity that = (ReaderEntity) o;
    return id != that.id;
  }

  @Override
  public int hashCode() {
    return 37;
  }
}