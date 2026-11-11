"""Unit tests for employee_app/validators.py.

Valid/invalid examples come from the VR table in Project 1 (VR-01 to VR-04).
Covers: VR-01, VR-02, VR-03, VR-04
"""
from datetime import date, timedelta

import pytest

import validators

CATEGORY_IDS = [1, 2, 3, 4, 5, 6]
TODAY = date(2026, 11, 10)  # fixed so date tests don't depend on when they run


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
