package menu;

import exceptions.AuthenticationException;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import services.AuthService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class MenuFlowTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;

    @BeforeEach
    void setUp() {
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-A1 (menu handles blank inputs followed by successful login flow)")
    void testSuccessfulLoginFlowWithEmptyInputReprompt() throws Exception {
        // Arrange inputs
        // First prompt is empty username and empty password ('enter' pressing is simulated by \n)
        // Followed by valid username "fahadh" and valid password "fahad123"
        String simulatedInput = "\n\nfahadh\nfahad123\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Connection mockConn = mock(Connection.class);
        AuthService mockAuthService = mock(AuthService.class);
        User expectedUser = new User(1, "fahadh", "manager", "Fahad Hussain");
        when(mockAuthService.login("fahadh", "fahad123", mockConn)).thenReturn(expectedUser);

        // Act
        Menu menu = new Menu();
        Field authField = Menu.class.getDeclaredField("authService");
        authField.setAccessible(true);
        authField.set(menu, mockAuthService);

        User loggedInUser = menu.runLogin(mockConn);

        // Asserts
        assertNotNull(loggedInUser);
        assertEquals("Fahad Hussain", loggedInUser.getFullName());
        assertEquals("manager", loggedInUser.getRole());

        String output = outputCapture.toString();
        assertTrue(output.contains("Username: "));
        assertTrue(output.contains("Password: "));
        assertTrue(output.contains("Welcome, Fahad Hussain!"));
        verify(mockAuthService).login("fahadh", "fahad123", mockConn);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-A1 (menu handles failed attempt then successful retry)")
    void testFailedLoginAttemptThenSuccessfulRetryFlow() throws Exception {
        // Arrange
        // Attempt 1 is wronguser / wrongpass
        // Attempt 2 is correct fahadh / fahad123
        String simulatedInput = "wronguser\nwrongpass\nfahadh\nfahad123\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Connection mockConn = mock(Connection.class);
        AuthService mockAuthService = mock(AuthService.class);
        User expectedUser = new User(1, "fahadh", "manager", "Fahad Hussain");

        when(mockAuthService.login("wronguser", "wrongpass", mockConn))
            .thenThrow(new AuthenticationException("Invalid username or password."));
        when(mockAuthService.login("fahadh", "fahad123", mockConn))
            .thenReturn(expectedUser);

        // Act
        Menu menu = new Menu();
        Field authField = Menu.class.getDeclaredField("authService");
        authField.setAccessible(true);
        authField.set(menu, mockAuthService);

        User loggedInUser = menu.runLogin(mockConn);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("Fahad Hussain", loggedInUser.getFullName());

        String output = outputCapture.toString();
        assertTrue(output.contains("Invalid username or password."));
        assertTrue(output.contains("Welcome, Fahad Hussain!"));
        verify(mockAuthService).login("wronguser", "wrongpass", mockConn);
        verify(mockAuthService).login("fahadh", "fahad123", mockConn);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-A1 (manager app login with empty password reprompts without consuming attempt)")
    void testLoginWithEmptyPasswordReprompt() throws Exception {
        // Arrange
        // Prompt 1: username "fahadh", empty password (pressed Enter)
        // Prompt 2: username "fahadh", password "fahad123"
        String simulatedInput = "fahadh\n\nfahadh\nfahad123\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Connection mockConn = mock(Connection.class);
        AuthService mockAuthService = mock(AuthService.class);
        User expectedUser = new User(1, "fahadh", "manager", "Fahad Hussain");
        when(mockAuthService.login("fahadh", "fahad123", mockConn)).thenReturn(expectedUser);

        Menu menu = new Menu();
        Field authField = Menu.class.getDeclaredField("authService");
        authField.setAccessible(true);
        authField.set(menu, mockAuthService);

        // Act
        User loggedInUser = menu.runLogin(mockConn);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("Fahad Hussain", loggedInUser.getFullName());
        assertEquals("manager", loggedInUser.getRole());

        String output = outputCapture.toString();
        assertTrue(output.contains("Username: "));
        assertTrue(output.contains("Password: "));
        assertTrue(output.contains("Welcome, Fahad Hussain!"));
        // Verify login was called only once with valid credentials, never with empty password
        verify(mockAuthService, times(1)).login("fahadh", "fahad123", mockConn);
        verify(mockAuthService, never()).login(eq("fahadh"), eq(""), any());
    }

    @Test
    @Tag("smoke")
    @DisplayName("Covers FR-A1 (manager app login with empty password and empty username and password)")
    void testLoginWithEmptyPasswordAndEmptyUsernameAndPassword() throws Exception {
        // Arrange
        // 1. Valid username "fahadh" + empty password (Enter) -> reprompt
        // 2. Empty username (Enter) + empty password (Enter)   -> reprompt
        // 3. Valid username "fahadh" + valid password "fahad123" -> successful login
        String simulatedInput = "fahadh\n\n\n\nfahadh\nfahad123\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Connection mockConn = mock(Connection.class);
        AuthService mockAuthService = mock(AuthService.class);
        User expectedUser = new User(1, "fahadh", "manager", "Fahad Hussain");
        when(mockAuthService.login("fahadh", "fahad123", mockConn)).thenReturn(expectedUser);

        Menu menu = new Menu();
        Field authField = Menu.class.getDeclaredField("authService");
        authField.setAccessible(true);
        authField.set(menu, mockAuthService);

        // Act
        User loggedInUser = menu.runLogin(mockConn);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("Fahad Hussain", loggedInUser.getFullName());
        assertEquals("manager", loggedInUser.getRole());

        String output = outputCapture.toString();
        assertTrue(output.contains("Username: "));
        assertTrue(output.contains("Password: "));
        assertTrue(output.contains("Welcome, Fahad Hussain!"));
        // Verify authService.login was only invoked once with valid credentials
        verify(mockAuthService, times(1)).login("fahadh", "fahad123", mockConn);
        verify(mockAuthService, never()).login(eq("fahadh"), eq(""), any());
        verify(mockAuthService, never()).login(eq(""), eq(""), any());
    }
}
