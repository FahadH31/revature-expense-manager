package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import model.Expense;

public class ExpenseDao {
    public List<Expense> getPendingExpenses(Connection conn) throws SQLException {
        String sql = """
            SELECT e.id, u.full_name AS employee_name, e.expense_date,
                   c.name AS category, e.amount, e.description
            FROM expenses e
            JOIN users      u ON e.user_id      = u.id
            JOIN categories c ON e.category_id  = c.id
            JOIN approvals  a ON a.expense_id   = e.id
            WHERE a.status = 'pending'
            ORDER BY e.expense_date ASC
            """;
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Expense> expenses = new ArrayList<>();
                while (resultSet.next()) {
                    Expense expense = new Expense(
                        resultSet.getInt("id"),
                        resultSet.getString("employee_name"),
                        resultSet.getString("expense_date"),
                        resultSet.getString("category"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("description"),
                        "pending" // status is always pending for this query
                    );
                    expenses.add(expense);
                }
                return expenses;
            }
        }
    }
}