package services;

import dao.ReportDao;
import exceptions.AuthorizationException;
import model.ReportRow;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    private ReportDao mockReportDao;
    private Connection mockConn;
    private ReportService reportService;
    private User manager;
    private User employee;

    @BeforeEach
    void setUp() {
        mockReportDao = mock(ReportDao.class);
        mockConn = mock(Connection.class);
        reportService = new ReportService(mockReportDao);

        manager = new User(1, "fahadh", "manager", "Fahad Hussain");
        employee = new User(2, "test_emp_1", "employee", "Test Employee 1");
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-M3 (getReportByEmployee returns rows for authorized manager)")
    void testGetReportByEmployeeSuccess() throws SQLException {
        List<ReportRow> expectedRows = List.of(
            new ReportRow("Alice Employee", 3, new BigDecimal("250.00"), new BigDecimal("150.00")),
            new ReportRow("Bob Employee", 1, new BigDecimal("75.50"), new BigDecimal("75.50"))
        );
        when(mockReportDao.getReportByEmployee(mockConn)).thenReturn(expectedRows);

        List<ReportRow> result = reportService.getReportByEmployee(manager, mockConn);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice Employee", result.get(0).getName());
        assertEquals(3, result.get(0).getCount());
        assertEquals(new BigDecimal("250.00"), result.get(0).getTotalAmount());
        assertEquals(new BigDecimal("150.00"), result.get(0).getTotalApprovedAmount());
        verify(mockReportDao).getReportByEmployee(mockConn);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-M3 (getReportByCategory returns rows for authorized manager)")
    void testGetReportByCategorySuccess() throws SQLException {
        List<ReportRow> expectedRows = List.of(
            new ReportRow("Travel", 2, new BigDecimal("500.00"), new BigDecimal("500.00")),
            new ReportRow("Meals", 4, new BigDecimal("120.00"), new BigDecimal("80.00"))
        );
        when(mockReportDao.getReportByCategory(mockConn)).thenReturn(expectedRows);

        List<ReportRow> result = reportService.getReportByCategory(manager, mockConn);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Travel", result.get(0).getName());
        assertEquals(2, result.get(0).getCount());
        verify(mockReportDao).getReportByCategory(mockConn);
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-A4 (employee accessing employee report throws AuthorizationException)")
    void testEmployeeCannotAccessEmployeeReport() throws SQLException {
        assertThrows(AuthorizationException.class, () ->
            reportService.getReportByEmployee(employee, mockConn)
        );

        verify(mockReportDao, never()).getReportByEmployee(any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-A4 (employee accessing category report throws AuthorizationException)")
    void testEmployeeCannotAccessCategoryReport() throws SQLException {
        assertThrows(AuthorizationException.class, () ->
            reportService.getReportByCategory(employee, mockConn)
        );

        verify(mockReportDao, never()).getReportByCategory(any());
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-M4 (exportToCsv generates CSV file in reports directory)")
    void testExportToCsvCreatesFile() {
        List<ReportRow> rows = List.of(
            new ReportRow("Travel", 2, new BigDecimal("300.00"), new BigDecimal("300.00")),
            new ReportRow("Meals", 1, new BigDecimal("45.00"), new BigDecimal("0.00"))
        );

        reportService.exportToCsv(rows, "category_test");

        File reportsDir = new File("reports");
        assertTrue(reportsDir.exists() && reportsDir.isDirectory());

        File[] files = reportsDir.listFiles((dir, name) -> name.startsWith("category_test_") && name.endsWith(".csv"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected at least one exported CSV file to exist");

        // Clean up test generated file
        for (File f : files) {
            f.delete();
        }
    }
}