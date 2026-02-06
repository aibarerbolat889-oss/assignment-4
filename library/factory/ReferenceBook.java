package library.factory;

public class ReferenceBook implements BookItem {
    private final String title;
    private final String author;

    public ReferenceBook(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String title() { return title; }
    public String author() { return author; }
    public BookType type() { return BookType.REFERENCE; }
}
