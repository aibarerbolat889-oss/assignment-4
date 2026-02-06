package library.entities;

import java.time.LocalDate;

public class LoanInfo {
    public long loanId;
    public long bookId;
    public String bookTitle;
    public String bookAuthor;
    public String memberName;
    public LocalDate loanDate;
    public LocalDate dueDate;
    public LocalDate returnDate; // null если не возвращена

    public LoanInfo(long loanId, long bookId,
                    String bookTitle, String bookAuthor,
                    String memberName,
                    LocalDate loanDate, LocalDate dueDate,
                    LocalDate returnDate) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.memberName = memberName;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }
}