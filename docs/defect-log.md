# Defects

## Bug 001: Updates in python employee app, would only push to database once you exit app.
Fix: Add 'with' to "services.py" functions.

## Bug 002: Description does not count spaces towards character limit. (Found by Team 1)
Not a bug, working as intended

## Bug 003: Category accepts values with leading zeros like "01" but it should reject it.
Fix: Added stricter comparison in validators.py by utilizing regex instead of just isdigit().