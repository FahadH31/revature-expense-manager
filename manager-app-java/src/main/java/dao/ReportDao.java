package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ReportRow;

public class ReportDao {

    public List<ReportRow> getReportByEmployee(Connection conn) throws SQLException {
        String sql = """
            SELECT u.full_name,
                   COUNT(e.id)                                                   AS count,
                   SUM(e.amount)                                                 AS total,
                   SUM(CASE WHEN a.status = 'approved' THEN e.amount ELSE 0 END) AS approved_total
            FROM expenses e
            JOIN users u     ON e.user_id = u.id
            JOIN approvals a ON a.expense_id = e.id
            GROUP BY u.id
            ORDER BY u.full_name
            """;

        return executeReportQuery(sql, conn);
    }

    public List<ReportRow> getReportByCategory(Connection conn) throws SQLException {
        String sql = """
            SELECT c.name,
                   COUNT(e.id)                                                   AS count,
                   SUM(e.amount)                                                 AS total,
                   SUM(CASE WHEN a.status = 'approved' THEN e.amount ELSE 0 END) AS approved_total
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            JOIN approvals a  ON a.expense_id = e.id
            GROUP BY c.id
            ORDER BY c.name
            """;

        return executeReportQuery(sql, conn);
    }

    private List<ReportRow> executeReportQuery(String sql, Connection conn) throws SQLException {
        List<ReportRow> rows = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString(1);
                int count = rs.getInt("count");
                BigDecimal total = BigDecimal.valueOf(rs.getDouble("total"))
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal approvedTotal = BigDecimal.valueOf(rs.getDouble("approved_total"))
                        .setScale(2, RoundingMode.HALF_UP);

                rows.add(new ReportRow(name, count, total, approvedTotal));
            }
        }
        return rows;
    }
}
