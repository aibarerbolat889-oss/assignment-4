package library.repositories;

import library.db.IDatabase;
import library.entities.Book;
import library.exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepositoryImpl implements BookRepository {

    private final IDatabase db;

    public BookRepositoryImpl(IDatabase db) {
        this.db = db;
    }

    @Override
    public Book getById(long id) {
        String sql = "select id, title, author, available from books where id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available")
                );
            }
            throw new NotFoundException("Book not found: id=" + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> getAll() {
        String sql = "select id, title, author, available from books order by id";
        List<Book> res = new ArrayList<>();

        try (Connection con = db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                res.add(new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available")
                ));
            }
            return res;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> getAvailable() {
        String sql = "select id, title, author, available from books where available = true order by id";
        List<Book> res = new ArrayList<>();

        try (Connection con = db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                res.add(new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available")
                ));
            }
            return res;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public long create(String title, String author) {
        String sql = "insert into books(title, author, available) values(?,?,true) returning id";

        try (var con = db.getConnection();
             var ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, author);

            var rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public long create(Book book) {
        String sql = "insert into books(title, author, available) values(?,?,?) returning id";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.title);
            ps.setString(2, book.author);
            ps.setBoolean(3, book.available);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setAvailable(long bookId, boolean available) {
        String sql = "update books set available = ? where id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, available);
            ps.setLong(2, bookId);

            int updated = ps.executeUpdate();
            if (updated == 0) throw new NotFoundException("Book not found: id=" + bookId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
