package library.entities;

import java.time.LocalDate;

public class Loan {
    public long id;
    public long bookId;
    public long memberId;
    public LocalDate loanDate;
    public LocalDate dueDate;
    public LocalDate returnDate; // null если не возвращена

    public Loan(long id, long bookId, long memberId,
                LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }
}
