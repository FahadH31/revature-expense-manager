"""Menu tests for employee_app/menu.py using unittest.mock.

Each test patches only input() to simulate what the user types.
Login tests use the real temporary database from the `db` fixture (tests/conftest.py),
and capsys (built into pytest) captures what the menu prints.

Covers: TC-004, TC-009, TC-011, TC-054, FR-A1, FR-A2, FR-A3, VR-09
"""
from unittest.mock import MagicMock, patch

import pytest

import menu


# Login loop (FR-A1, FR-A2, FR-A3)
@pytest.mark.negative
def test_three_failed_logins_exit_with_code_1(db):
    """Covers: TC-011, FR-A3 - three wrong attempts in a row exit with code 1."""
    with patch("builtins.input", side_effect=["wronguser", "wrongpass"] * 3):
        with pytest.raises(SystemExit) as exc:
            menu.run_login(db)

    assert exc.value.code == 1


@pytest.mark.negative
def test_blank_login_does_not_count_as_attempt(db, capsys):
    """Covers: TC-004, FR-A1 - 3 blank attempts don't count toward the lockout; valid login still succeeds."""
    blank_attempts = ["", ""] * 3  # username + password left blank, 3 times
    with patch("builtins.input", side_effect=blank_attempts + ["test_emp_1", "test123"]):
        user = menu.run_login(db)

    assert user["username"] == "test_emp_1"
    output = capsys.readouterr().out
    assert output.count("Username or password left blank.") == 3
    assert "Welcome, Test Employee 1" in output


# Main menu flow (VR-09)
@pytest.mark.smoke
def test_menu_rejects_invalid_choice_then_exits(capsys):
    """Covers: TC-054, VR-09 - an unlisted option shows an error, then 0 exits the menu."""
    user = {"id": 3, "username": "test_emp_1"}

    with patch("builtins.input", side_effect=["9", "0"]):
        menu.run_menu(user, MagicMock())

    output = capsys.readouterr().out
    assert "Invalid choice. Please try again." in output
    assert "Exiting." in output
