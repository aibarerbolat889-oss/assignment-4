package library.repositories;

import library.db.IDatabase;
import library.entities.Loan;
import library.entities.LoanInfo;
import library.exceptions.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanRepositoryImpl implements LoanRepository {

    private final IDatabase db;

    public LoanRepositoryImpl(IDatabase db) {
        this.db = db;
    }

    @Override
    public Loan getActiveLoanByBookId(long bookId) {
        String sql = """
                select id, book_id, member_id, loan_date, due_date, return_date
                from loans
                where book_id = ? and return_date is null
                """;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapLoan(rs);
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getActiveLoansByMember(long memberId) {
        String sql = """
                select id, book_id, member_id, loan_date, due_date, return_date
                from loans
                where member_id = ? and return_date is null
                order by id
                """;

        List<Loan> res = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) res.add(mapLoan(rs));
            return res;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long createLoan(long bookId, long memberId, LocalDate dueDate) {
        String sql = "insert into loans(book_id, member_id, due_date) values(?,?,?) returning id";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            ps.setLong(2, memberId);
            ps.setDate(3, Date.valueOf(dueDate));

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeLoan(long loanId, LocalDate returnDate) {
        String sql = "update loans set return_date = ? where id = ? and return_date is null";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(returnDate));
            ps.setLong(2, loanId);

            int updated = ps.executeUpdate();
            if (updated == 0) throw new NotFoundException("Active loan not found: id=" + loanId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ История по одному участнику (все займы, включая возвращённые)
    @Override
    public List<LoanInfo> getLoanInfoByMember(long memberId) {
        String sql = """
                SELECT l.id          AS loan_id,
                       l.book_id     AS book_id,
                       b.title       AS title,
                       b.author      AS author,
                       m.full_name   AS member_name,
                       l.loan_date   AS loan_date,
                       l.due_date    AS due_date,
                       l.return_date AS return_date
                FROM loans l
                JOIN books b   ON b.id = l.book_id
                JOIN members m ON m.id = l.member_id
                WHERE l.member_id = ?
                ORDER BY l.id
                """;

        List<LoanInfo> result = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapLoanInfo(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ⭐ ВСЕ НЕ ВОЗВРАЩЁННЫЕ (занятые книги) + кто взял
    @Override
    public List<LoanInfo> getActiveLoanInfo() {
        String sql = """
                SELECT l.id          AS loan_id,
                       l.book_id     AS book_id,
                       b.title       AS title,
                       b.author      AS author,
                       m.full_name   AS member_name,
                       l.loan_date   AS loan_date,
                       l.due_date    AS due_date,
                       l.return_date AS return_date
                FROM loans l
                JOIN books b   ON b.id = l.book_id
                JOIN members m ON m.id = l.member_id
                WHERE l.return_date IS NULL
                ORDER BY l.id
                """;

        List<LoanInfo> result = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapLoanInfo(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private LoanInfo mapLoanInfo(ResultSet rs) throws SQLException {
        Date rd = rs.getDate("return_date");
        LocalDate returnDate = (rd == null) ? null : rd.toLocalDate();

        return new LoanInfo(
                rs.getLong("loan_id"),
                rs.getLong("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("member_name"),
                rs.getDate("loan_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate
        );
    }

    private Loan mapLoan(ResultSet rs) throws SQLException {
        Date returnDt = rs.getDate("return_date");
        LocalDate returnDate = (returnDt == null) ? null : returnDt.toLocalDate();

        return new Loan(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getLong("member_id"),
                rs.getDate("loan_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate
        );
    }
}
