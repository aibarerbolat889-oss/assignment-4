package library.repositories;

import library.entities.Loan;
import library.entities.LoanInfo;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository {


    Loan getActiveLoanByBookId(long bookId);
    List<Loan> getActiveLoansByMember(long memberId);
    long createLoan(long bookId, long memberId, LocalDate dueDate);
    void closeLoan(long loanId, LocalDate returnDate);
    List<LoanInfo> getLoanInfoByMember(long memberId);
    List<LoanInfo> getActiveLoanInfo();
}
