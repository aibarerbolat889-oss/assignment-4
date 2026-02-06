package library.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FineCalculator {

    private final int finePerDay;

    public FineCalculator(int finePerDay) {
        this.finePerDay = finePerDay;
    }

    // Проверка просрочена ли книга
    public boolean isOverdue(LocalDate dueDate, LocalDate today) {
        return today.isAfter(dueDate);
    }

    // Штраф если опоздала
    public long calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return 0;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * finePerDay;
    }
}
