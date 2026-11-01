# Python Prompts

## 2026-10-29

Prompt: For validators.py,  write a comment under each function describing what they do.



## 2026-11-01
Prompt: Check the edit_expense() function in services.py. Does it accurately match the requirement FR-E3 that is shown in overview.md?

Output:
Yes, the current edit_expense() matches FR-E3 well.

It correctly:

- Looks up the expense by ID.
- Raises NotFoundError("Expense not found.") if missing.
- Raises AuthorizationError if it belongs to another user.
- Raises InvalidStateError("Expense is no longer pending.") if approved or denied.
- Allows editing amount, category, description, and date.
- Uses the same validators as submission.
- Re-prompts only the invalid field.
- Keeps the existing value when the user presses Enter.
- Updates only the expenses row and leaves its pending approval row intact.

One minor observation: invalid input is handled by re-prompting rather than raising ValidationError, which is appropriate for FR-E3’s interactive behavior.

The function is currently named edit_expense(), which is fine; its pending-only behavior is enforced internally.