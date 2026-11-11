package services;

import dao.UserDao;
import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.DatabaseException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserDao mockUserDao;
    private Connection mockConn;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockUserDao = mock(UserDao.class);
        mockConn = mock(Connection.class);
        authService = new AuthService(mockUserDao);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-A1 (valid manager login returns authenticated user)")
    void testValidManagerLogin() throws SQLException {
        User managerUser = new User(1, "fahadh", "manager", "Fahad Hussain");
        when(mockUserDao.findByCredentials("fahadh", "fahad123", mockConn)).thenReturn(managerUser);

        User result = authService.login("fahadh", "fahad123", mockConn);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("fahadh", result.getUsername());
        assertEquals("manager", result.getRole());
        assertEquals("Fahad Hussain", result.getFullName());
        verify(mockUserDao).findByCredentials("fahadh", "fahad123", mockConn);
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-A1 (wrong username or password throws AuthenticationException)")
    void testWrongCredentialsThrowsAuthenticationException() throws SQLException {
        when(mockUserDao.findByCredentials("fahadh", "wrongpass", mockConn)).thenReturn(null);

        assertThrows(AuthenticationException.class, () ->
            authService.login("fahadh", "wrongpass", mockConn)
        );

        verify(mockUserDao).findByCredentials("fahadh", "wrongpass", mockConn);
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-A2 (employee account in manager app throws AuthorizationException)")
    void testEmployeeRoleInManagerAppThrowsAuthorizationException() throws SQLException {
        User employeeUser = new User(2, "test_emp_1", "employee", "Test Employee 1");
        when(mockUserDao.findByCredentials("test_emp_1", "test123", mockConn)).thenReturn(employeeUser);

        assertThrows(AuthorizationException.class, () ->
            authService.login("test_emp_1", "test123", mockConn)
        );

        verify(mockUserDao).findByCredentials("test_emp_1", "test123", mockConn);
    }

    @Test
    @Tag("negative")
    @DisplayName("Covers FR-S4 (forced SQLException wraps to DatabaseException)")
    void testForcedSQLExceptionWrapsToDatabaseException() throws SQLException {
        when(mockUserDao.findByCredentials(anyString(), anyString(), any()))
            .thenThrow(new SQLException("Simulated database connection failure"));

        assertThrows(DatabaseException.class, () ->
            authService.login("fahadh", "fahad123", mockConn)
        );

        verify(mockUserDao).findByCredentials("fahadh", "fahad123", mockConn);
    }
}
