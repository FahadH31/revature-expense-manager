package services;

import java.sql.Connection;
import java.sql.SQLException;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.DatabaseException;
import model.User;
import dao.UserDao;

public class AuthService {
    private final UserDao userDao;

    public AuthService() {
        this(new UserDao());
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password, Connection conn)
            throws AuthenticationException, AuthorizationException {
        try {
            User user = userDao.findByCredentials(username, password, conn);
            if (user == null) {
                throw new AuthenticationException("Invalid username or password.");
            }
            if (!"manager".equals(user.getRole())) {
                throw new AuthorizationException("User is not authorized to access this resource.");
            }
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Error during authentication: " + e.getMessage(), e);
        }
    }
}