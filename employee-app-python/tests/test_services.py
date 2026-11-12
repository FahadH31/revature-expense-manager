"""Service-layer tests for employee_app/services.py against a temporary seeded database.

Seed facts used (sql/seed.sql):
- test_emp_1 is user id 3; divyan03 is a manager.
- Expense 5 belongs to test_emp_1 and is approved.
- Expense 10 belongs to test_emp_2.

Covers: TC-001, TC-002, TC-003, TC-009, TC-013, TC-017, TC-023, TC-027, TC-052
"""
import sqlite3
from datetime import date, timedelta

import pytest

from exceptions import AuthenticationError, AuthorizationError, InvalidStateError
from services import edit_expense, login, submit_expense


@pytest.fixture
def emp1(db):
    """Logged-in test_emp_1 user row."""
    return login("test_emp_1", "test123", db)


def expense_with_approval(conn, expense_id):
    return conn.execute(
        "SELECT e.*, a.status, a.reviewer_id, a.review_date "
        "FROM expenses e JOIN approvals a ON a.expense_id = e.id WHERE e.id = ?",
        (expense_id,),
    ).fetchone()


# Login (FR-A1, FR-A2)
@pytest.mark.smoke
@pytest.mark.integration
def test_login_success(db):
    """Covers: TC-001, FR-A1 - valid employee credentials return the user row."""
    user = login("test_emp_1", "test123", db)
    assert user["full_name"] == "Test Employee 1"
    assert user["role"] == "employee"


@pytest.mark.negative
@pytest.mark.integration
@pytest.mark.parametrize("username, password", [
    ("wronguser", "test123"),   # TC-002 incorrect username
    ("test_emp_1", "wrongpass"),  # TC-003 incorrect password
], ids=["TC-002-wrong-username", "TC-003-wrong-password"])
def test_login_rejects_bad_credentials(db, username, password):
    """Covers: TC-002, TC-003, FR-A1 - wrong username or password is rejected."""
    with pytest.raises(AuthenticationError, match="Invalid username or password."):
        login(username, password, db)


@pytest.mark.negative
@pytest.mark.integration
def test_login_rejects_manager(db):
    """Covers: TC-009, FR-A2 - manager credentials cannot log in to the employee app."""
    with pytest.raises(AuthorizationError, match="does not have access to this app"):
        login("divyan03", "divyan123", db)


# Submit expense (FR-E1)
@pytest.mark.integration
def test_submit_expense_saves_expense_and_pending_approval(db, emp1):
    """Covers: TC-013, FR-E1, VR-01 to VR-04 - valid expense is saved with a pending approval."""
    expense_date = (date.today() - timedelta(days=2)).isoformat()

    new_id = submit_expense(emp1, "50.00", "1", "Uber to airport", expense_date, db)

    row = expense_with_approval(db, new_id)
    assert row is not None
    assert row["user_id"] == emp1["id"]
    assert row["amount"] == 50.00
    assert row["category_id"] == 1
    assert row["description"] == "Uber to airport"
    assert row["expense_date"] == expense_date
    assert row["status"] == "pending"
    assert row["reviewer_id"] is None
    assert row["review_date"] is None


@pytest.mark.integration
def test_submit_expense_empty_date_defaults_to_today(db, emp1):
    """Covers: TC-023, FR-E1, VR-04 - pressing Enter on the date saves today's date."""
    new_id = submit_expense(emp1, "15.50", "4", "Notebook and pens", "", db)

    row = expense_with_approval(db, new_id)
    assert row["expense_date"] == date.today().isoformat()


# Edit expense (FR-A4, FR-E3)
@pytest.mark.negative
@pytest.mark.integration
def test_edit_other_employees_expense_is_refused(db, emp1):
    """Covers: TC-017, FR-A4 - employee cannot edit another employee's expense."""
    before = dict(expense_with_approval(db, 10))

    with pytest.raises(AuthorizationError):
        edit_expense(emp1, 10, db)

    assert dict(expense_with_approval(db, 10)) == before


@pytest.mark.negative
@pytest.mark.integration
def test_edit_approved_expense_is_refused(db, emp1):
    """Covers: TC-027, FR-E3, FR-S2 - an approved expense can no longer be edited."""
    before = dict(expense_with_approval(db, 5))
    assert before["status"] == "approved"

    with pytest.raises(InvalidStateError, match="Expense is no longer pending."):
        edit_expense(emp1, 5, db)

    assert dict(expense_with_approval(db, 5)) == before


# Schema constraints (FR-S1)
@pytest.mark.negative
@pytest.mark.integration
@pytest.mark.parametrize("statement", [
    "INSERT INTO expenses(user_id, category_id, amount, description, expense_date, created_at) "
    "VALUES (3, 1, -10.00, 'negative amount', '2026-11-01', '2026-11-01 10:00:00')",
    "INSERT INTO users(username, password, role, full_name) "
    "VALUES ('test_emp_1', 'x', 'employee', 'Duplicate Username')",
    "INSERT INTO users(username, password, role, full_name) "
    "VALUES ('new_admin', 'x', 'admin', 'Invalid Role')",
], ids=["negative-amount", "duplicate-username", "invalid-role"])
def test_schema_rejects_invalid_rows(db, statement):
    """Covers: TC-052, FR-S1 - direct SQL that breaks a constraint is rejected."""
    with pytest.raises(sqlite3.IntegrityError):
        db.execute(statement)
