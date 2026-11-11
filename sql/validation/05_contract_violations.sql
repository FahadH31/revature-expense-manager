-- Validation 5: approvals whose status or review_date breaks the shared data contract (8.3).
-- Status must be lowercase pending/approved/denied; review_date, when set, must be a real YYYY-MM-DD date.
-- Expected: zero rows.
SELECT id AS approval_id, expense_id, status, review_date
FROM approvals
WHERE status NOT IN ('pending', 'approved', 'denied')
   OR status <> lower(status)
   OR (review_date IS NOT NULL
       AND (review_date NOT GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
            OR date(review_date) IS NULL
            OR date(review_date) <> review_date));
