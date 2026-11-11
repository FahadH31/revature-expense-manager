-- Validation 3: pending approvals that have a reviewer or a review date. Expected: zero rows.
SELECT id AS approval_id, expense_id, reviewer_id, review_date
FROM approvals
WHERE status = 'pending'
  AND (reviewer_id IS NOT NULL OR review_date IS NOT NULL);
