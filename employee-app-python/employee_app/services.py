import logging
import validators
from exceptions import AuthenticationError, AuthorizationError, ValidationError
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
        raise ValidationError("Invalid amount entered. Value must be between 0.01-10,000.00.")
    elif not validators.validate_category(category_id, valid_ids):
        raise ValidationError("Invalid category entered. Value must be between 0-6.")
    elif not validators.validate_description(description):
        raise ValidationError("Invalid description entered. Entry must be between 1-200 chars.")
    elif not validators.validate_date(expense_date):
        raise ValidationError("Invalid expense date entered. Must follow YYYY-MM-DD format and cannot be a future value or more than 90 days ago.")

    # if empty date, resolve to today's date
    if expense_date.strip() == "":
        expense_date = date.today()

    # get today's date + time
    current_date = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    expense = conn.execute(
        "INSERT INTO expenses(user_id, category_id, amount, description, expense_date, created_at) " 
        "VALUES(?, ?, ?, ?, ?, ?)",
        (user["id"], category_id, amount, description, expense_date, current_date)
    )
    expense_id = expense.lastrowid      # get the expense id so it can be inserted in approvals table

    approval = conn.execute(
        "INSERT INTO approvals(expense_id, status )"
        "VALUES(?, ?)",
        (expense_id, 'pending')
    )

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


