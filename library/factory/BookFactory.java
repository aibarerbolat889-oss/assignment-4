package library.factory;

public class BookFactory {

    public static BookItem create(BookType type, String title, String author) {
        return switch (type) {
            case PRINTED -> new PrintedBook(title, author);
            case EBOOK -> new Ebook(title, author);
            case REFERENCE -> new ReferenceBook(title, author);
        };
    }
}
