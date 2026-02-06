package library.entities;

public class Book {
    public long id;
    public String title;
    public String author;
    public boolean available;

    public Book(long id, String title, String author, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
    }
}
