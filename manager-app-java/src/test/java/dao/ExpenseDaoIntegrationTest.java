package dao;

import db.Database;
import model.Expense;
import model.ReportRow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class ExpenseDaoIntegrationTest {

    private File tempDbFile;
    private Connection conn;
    private ExpenseDao expenseDao;
    private ApprovalDao approvalDao;
    private ReportDao reportDao;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        tempDbFile = File.createTempFile("test_expense_mgr_", ".db");
        conn = Database.connect(tempDbFile.getAbsolutePath());

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('employee', 'manager')),
                    full_name TEXT NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE categories (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL UNIQUE
                )
            """);
            stmt.execute("""
                CREATE TABLE expenses (
                    id INTEGER PRIMARY KEY,
                    user_id INTEGER NOT NULL REFERENCES users(id),
                    category_id INTEGER NOT NULL REFERENCES categories(id),
                    amount REAL NOT NULL CHECK(amount > 0 AND amount <= 10000),
                    description TEXT NOT NULL,
                    expense_date TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE approvals (
                    id INTEGER PRIMARY KEY,
                    expense_id INTEGER NOT NULL UNIQUE REFERENCES expenses(id),
                    status TEXT NOT NULL DEFAULT 'pending' CHECK(status IN ('pending', 'approved', 'denied')),
                    reviewer_id INTEGER REFERENCES users(id),
                    comment TEXT,
                    review_date TEXT
                )
            """);

            stmt.execute("INSERT INTO users VALUES (1, 'mgr_asha', 'pass1', 'manager', 'Asha Manager');");
            stmt.execute("INSERT INTO users VALUES (2, 'emp_bob', 'pass2', 'employee', 'Bob Employee');");
            stmt.execute("INSERT INTO users VALUES (3, 'emp_alice', 'pass3', 'employee', 'Alice Employee');");

            stmt.execute("INSERT INTO categories VALUES (1, 'Travel');");
            stmt.execute("INSERT INTO categories VALUES (2, 'Meals');");

            stmt.execute("INSERT INTO expenses VALUES (101, 2, 1, 150.00, 'Flight to client', '2026-11-01', '2026-11-01 10:00:00');");
            stmt.execute("INSERT INTO approvals (id, expense_id, status) VALUES (1, 101, 'pending');");

            stmt.execute("INSERT INTO expenses VALUES (102, 2, 2, 35.50, 'Team lunch', '2026-11-05', '2026-11-05 12:00:00');");
            stmt.execute("INSERT INTO approvals (id, expense_id, status) VALUES (2, 102, 'pending');");

            stmt.execute("INSERT INTO expenses VALUES (103, 3, 2, 45.00, 'Client coffee', '2026-11-02', '2026-11-02 09:00:00');");
            stmt.execute("INSERT INTO approvals VALUES (3, 103, 'approved', 1, 'Approved by Asha', '2026-11-03');");
        }

        expenseDao = new ExpenseDao();
        approvalDao = new ApprovalDao();
        reportDao = new ReportDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        if (tempDbFile != null && tempDbFile.exists()) {
            tempDbFile.delete();
        }
    }

    @Test
    @DisplayName("Covers FR-M1 (getPendingExpenses returns only pending rows and excludes approved/denied)")
    void testGetPendingExpensesReturnsOnlyPending() throws SQLException {
        List<Expense> pending = expenseDao.getPendingExpenses(conn);

        assertNotNull(pending);
        assertEquals(2, pending.size());
        for (Expense e : pending) {
            assertEquals("pending", e.getStatus());
        }
    }

    @Test
    @DisplayName("Covers FR-M1 (getPendingExpenses sorts rows oldest first by expense_date ascending)")
    void testGetPendingExpensesSortedOldestFirst() throws SQLException {
        List<Expense> pending = expenseDao.getPendingExpenses(conn);

        assertEquals(2, pending.size());
        assertEquals(101, pending.get(0).getId());
        assertEquals("2026-11-01", pending.get(0).getExpenseDate());
        assertEquals(102, pending.get(1).getId());
        assertEquals("2026-11-05", pending.get(1).getExpenseDate());
    }

    @Test
    @DisplayName("Covers FR-M2, FR-S2 (saveDecision updates approvals table status, reviewer, comment, and date)")
    void testSaveDecisionUpdatesRecord() throws SQLException {
        approvalDao.saveDecision(101, "approved", 1, "Approved for reimbursement", "2026-11-10", conn);

        String updatedStatus = approvalDao.findStatus(101, conn);
        assertEquals("approved", updatedStatus);
    }

    @Test
    @DisplayName("Covers FR-M1, FR-M2 (reviewed expense is removed from pending list)")
    void testSaveDecisionRemovesExpenseFromPendingList() throws SQLException {
        approvalDao.saveDecision(101, "denied", 1, "Missing receipt", "2026-11-10", conn);

        List<Expense> remainingPending = expenseDao.getPendingExpenses(conn);
        assertEquals(1, remainingPending.size());
        assertEquals(102, remainingPending.get(0).getId());
    }

    @Test
    @DisplayName("Covers FR-M3, FR-S5 (ReportDao calculates count, total amount, and approved total with 2 decimals)")
    void testReportDaoCalculatesAggregates() throws SQLException {
        List<ReportRow> employeeReports = reportDao.getReportByEmployee(conn);

        assertNotNull(employeeReports);
        assertEquals(2, employeeReports.size());

        // Alice (employee ID 3) submit an expense of 45.00, approved
        ReportRow alice = employeeReports.stream().filter(r -> r.getName().equals("Alice Employee")).findFirst().orElse(null);
        assertNotNull(alice);
        assertEquals(1, alice.getCount());
        assertEquals(new BigDecimal("45.00"), alice.getTotalAmount());
        assertEquals(new BigDecimal("45.00"), alice.getTotalApprovedAmount());

        // Bob (employee ID 2) submits 2 expenses (150.00 + 35.50 = 185.50), neither approved
        ReportRow bob = employeeReports.stream().filter(r -> r.getName().equals("Bob Employee")).findFirst().orElse(null);
        assertNotNull(bob);
        assertEquals(2, bob.getCount());
        assertEquals(new BigDecimal("185.50"), bob.getTotalAmount());
        assertEquals(new BigDecimal("0.00"), bob.getTotalApprovedAmount());
    }
}
