# Defects

## Bug 001: Updates in python employee app, would only push to database once you exit app.
Fix: Add 'with' to "services.py" functions.

## Bug 002: Description does not count spaces towards character limit. (Found by Team 1)
Not a bug, working as intended

## Bug 003: Category accepts values with leading zeros like "01" but it should reject it.
Fix: Added stricter comparison in validators.py by utilizing regex instead of just isdigit().

## Bug 004: Menu choice validator rejected '0', which is the valid exit option.
Fix: Updated InputValidator.isValidMenuChoice() to allow 0 ((num < 0) || (num > 3)) and updated shared_validation_cases.csv with '0' as valid (true) and '9' as out-of-range (false).
