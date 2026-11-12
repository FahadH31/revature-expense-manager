"""Unit tests for employee_app/validators.py.

Valid/invalid examples come from the VR table in Project 1 (VR-01 to VR-04).
VR-08 and VR-09 also run every row of shared-test-data/validation_cases.csv,
which is meant to be shared with the Java manager app.
Covers: VR-01, VR-02, VR-03, VR-04, VR-08, VR-09
"""
import csv
from datetime import date, timedelta
from pathlib import Path

import pytest

import validators

CATEGORY_IDS = [1, 2, 3, 4, 5, 6]
TODAY = date(2026, 11, 10)  # fixed so date tests don't depend on when they run
MENU_CHOICES = {"0", "1", "2", "3", "4", "5"}  # employee app main menu (menu.py)
SHARED_CASES_CSV = Path(__file__).resolve().parents[2] / "shared-test-data" / "validation_cases.csv"


# VR-01 Amount
@pytest.mark.parametrize("raw", ["0.01", "10000", "10000.00", "12.5", "50"])
def test_validate_amount_accepts_valid(raw):
    """Covers: VR-01 - amounts in (0, 10000.00] with at most 2 decimals."""
    assert validators.validate_amount(raw)


@pytest.mark.negative
@pytest.mark.parametrize("raw", ["0", "-5", "10000.01", "0.001", "$50", "abc", "", "1.2.3", "1."])
def test_validate_amount_rejects_invalid(raw):
    """Covers: VR-01 - zero, negative, over max, too many decimals, non-numeric, empty."""
    assert not validators.validate_amount(raw)


# VR-02 Description
@pytest.mark.boundary
@pytest.mark.parametrize("raw", ["a", "a" * 200, "  Uber to airport  "])
def test_validate_description_accepts_valid(raw):
    """Covers: VR-02 - 1 to 200 characters after trimming."""
    assert validators.validate_description(raw)


@pytest.mark.negative
@pytest.mark.parametrize("raw", ["", "   ", "a" * 201])
def test_validate_description_rejects_invalid(raw):
    """Covers: VR-02 - empty, spaces only, 201 characters."""
    assert not validators.validate_description(raw)


# VR-03 Category
@pytest.mark.parametrize("raw", ["1", "6", " 3 "])
def test_validate_category_accepts_listed_ids(raw):
    """Covers: VR-03 - a number from the category list (surrounding spaces trimmed)."""
    assert validators.validate_category(raw, CATEGORY_IDS)


@pytest.mark.negative
@pytest.mark.parametrize("raw", ["0", "7", "abc", "", "   ", "1.0", "-1", "+1"])
def test_validate_category_rejects_invalid(raw):
    """Covers: VR-03 - out of range, non-numeric, empty, decimal, signed."""
    assert not validators.validate_category(raw, CATEGORY_IDS)


@pytest.mark.negative
@pytest.mark.parametrize("raw", ["01", "001", "06"])
def test_validate_category_rejects_leading_zeros(raw):
    """Covers: VR-03 - leading zeros are rejected (defect-log Bug 003)."""
    assert not validators.validate_category(raw, CATEGORY_IDS)


# VR-04 Expense date
@pytest.mark.boundary
@pytest.mark.parametrize("raw", [
    TODAY.isoformat(),                          # today
    (TODAY - timedelta(days=90)).isoformat(),   # exactly 90 days ago
    "",                                         # Enter defaults to today
])
def test_validate_date_accepts_valid(raw):
    """Covers: VR-04 - today, exactly 90 days ago, and empty (defaults to today)."""
    assert validators.validate_date(raw, today=TODAY)


@pytest.mark.negative
@pytest.mark.parametrize("raw", [
    "2026-02-30",                               # not a real date
    "12-10-2026",                               # wrong format
    (TODAY + timedelta(days=1)).isoformat(),    # tomorrow
    (TODAY - timedelta(days=91)).isoformat(),   # 91 days ago
    "abc",
])
def test_validate_date_rejects_invalid(raw):
    """Covers: VR-04 - impossible date, wrong format, future, older than 90 days."""
    assert not validators.validate_date(raw, today=TODAY)


# VR-08 Expense ID (shared cases live in shared-test-data/validation_cases.csv)
def test_validate_expense_id_accepts_surrounding_spaces():
    """Covers: VR-08 - input is trimmed before checking."""
    assert validators.validate_expense_id(" 7 ")


@pytest.mark.negative
@pytest.mark.parametrize("raw", ["007", "+7", "٣", None])
def test_validate_expense_id_rejects_python_edge_cases(raw):
    """Covers: VR-08 - leading zeros, sign, non-ASCII digit, non-string."""
    assert not validators.validate_expense_id(raw)


# VR-09 Menu choice
@pytest.mark.negative
@pytest.mark.parametrize("raw", ["6", "-1", "1.0", None])
def test_validate_menu_choice_rejects_unlisted(raw):
    """Covers: VR-09 - options not on the employee menu are rejected."""
    assert not validators.validate_menu_choice(raw, MENU_CHOICES)


# Shared cases (VR-08, VR-09): one test per row of validation_cases.csv
SHARED_VALIDATORS = {
    "expense_id": validators.validate_expense_id,
    "menu_choice": lambda choice: validators.validate_menu_choice(choice, MENU_CHOICES),
}


def load_shared_cases():
    with SHARED_CASES_CSV.open(newline="", encoding="utf-8") as f:
        return [
            pytest.param(
                row["field"],
                row["input"],
                row["expected"].strip().lower() == "true",
                id=f'{row["field"]}-{row["input"] or "empty"}',
            )
            for row in csv.DictReader(f)
        ]


@pytest.mark.parametrize("field, raw, expected", load_shared_cases())
def test_shared_validation_case(field, raw, expected):
    """Covers: VR-08, VR-09 - Python validators agree with the shared CSV."""
    assert field in SHARED_VALIDATORS, f"No validator mapped for CSV field '{field}'"
    assert SHARED_VALIDATORS[field](raw) is expected
