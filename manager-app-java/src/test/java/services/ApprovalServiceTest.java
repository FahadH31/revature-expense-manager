package services;

import dao.ApprovalDao;
import exceptions.AuthorizationException;
import exceptions.ExpenseManagerException;
import exceptions.InvalidStateException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ApprovalServiceTest {

    private ApprovalDao mockDao;
    private Connection mockConn;
    private ApprovalService service;
    private User manager;
    private User employee;

    @BeforeEach
    void setUp() {
        mockDao = mock(ApprovalDao.class);
        mockConn = mock(Connection.class);
        service = new ApprovalService(mockDao);

        manager = new User(1, "fahadh", "manager", "Fahad Hussain");
        employee = new User(2, "test_emp_1", "employee", "Test Employee 1");
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-M2, VR-06 (manager approves pending expense with optional comment)")
    void testApprovePendingExpenseSuccess() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("pending");

        service.decide(manager, 10, "a", "Approved for client dinner", mockConn);

        verify(mockDao).saveDecision(eq(10), eq("approved"), eq(1), eq("Approved for client dinner"), anyString(), eq(mockConn));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-M2, VR-05 (manager denies pending expense with valid comment)")
    void testDenyPendingExpenseSuccess() throws SQLException {
        when(mockDao.findStatus(11, mockConn)).thenReturn("pending");

        service.decide(manager, 11, "d", "Missing itemized receipt", mockConn);

        verify(mockDao).saveDecision(eq(11), eq("denied"), eq(1), eq("Missing itemized receipt"), anyString(), eq(mockConn));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-M2, VR-06 (manager approves pending expense without comment)")
    void testApproveWithoutCommentPersistsNull() throws SQLException {
        when(mockDao.findStatus(12, mockConn)).thenReturn("pending");

        service.decide(manager, 12, "approved", "", mockConn);

        verify(mockDao).saveDecision(eq(12), eq("approved"), eq(1), isNull(), anyString(), eq(mockConn));
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-A4 (employee calling review throws AuthorizationException and saves nothing)")
    void testEmployeeCannotReview() throws SQLException {
        assertThrows(AuthorizationException.class, () ->
            service.decide(employee, 10, "a", "Self approval", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-A4 (null user calling review throws AuthorizationException)")
    void testNullUserCannotReview() throws SQLException {
        assertThrows(AuthorizationException.class, () ->
            service.decide(null, 10, "a", "No user", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-M2 (non-existent expense ID throws NotFoundException)")
    void testExpenseNotFoundThrows() throws SQLException {
        when(mockDao.findStatus(999, mockConn)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
            service.decide(manager, 999, "a", "Valid comment", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-M2, FR-S2 (reviewing already approved expense throws InvalidStateException)")
    void testCannotReviewAlreadyApproved() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("approved");

        assertThrows(InvalidStateException.class, () ->
            service.decide(manager, 10, "a", "Re-approval", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers VR-05 (denial without comment throws ValidationException)")
    void testCannotDenyWithoutComment() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("pending");

        assertThrows(ValidationException.class, () ->
            service.decide(manager, 10, "d", "", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("boundary")
    @Tag("negative")
    @DisplayName("Covers VR-05 (denial with 4-character comment throws ValidationException)")
    void testDenialWithShortCommentThrows() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("pending");

        assertThrows(ValidationException.class, () ->
            service.decide(manager, 10, "d", "four", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("boundary")
    @Tag("negative")
    @DisplayName("Covers VR-06 (approval comment exceeding 200 characters throws ValidationException)")
    void testApprovalCommentExceedingMaxThrows() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("pending");
        String longComment = "a".repeat(201);

        assertThrows(ValidationException.class, () ->
            service.decide(manager, 10, "a", longComment, mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-M2 (invalid decision character throws ValidationException)")
    void testInvalidReviewCharacter() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("pending");

        assertThrows(ValidationException.class, () ->
            service.decide(manager, 10, "reject", "Comment", mockConn)
        );

        verify(mockDao, never()).saveDecision(anyInt(), anyString(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers custom Exception hierarchy (ValidationException inherits from ExpenseManagerException)")
    void testExceptionPolymorphismHierarchy() throws SQLException {
        when(mockDao.findStatus(10, mockConn)).thenReturn("pending");

        // Verify base ExpenseManagerException catch
        assertThrows(ExpenseManagerException.class, () ->
            service.decide(manager, 10, "invalid_choice", "Comment", mockConn)
        );
    }
}
