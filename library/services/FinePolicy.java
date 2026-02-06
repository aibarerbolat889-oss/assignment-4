package library.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class FinePolicy {

    private static final FinePolicy INSTANCE = new FinePolicy();

    private long finePerDay = 500; // можешь поменять

    private FinePolicy() {}

    public static FinePolicy getInstance() {
        return INSTANCE;
    }

    public boolean isOverdue(LocalDate dueDate, LocalDate today) {
        return today.isAfter(dueDate);
    }

    public long calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) return 0;
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * finePerDay;
    }

    public void setFinePerDay(long finePerDay) {
        this.finePerDay = finePerDay;
    }

    public long getFinePerDay() {
        return finePerDay;
    }
}
