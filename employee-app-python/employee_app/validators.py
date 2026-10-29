import re
from datetime import date, datetime


_DATE_PATTERN = re.compile(r"^\d{4}-\d{2}-\d{2}$")


def validate_amount(raw):
    """Return whether raw is a valid expense amount according to VR-01."""
    if not isinstance(raw, str):
        return False

    text = raw.strip()
    if not text:
        return False

    if text.count(".") > 1:
        return False

    parts = text.split(".")
    if not all(part and part.isdigit() for part in parts):
        return False
    if len(parts) == 2 and len(parts[1]) > 2:
        return False

    try:
        value = float(text)
    except Exception:
        return False

    return 0 < value <= 10000.00


def validate_description(raw):
    """Return whether raw is a 1–200 character description according to VR-02."""
    if not isinstance(raw, str):
        return False

    text = raw.strip()
    return 1 <= len(text) <= 200


def validate_category(raw, valid_ids):
    """Return whether raw is one of the supplied category IDs according to VR-03."""
    if not isinstance(raw, str):
        return False

    text = raw.strip()
    if not text or not text.isdigit():
        return False

    try:
        category_id = int(text)
        return category_id in valid_ids
    except Exception:
        return False


def validate_date(raw, today = None):
    """Return whether raw is a valid expense date according to VR-04.

    An empty date is valid because the project treats Enter as today's date.
    """
    if not isinstance(raw, str):
        return False

    text = raw.strip()
    if not text:
        return True

    if not _DATE_PATTERN.fullmatch(text):
        return False

    try:
        parsed_date = datetime.strptime(text, "%Y-%m-%d").date()
        if today is None:
            comparison_date = date.today()
        elif isinstance(today, datetime):
            comparison_date = today.date()
        elif isinstance(today, date):
            comparison_date = today
        else:
            return False
    except Exception:
        return False

    try:
        age_in_days = (comparison_date - parsed_date).days
        return parsed_date <= comparison_date and age_in_days <= 90
    except Exception:
        return False
