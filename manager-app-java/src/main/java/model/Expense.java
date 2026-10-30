package model;

public class Expense {
    private final int id;
    private final String employeeName;
    private final String expenseDate;
    private final String category;
    private final double amount;
    private final String description;
    private final String status;

    public Expense(int id, String employeeName, String expenseDate, String category, double amount, String description, String status) {
        this.id = id;
        this.employeeName = employeeName;
        this.expenseDate = expenseDate;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    // getters only — no setters
    public int getId() { return id; }
    public String getEmployeeName() { return employeeName; }
    public String getExpenseDate() { return expenseDate; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}
