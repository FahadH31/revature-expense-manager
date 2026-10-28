class ExpenseManagerError(Exception):
    """Base exception for all Employee App errors."""

class ValidationError(ExpenseManagerError):
    """Input failed validation rule """

class AuthenticationError(ExpenseManagerError):
    """Wrong username or password."""

class AuthorizationError(ExpenseManagerError):
    """User tried to do something they are not allowed to do."""

class NotFoundError(ExpenseManagerError):
    """A requested record (expense, user) was not found."""

class InvalidStateError(ExpenseManagerError):
    """Action cannot be performed in the current state (e.g., editing approved expense)."""

class DatabaseError(ExpenseManagerError):
    """Unexpected database failure."""
