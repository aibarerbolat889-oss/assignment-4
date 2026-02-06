package library;

import library.db.PostgresDatabase;
import library.factory.BookFactory;
import library.factory.BookItem;
import library.factory.BookType;
import library.repositories.*;
import library.services.LoanService;
import library.services.ReportingService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        var db = new PostgresDatabase();

        BookRepository bookRepo = new BookRepositoryImpl(db);
        MemberRepository memberRepo = new MemberRepositoryImpl(db);
        LoanRepository loanRepo = new LoanRepositoryImpl(db);

        LoanService loanService = new LoanService(bookRepo, memberRepo, loanRepo);
        ReportingService reportingService = new ReportingService(loanRepo);

        Scanner sc = new Scanner(System.in);

        // ====== SHOW CURRENT STATE ======
        System.out.println("=== AVAILABLE BOOKS ===");
        for (var b : bookRepo.getAvailable()) {
            System.out.println(b.id + " | " + b.title + " | " + b.author);
        }

        System.out.println("\n=== BORROWED (NOT RETURNED) ===");
        var active = loanService.getActiveLoanInfo();
        if (active.isEmpty()) {
            System.out.println("No active loans.");
        } else {
            for (var info : active) {
                System.out.println(
                        "BookId " + info.bookId +
                                " | " + info.bookTitle +
                                " | Borrowed by: " + info.memberName +
                                " | Borrowed: " + info.loanDate +
                                " | Due: " + info.dueDate
                );
            }
        }

        System.out.println("\n=== ALL BOOKS (STATUS) ===");
        HashSet<Long> borrowedIds = new HashSet<>();
        for (var l : active) borrowedIds.add(l.bookId);

        for (var b : bookRepo.getAll()) {
            String status = borrowedIds.contains(b.id) ? "BORROWED" : "AVAILABLE";
            System.out.println(b.id + " | " + b.title + " | " + b.author + " | " + status);
        }

        // ====== REGISTER BOOK (FACTORY) ======
        System.out.print("\nRegister new book? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {

            System.out.print("Type (1=PRINTED, 2=EBOOK, 3=REFERENCE): ");
            int t = Integer.parseInt(sc.nextLine().trim());
            BookType type = (t == 2) ? BookType.EBOOK : (t == 3 ? BookType.REFERENCE : BookType.PRINTED);

            System.out.print("Title: ");
            String title = sc.nextLine();
            System.out.print("Author: ");
            String author = sc.nextLine();

            BookItem item = BookFactory.create(type, title, author);
            long id = bookRepo.create(item.title(), item.author());

            System.out.println("✅ Registered " + item.type() + " book, id=" + id);
        }

        // ====== REGISTER MEMBER ======
        System.out.print("\nRegister new member? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Full name: ");
            String name = sc.nextLine();
            System.out.print("Phone: ");
            String phone = sc.nextLine();

            long id = memberRepo.create(name, phone);
            System.out.println("✅ Registered member, id=" + id);
        }

        // ====== BORROW BOOK ======
        System.out.print("\nBorrow a book? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Book id: ");
            long bookId = Long.parseLong(sc.nextLine());
            System.out.print("Member id: ");
            long memberId = Long.parseLong(sc.nextLine());
            System.out.print("Days: ");
            int days = Integer.parseInt(sc.nextLine());

            long loanId = loanService.borrowBook(bookId, memberId, days);
            var loan = loanRepo.getActiveLoanByBookId(bookId);

            System.out.println("✅ BORROWED");
            System.out.println(
                    "Loan#" + loanId +
                            " | Borrowed: " + loan.loanDate +
                            " | Due: " + loan.dueDate
            );
        }

        // ====== RETURN BOOK ======
        System.out.print("\nReturn a book? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Book id: ");
            long bookId = Long.parseLong(sc.nextLine());

            loanService.returnBook(bookId);
            System.out.println("✅ RETURNED on " + LocalDate.now());
        }

        // ====== REPORT (BUILDER + SINGLETON + LAMBDAS) ======
        System.out.println("\n=== LOAN REPORT ===");
        var report = reportingService.generateActiveLoansReport();
        System.out.println("Generated: " + report.generatedAt);
        System.out.println("Total active: " + report.totalActive);
        System.out.println("Overdue: " + report.totalOverdue);
        System.out.println("Total fine: " + report.totalFine);
        report.lines.forEach(System.out::println);

        System.out.println("\nDone.");
    }
}
