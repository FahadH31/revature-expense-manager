package menu;

import java.sql.Connection;
import java.util.Scanner;
import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import model.User;
import services.AuthService;
import dao.ExpenseDao;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final AuthService authService = new AuthService();
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
                            for (var expense : expenses) {
                                System.out.printf("ID: %d, Employee: %s, Date: %s, Category: %s, Amount: %.2f, Description: %s, Status: %s%n",
                                        expense.getId(),
                                        expense.getEmployeeName(),
                                        expense.getExpenseDate(),
                                        expense.getCategory(),
                                        expense.getAmount(),
                                        expense.getDescription(),
                                        expense.getStatus());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error retrieving pending expenses: " + e.getMessage());
                    }   
                    break;
                case "2":
                    // Will add functionality to review an expense later
                    System.out.println("Review an expense selected.");
                    break;
                case "3":
                    // Will add functionality to generate reports later
                    System.out.println("Reports selected.");
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
