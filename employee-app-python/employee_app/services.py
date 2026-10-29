import logging

from employee_app.exceptions import AuthenticationError, AuthorizationError


logger = logging.getLogger(__name__)


def login(username: str, password: str, conn) -> dict:
    """Authenticate an employee against the users table.

    Raises:
        AuthenticationError: If the username/password pair is not valid.
        AuthorizationError: If the credentials belong to a non-employee.
    """
    row = conn.execute(
        "SELECT * FROM users WHERE username = ? AND password = ?",
        (username, password),
    ).fetchone()

    if row is None:
        logger.warning("Failed login attempt for username: %s", username)
        raise AuthenticationError("Invalid username or password.")

    if row["role"] != "employee":
        raise AuthorizationError("This account does not have access to this app.")

    logger.info("User '%s' logged in successfully.", username)
    return dict(row)
