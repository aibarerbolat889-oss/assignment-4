package library.report;

import java.time.LocalDate;
import java.util.List;

public class LoanReport {

    public final LocalDate generatedAt;
    public final int totalActive;
    public final int totalOverdue;
    public final long totalFine;
    public final List<String> lines;

    private LoanReport(Builder b) {
        this.generatedAt = b.generatedAt;
        this.totalActive = b.totalActive;
        this.totalOverdue = b.totalOverdue;
        this.totalFine = b.totalFine;
        this.lines = b.lines;
    }

    public static class Builder {
        private LocalDate generatedAt;
        private int totalActive;
        private int totalOverdue;
        private long totalFine;
        private List<String> lines;

        public Builder generatedAt(LocalDate date) {
            this.generatedAt = date;
            return this;
        }

        public Builder totalActive(int v) {
            this.totalActive = v;
            return this;
        }

        public Builder totalOverdue(int v) {
            this.totalOverdue = v;
            return this;
        }

        public Builder totalFine(long v) {
            this.totalFine = v;
            return this;
        }

        public Builder lines(List<String> lines) {
            this.lines = lines;
            return this;
        }

        public LoanReport build() {
            return new LoanReport(this);
        }
    }
}
