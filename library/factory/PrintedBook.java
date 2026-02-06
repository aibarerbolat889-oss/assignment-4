package library.factory;

public class PrintedBook implements BookItem {
    private final String title;
    private final String author;

    public PrintedBook(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String title() { return title; }
    public String author() { return author; }
    public BookType type() { return BookType.PRINTED; }
}

