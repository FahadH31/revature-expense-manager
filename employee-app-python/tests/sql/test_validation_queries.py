"""SQL validation queries (project doc 9.3): run each query in sql/validation/ against a database.

Covers: FR-S1, FR-S2, FR-S5
"""
from pathlib import Path

import pytest

# The `db` fixture is shared from tests/conftest.py.
ROOT = Path(__file__).resolve().parents[3]
SQL_DIR = ROOT / "sql"
VALIDATION_DIR = SQL_DIR / "validation"
QUERY_FILES = sorted(VALIDATION_DIR.glob("*.sql"))


def run_query(conn, path):
    return conn.execute(path.read_text(encoding="utf-8")).fetchall()


def test_five_validation_queries_exist():
    assert len(QUERY_FILES) == 5


@pytest.mark.integration
@pytest.mark.parametrize("path", QUERY_FILES, ids=lambda p: p.stem)
def test_validation_query_returns_no_rows_on_seed_data(db, path):
    """Covers: FR-S1, FR-S2, FR-S5 - every validation query is clean on the seed data."""
    assert run_query(db, path) == []


# Each statement breaks exactly one rule.
BAD_DATA = {
    "01_expenses_without_user": (
        "INSERT INTO expenses(user_id, category_id, amount, description, expense_date, created_at) "
        "VALUES (9999, 1, 5, 'orphan', '2026-11-05', '2026-11-05 10:00:00')"
    ),
    "02_expenses_without_approval": (
        "INSERT INTO expenses(user_id, category_id, amount, description, expense_date, created_at) "
        "SELECT id, 1, 5, 'no approval', '2026-11-05', '2026-11-05 10:00:00' "
        "FROM users WHERE role = 'employee' LIMIT 1"
    ),
    "03_pending_with_reviewer": (
        "UPDATE approvals SET reviewer_id = (SELECT id FROM users WHERE role = 'manager' LIMIT 1) "
        "WHERE status = 'pending'"
    ),
    "04_decided_missing_or_nonmanager": (
        "UPDATE approvals SET reviewer_id = (SELECT id FROM users WHERE role = 'employee' LIMIT 1) "
        "WHERE status = 'approved'"
    ),
    "05_contract_violations": (
        "UPDATE approvals SET review_date = '05/11/2026' WHERE status = 'approved'"
    ),
}


@pytest.mark.negative
@pytest.mark.integration
@pytest.mark.parametrize("path", QUERY_FILES, ids=lambda p: p.stem)
def test_validation_query_detects_bad_data(db, path):
    """Covers: FR-S5 - each query returns rows once matching bad data is inserted."""
    db.execute("PRAGMA foreign_keys = OFF")  # let orphan rows in so the query has something to catch
    db.execute(BAD_DATA[path.stem])
    db.commit()
    assert run_query(db, path) != []
