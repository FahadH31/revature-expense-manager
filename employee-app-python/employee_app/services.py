import logging
import validators
from exceptions import (
    AuthenticationError,
    AuthorizationError,
    InvalidStateError,
    NotFoundError,
    ValidationError,
)
from datetime import date, datetime

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
        logger.warning("Unauthorized login attempt for username: %s", username)
        raise AuthorizationError("This account does not have access to this app.")

    logger.info("User '%s' logged in successfully.", username)
    return dict(row)


def submit_expense(user, amount, category_id, description, expense_date, conn):
    """
    Insert employee expense into the 'expenses' table, and into the 'approvals' table with status 'pending'.
    """
    
    category_id_rows = conn.execute("SELECT id FROM categories").fetchall()
    valid_ids = [row["id"] for row in category_id_rows]

    # raise exceptions but don't handle them here 
    if not validators.validate_amount(amount):
        logger.warning("Expense submission rejected for user '%s': invalid amount.", user["username"])
        raise ValidationError("Invalid amount entered. Value must be between 0.01-10,000.00.")
    elif not validators.validate_category(category_id, valid_ids):
        logger.warning("Expense submission rejected for user '%s': invalid category.", user["username"])
        raise ValidationError("Invalid category entered. Value must be between 0-6.")
    elif not validators.validate_description(description):
        logger.warning("Expense submission rejected for user '%s': invalid description.", user["username"])
        raise ValidationError("Invalid description entered. Entry must be between 1-200 chars.")
    elif not validators.validate_date(expense_date):
        logger.warning("Expense submission rejected for user '%s': invalid date.", user["username"])
        raise ValidationError("Invalid expense date entered. Must follow YYYY-MM-DD format and cannot be a future value or more than 90 days ago.")

    # if empty date, resolve to today's date
    if expense_date.strip() == "":
        expense_date = date.today()

    # get today's date + time
    current_date = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    with conn:
        expense = conn.execute(
            "INSERT INTO expenses(user_id, category_id, amount, description, expense_date, created_at) "
            "VALUES(?, ?, ?, ?, ?, ?)",
            (user["id"], category_id, amount, description, expense_date, current_date)
        )
        expense_id = expense.lastrowid

        conn.execute(
            "INSERT INTO approvals(expense_id, status) VALUES(?, ?)",
            (expense_id, "pending")
        )

    logger.info("Expense %s submitted by user '%s'.", expense_id, user["username"])
    return expense_id

def get_expenses(user, conn, status_filter=None):                                                                                                                  
        query = """                                                                                                                                                    
            SELECT e.id, e.expense_date, c.name AS category, e.amount, e.description, a.status                                                                         
            FROM expenses e                                                                                                                                            
            JOIN categories c ON e.category_id = c.id                                                                                                                  
            JOIN approvals a ON a.expense_id = e.id                                                                                                                    
            WHERE e.user_id = ?                                                                                                                                        
        """                                                                                                                                                            
        params = [user["id"]]                                                                                                                                          
                                                                                                                                                                       
        if status_filter:                                                                                                                                              
            query += " AND a.status = ?"                                                                                                                               
            params.append(status_filter)                                                                                                                               
                                                                                                                                                                       
        query += " ORDER BY e.expense_date DESC"                                                                                                                       
                                                                                                                                                                       
        rows = conn.execute(query, params).fetchall()                                                                                                                  
        return [dict(row) for row in rows] 

