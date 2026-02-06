package library.services;

import library.report.LoanReport;
import library.entities.LoanInfo;
import library.repositories.LoanRepository;

import java.time.LocalDate;
import java.util.List;

public class ReportingService {

    private final LoanRepository loanRepository;
    private final FinePolicy finePolicy = FinePolicy.getInstance();

    public ReportingService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LoanReport generateActiveLoansReport() {
        LocalDate today = LocalDate.now();

        // ✅ lambdas: сортировка + фильтрация
        List<LoanInfo> active = loanRepository.getActiveLoanInfo()
                .stream()
                .sorted((a, b) -> a.dueDate.compareTo(b.dueDate)) // ближайший dueDate сверху
                .toList();

        int overdueCount = (int) active.stream()
                .filter(li -> finePolicy.isOverdue(li.dueDate, today))
                .count();

        long totalFine = active.stream()
                .filter(li -> finePolicy.isOverdue(li.dueDate, today))
                .mapToLong(li -> finePolicy.calculateFine(li.dueDate, today))
                .sum();

        List<String> lines = active.stream()
                .map(li -> {
                    boolean overdue = finePolicy.isOverdue(li.dueDate, today);
                    long fine = overdue ? finePolicy.calculateFine(li.dueDate, today) : 0;
                    String status = overdue ? ("OVERDUE fine=" + fine) : "OK";
                    return "Book: " + li.bookTitle +
                            " | Borrowed by: " + li.memberName +
                            " | Borrowed: " + li.loanDate +
                            " | Due: " + li.dueDate +
                            " | " + status;
                })
                .toList();

        return new LoanReport.Builder()
                .generatedAt(today)
                .totalActive(active.size())
                .totalOverdue(overdueCount)
                .totalFine(totalFine)
                .lines(lines)
                .build();
    }
}
