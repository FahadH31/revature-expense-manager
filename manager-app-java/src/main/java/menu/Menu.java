package menu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.ExpenseManagerException;
import model.ReportRow;
import model.User;
import services.AuthService;
import services.ApprovalService;
import services.ReportService;
import dao.ExpenseDao;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final AuthService authService = new AuthService();
    private final ApprovalService approvalService = new ApprovalService();
    private final ReportService reportService = new ReportService();
    // also hold references to your services

    public User runLogin(Connection conn) {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                continue;
            }

            try {
                User user = authService.login(username, password, conn);
                System.out.println("Welcome, " + user.getFullName() + "!");
                return user;
                // call authService.login()
                // print welcome message, return user
            } catch (AuthorizationException e) {
                System.out.println("Access denied.");
                System.exit(0);
            } catch (AuthenticationException e) {
                System.out.println("Invalid username or password.");
                attempts++;
            }
        }
        System.out.println("Too many failed attempts.");
        System.exit(1);
        return null;
    }

    public void runMainMenu(User manager, Connection conn) {
        while (true) {
            System.out.println("\nManager Menu");
            System.out.println("1. List pending expenses");
            System.out.println("2. Review an expense");
            System.out.println("3. Reports");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("List pending expenses selected.");
                    ExpenseDao expenseDao = new ExpenseDao();
                    try {
                        var expenses = expenseDao.getPendingExpenses(conn);
                        if (expenses.isEmpty()) {
                            System.out.println("No pending expenses found.");
                        } else {
                            System.out.println("Pending Expenses:");
                            System.out.printf("\n%-4s | %-16s | %-10s | %-16s | %-10s | %s%n",
                                    "ID", "Employee", "Date", "Category", "Amount", "Description");
                            System.out.println("-".repeat(95));
                            for (var expense : expenses) {
                                System.out.printf("%-4d | %-16s | %-10s | %-16s | $%-9.2f | %s%n",
                                        expense.getId(),
                                        expense.getEmployeeName(),
                                        expense.getExpenseDate(),
                                        expense.getCategory(),
                                        expense.getAmount(),
                                        expense.getDescription());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error retrieving pending expenses: " + e.getMessage());
                    }   
                    break;
                case "2":
                    System.out.print("Enter expense ID: ");
                    String idInput = scanner.nextLine().trim();
                    int expenseId;
                    try {
                        expenseId = Integer.parseInt(idInput);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid expense ID. Must be a number.");
                        break;
                    }

                    System.out.print("Approve (a) or Deny (d): ");
                    String decision = scanner.nextLine().trim();

                    System.out.print("Enter comment: ");
                    String comment = scanner.nextLine().trim();

                    try {
                        approvalService.decide(manager, expenseId, decision, comment, conn);
                        String action = (decision.equalsIgnoreCase("a") || decision.equalsIgnoreCase("approved"))
                                ? "approved"
                                : "denied";
                        System.out.println("Expense #" + expenseId + " successfully " + action + ".");
                    } catch (ExpenseManagerException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.println("\nSelect Report:");
                    System.out.println("1. Spending by Employee");
                    System.out.println("2. Spending by Category");
                    System.out.print("Choose an option: ");
                    String reportChoice = scanner.nextLine().trim();

                    List<ReportRow> reportRows = null;
                    String reportType = null;
                    String headerLabel = null;

                    try {
                        if ("1".equals(reportChoice)) {
                            reportRows = reportService.getReportByEmployee(manager, conn);
                            reportType = "employee";
                            headerLabel = "Employee";
                        } else if ("2".equals(reportChoice)) {
                            reportRows = reportService.getReportByCategory(manager, conn);
                            reportType = "category";
                            headerLabel = "Category";
                        } else {
                            System.out.println("Invalid report option.");
                            break;
                        }

                        if (reportRows.isEmpty()) {
                            System.out.println("No expense data available for this report.");
                            break;
                        }

                        System.out.printf("\n%-20s | %-5s | %-12s | %-14s%n",
                                headerLabel, "Count", "Total Amount", "Total Approved");
                        System.out.println("-".repeat(60));
                        for (ReportRow row : reportRows) {
                            System.out.printf("%-20s | %-5d | $%-11.2f | $%-13.2f%n",
                                    row.getName(),
                                    row.getCount(),
                                    row.getTotalAmount(),
                                    row.getTotalApprovedAmount());
                        }

                        System.out.print("\nExport this report to CSV? (y/n): ");
                        String exportChoice = scanner.nextLine().trim();
                        if ("y".equalsIgnoreCase(exportChoice)) {
                            reportService.exportToCsv(reportRows, reportType);
                        }
                    } catch (ExpenseManagerException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case "0": {
                    System.out.println("Goodbye.");
                    return;
                }
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
