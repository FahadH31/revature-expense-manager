package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import dao.ReportDao;
import exceptions.AuthorizationException;
import model.ReportRow;
import model.User;

public class ReportService {
    private final ReportDao reportDao;

    public ReportService() {
        this(new ReportDao());
    }

    public ReportService(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public List<ReportRow> getReportByEmployee(User manager, Connection conn) throws SQLException {
        validateManager(manager);
        return reportDao.getReportByEmployee(conn);
    }

    public List<ReportRow> getReportByCategory(User manager, Connection conn) throws SQLException {
        validateManager(manager);
        return reportDao.getReportByCategory(conn);
    }

    public void exportToCsv(List<ReportRow> rows, String reportType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String cleanType = (reportType == null || reportType.isBlank()) ? "report" : reportType.trim().toLowerCase();
        Path reportsDir = Path.of("reports");
        Path filePath = reportsDir.resolve(cleanType + "_" + timestamp + ".csv");

        try {
            Files.createDirectories(reportsDir);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                String firstHeader = cleanType.equalsIgnoreCase("category") ? "Category" : "Employee";
                writer.write(firstHeader + ",Count,Total Amount,Total Approved Amount");
                writer.newLine();

                for (ReportRow row : rows) {
                    String escapedName = row.getName();
                    if (escapedName.contains(",") || escapedName.contains("\"")) {
                        escapedName = "\"" + escapedName.replace("\"", "\"\"") + "\"";
                    }

                    writer.write(String.format("%s,%d,%.2f,%.2f",
                            escapedName,
                            row.getCount(),
                            row.getTotalAmount(),
                            row.getTotalApprovedAmount()));
                    writer.newLine();
                }
            }

            System.out.println("Report exported successfully to: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to export report to CSV: " + e.getMessage());
        }
    }

    private void validateManager(User manager) {
        if (manager == null || !"manager".equalsIgnoreCase(manager.getRole())) {
            throw new AuthorizationException("User is not authorized to access reports.");
        }
    }
}