def edit_expense(user: dict, expense_id: int, conn):
    row = conn.execute(
        """
        SELECT e.id, e.user_id, e.category_id, e.amount,
               e.description, e.expense_date, a.status
        FROM expenses e
        JOIN approvals a ON a.expense_id = e.id
        WHERE e.id = ?
        """,
        (expense_id,),
    ).fetchone()

    if row is None:
        logger.warning("Edit failed for user '%s': expense %s not found.", user["username"], expense_id)
        raise NotFoundError("Expense not found.")
    if row["user_id"] != user["id"]:
        logger.warning("Unauthorized edit attempt by user '%s' for expense %s.", user["username"], expense_id)
        raise AuthorizationError("You are not authorized to edit this expense.")
    if row["status"] != "pending":
        logger.warning("Edit rejected for user '%s': expense %s is not pending.", user["username"], expense_id)
        raise InvalidStateError("Expense is no longer pending.")

    categories = conn.execute(
        "SELECT id, name FROM categories ORDER BY id"
    ).fetchall()

    valid_category_ids = [c["id"] for c in categories]

    amount = get_validated_input("Amount", row["amount"],validators.validate_amount,"Invalid amount. Value must be between 0.01-10,000.00.",float)

    print("Categories:", ", ".join(f"{c['id']}: {c['name']}" for c in categories))

    category_id = get_validated_input("Category", row["category_id"], lambda x: validators.validate_category(x, valid_category_ids), "Invalid category. Choose a category from the list.",int)

    description = get_validated_input("Description", row["description"], validators.validate_description,"Invalid description. Entry must be between 1-200 chars.")

    expense_date = get_validated_input("Expense date", row["expense_date"], validators.validate_date, "Invalid date. Use YYYY-MM-DD within the allowed date range.")
    with conn:
        conn.execute(
            """
            UPDATE expenses
            SET category_id = ?, amount = ?, description = ?, expense_date = ?
            WHERE id = ?
            """,
            (category_id, amount, description, expense_date, expense_id),
        )

    logger.info("Expense %s edited by user '%s'.", expense_id, user["username"])
    return expense_id

def delete_expense(user: dict, expense_id: int, conn):
    row = conn.execute(
        """
        SELECT e.id, e.user_id, a.status
        FROM expenses e
        JOIN approvals a ON a.expense_id = e.id
        WHERE e.id = ?
        """,
        (expense_id,),
    ).fetchone()

    if row is None:
        logger.warning("Delete failed for user '%s': expense %s not found.", user["username"], expense_id)
        raise NotFoundError("Expense not found.")
    if row["user_id"] != user["id"]:
        logger.warning("Unauthorized delete attempt by user '%s' for expense %s.", user["username"], expense_id)
        raise AuthorizationError("You are not authorized to delete this expense.")
    if row["status"] != "pending":
        logger.warning("Delete rejected for user '%s': expense %s is not pending.", user["username"], expense_id)
        raise InvalidStateError("Expense is no longer pending.")

    confirmation = input("Are you sure? (y/N): ").strip()
    if confirmation.lower() != "y":
        logger.info("Deletion cancelled by user '%s' for expense %s.", user["username"], expense_id)
        return False

    with conn:
        conn.execute("DELETE FROM approvals WHERE expense_id = ?", (expense_id,))
        conn.execute("DELETE FROM expenses WHERE id = ?", (expense_id,))

    logger.info("Expense %s deleted by user '%s'.", expense_id, user["username"])
    return True

def get_review_history(user: dict, conn) -> list[dict]:
    rows = conn.execute(
        """
        SELECT e.id, e.expense_date, c.name AS category, e.amount, e.description,
               a.status,
               COALESCE(NULLIF(TRIM(a.comment), ''), '(no comment)') AS comment,
               u.full_name AS reviewer_name, a.review_date
        FROM expenses e
        JOIN categories c ON e.category_id = c.id
        JOIN approvals a ON a.expense_id = e.id
        LEFT JOIN users u ON a.reviewer_id = u.id
        WHERE e.user_id = ? AND a.status IN ('approved', 'denied')
        ORDER BY a.review_date DESC
        """,
        (user["id"],),
    ).fetchall()
    
    return [dict(row) for row in rows]


def get_validated_input(prompt, current_value, validator, error_message, convert=str):
    while True:
        raw = input(f"{prompt} [{current_value}]: ").strip()

        if not raw:
            return current_value

        if validator(raw):
            return convert(raw)

        print(error_message)
