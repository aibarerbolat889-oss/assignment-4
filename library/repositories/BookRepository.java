package library.repositories;

import library.entities.Book;
import java.util.List;

public interface BookRepository {
    Book getById(long id);
    List<Book> getAll();
    List<Book> getAvailable();
    long create(Book book);
    long create(String title, String author);
    void setAvailable(long bookId, boolean available);
}
