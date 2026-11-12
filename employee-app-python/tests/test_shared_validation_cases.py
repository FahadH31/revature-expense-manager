"""Data-driven validator tests from shared-test-data/validation_cases.csv.

The same CSV is meant to be shared with the Java manager app, so both apps
agree on which inputs are valid. Each CSV row becomes one test.
Covers: VR-08, VR-09
"""
import csv
from pathlib import Path

import pytest

import validators

CSV_PATH = Path(__file__).resolve().parents[2] / "shared-test-data" / "validation_cases.csv"
MENU_CHOICES = {"0", "1", "2", "3", "4", "5"}  # employee app main menu (menu.py)

VALIDATORS = {
    "expense_id": validators.validate_expense_id,
    "menu_choice": lambda raw: validators.validate_menu_choice(raw, MENU_CHOICES),
}


def load_cases():
    with CSV_PATH.open(newline="", encoding="utf-8") as f:
        return [
            pytest.param(
                row["field"],
                row["input"],
                row["expected"].strip().lower() == "true",
                id=f'{row["field"]}-{row["input"] or "empty"}',
            )
            for row in csv.DictReader(f)
        ]


@pytest.mark.parametrize("field, raw, expected", load_cases())
def test_shared_validation_case(field, raw, expected):
    """Covers: VR-08, VR-09 - Python validators agree with the shared CSV."""
    assert field in VALIDATORS, f"No validator mapped for CSV field '{field}'"
    assert VALIDATORS[field](raw) is expected
