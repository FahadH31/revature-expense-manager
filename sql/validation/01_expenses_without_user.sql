-- Validation 1: expenses with no matching user. Expected: zero rows.
SELECT e.id AS expense_id, e.user_id
FROM expenses e
LEFT JOIN users u ON u.id = e.user_id
WHERE u.id IS NULL;
