package library.services;

import library.entities.Loan;
import library.entities.LoanInfo;
import library.exceptions.BookAlreadyLoanedException;
import library.exceptions.NotFoundException;
import library.repositories.BookRepository;
import library.repositories.LoanRepository;
import library.repositories.MemberRepository;

import java.time.LocalDate;
import java.util.List;

public class LoanService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public LoanService(BookRepository bookRepository,
                       MemberRepository memberRepository,
                       LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public long borrowBook(long bookId, long memberId, int days) {
        memberRepository.getById(memberId);

        Loan active = loanRepository.getActiveLoanByBookId(bookId);
        if (active != null) {
            throw new BookAlreadyLoanedException("Book already on loan: bookId=" + bookId);
        }

        bookRepository.setAvailable(bookId, false);

        LocalDate dueDate = LocalDate.now().plusDays(days);
        return loanRepository.createLoan(bookId, memberId, dueDate);
    }

    public void returnBook(long bookId) {
        Loan active = loanRepository.getActiveLoanByBookId(bookId);
        if (active == null) {
            throw new NotFoundException("Active loan not found for bookId=" + bookId);
        }

        LocalDate today = LocalDate.now();
        loanRepository.closeLoan(active.id, today);
        bookRepository.setAvailable(bookId, true);
    }

    // история по участнику
    public List<LoanInfo> getLoanInfoByMember(long memberId) {
        memberRepository.getById(memberId);
        return loanRepository.getLoanInfoByMember(memberId);
    }

    // все НЕ возвращённые
    public List<LoanInfo> getActiveLoanInfo() {
        return loanRepository.getActiveLoanInfo();
    }
}
