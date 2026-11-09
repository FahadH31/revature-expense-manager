# Designed Test Cases - Revature Expense Manager

These cases were designed from `references/testing_plan.md` and `references/Project 1 - Java and Python.md`
(Section 9.9 and Appendix C). They cover both applications, the shared SQLite database, and the six
required cross-app scenarios (XW-01 to XW-06).

Requirement traceability (requirement -> test case -> automated test -> defect) is kept in
[`rtm.md`](rtm.md), not in this file.

## Summary

| Item | Count |
|------|-------|
| Total designed test cases | 50 (minimum 30) |
| Positive only | 15 |
| Negative and/or boundary | 35 (70%; minimum 40%) |
| Automated (planned) | 44 |
| Manual (cross-app XW-01 to XW-06) | 6 |
| SQL validation queries (Section 13, counted separately) | 5 |

## Column guide

| Column | Meaning |
|--------|---------|
| ID | Test case ID. Cross-app cases also show their XW ID from the project document. |
| Feature | Feature or area under test. |
| Description | What the test checks, plus any preconditions. |
| Input | Test data or steps. |
| Expected Result | What should happen. Messages match the project document exactly. |
| Type | Positive, Negative, Boundary (a case can be more than one). |
| Technique | EP = equivalence partitioning, BVA = boundary value analysis. |
| Source | `Own` (written by the team) or `AI-assisted`. |
| Automation | Planned test name, or `Manual`. Update if the final test name differs. |
| Status | `Not Run` until a test cycle is executed. Actual results, defect IDs, cycle and executor are recorded at execution time. |

Fixed "today" for date-based cases is `2026-11-05`.

---

## 1. Authentication - Python Employee App

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-01 | Login (Python) | Valid employee login | username=`test_emp_1`, password=correct seeded password | `Welcome, <full name>` shown; employee menu opens | Positive | EP | Own | `test_login_valid_employee` | Not Run |
| TC-02 | Login (Python) | Wrong password | username=`test_emp_1`, password=`wrong` | `Invalid username or password.` shown; failed attempt counted | Negative | EP | Own | `test_login_wrong_password` | Not Run |
| TC-03 | Login (Python) | Empty username or password | Empty username, then empty password | App re-prompts; empty input is not counted as a failed attempt | Negative, Boundary | BVA | Own | `test_login_empty_input_reprompts` | Not Run |
| TC-04 | Login (Python) | Lockout after 3 failed logins | 3 wrong username/password combinations in one run | `Too many failed attempts.` shown; app exits with code 1 | Negative | State transition | Own | `test_login_three_failures_exits` | Not Run |

## 2. Authentication - Java Manager App

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-05 | Login (Java) | Valid manager login | Seeded manager username and correct password | `Welcome, <full name>` shown; manager menu opens | Positive | EP | Own | `AuthServiceTest.login_validManager` | Not Run |
| TC-06 | Login (Java) | Wrong password | Valid manager username, password=`wrong` | `Invalid username or password.` shown; failed attempt counted | Negative | EP | Own | `AuthServiceTest.login_wrongPassword` | Not Run |
| TC-07 | Login (Java) | Employee tries the Manager App | Valid employee username and password | `This account does not have access to this app.` shown; app exits | Negative | Role partitioning | Own | `AuthServiceTest.login_employeeRoleRejected` | Not Run |
| TC-08 | Login (Java) | Lockout after 3 failed logins | 3 incorrect credentials in one run | `Too many failed attempts.` shown; app exits with code 1 | Negative | State transition | Own | `AuthServiceTest.login_threeFailuresExits` | Not Run |

## 3. Submit Expense (Python) - Validation and Submission

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-09 | Submit Expense | Amount just below the lower limit | amount=`0.00`; other fields valid | Amount rejected with rule shown; amount asked again; no rows inserted | Negative, Boundary | BVA | Own | `test_validate_amount_zero_invalid` | Not Run |
| TC-10 | Submit Expense | Amount at the lower limit | amount=`0.01`; other fields valid | Amount accepted | Positive, Boundary | BVA | Own | `test_validate_amount_minimum_valid` | Not Run |
| TC-11 | Submit Expense | Amount at the upper limit | amount=`10000.00`; other fields valid | Amount accepted | Positive, Boundary | BVA | Own | `test_validate_amount_maximum_valid` | Not Run |
| TC-12 | Submit Expense | Amount just above the upper limit | amount=`10000.01` | Amount rejected; no expense or approval row created | Negative, Boundary | BVA | Own | `test_validate_amount_above_maximum_invalid` | Not Run |
| TC-13 | Submit Expense | Empty description | description=`` (empty) and description=`   ` (spaces only) | Description rejected and asked again | Negative, Boundary | BVA | Own | `test_validate_description_empty_invalid` | Not Run |
| TC-14 | Submit Expense | 1-character description | description=`A` | Description accepted | Positive, Boundary | BVA | Own | `test_validate_description_one_character_valid` | Not Run |
| TC-15 | Submit Expense | 200-character description | description of exactly 200 characters | Description accepted and saved in full | Positive, Boundary | BVA | Own | `test_validate_description_200_characters_valid` | Not Run |
| TC-16 | Submit Expense | 201-character description | description of 201 characters | Description rejected and asked again | Negative, Boundary | BVA | Own | `test_validate_description_201_characters_invalid` | Not Run |
| TC-17 | Submit Expense | Invalid category | category=`0`, `7`, `abc` (each separately) | Selection rejected; category rule shown | Negative | EP | Own | `test_validate_category_invalid_selection` | Not Run |
| TC-18 | Submit Expense | Invalid date | date=`2026-11-06` (future) and `2026-02-30` (not a real date) | Date rejected and asked again | Negative | EP | Own | `test_validate_date_future_or_malformed` | Not Run |
| TC-19 | Submit Expense | Valid expense is saved as pending | amount=`42.50`, category=`1`, description=`Client meal`, date=`2026-08-07` (exactly 90 days ago) | One `expenses` row and one `approvals` row (status `pending`) saved together; new ID printed | Positive | Transaction / integration | Own | `test_submit_expense_creates_expense_and_pending_approval` | Not Run |
| TC-20 | Submit Expense | Empty date defaults to today | Valid fields; press Enter at the date prompt | Expense saved with today's date (`YYYY-MM-DD`) | Positive, Boundary | BVA | Own | `test_submit_empty_date_uses_today` | Not Run |

## 4. View Expenses (Python)

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-21 | View Expenses | Employee sees only own expenses, newest first | Seed expenses for two employees with mixed statuses and dates; log in as one | Only that employee's rows shown (ID, date, category, amount, status); newest date first | Positive | Integration / query | Own | `test_get_expenses_returns_own_newest_first` | Not Run |
| TC-22 | View Expenses | Filter with no matching result | Filter by a status the employee has no expenses in | `No expenses found.` shown; no other employee's rows shown | Negative | EP | Own | `test_get_expenses_empty_status_filter` | Not Run |

## 5. Edit and Delete Pending Expense (Python)

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-23 | Edit Expense | Edit a pending expense; Enter keeps the current value | Own pending expense; press Enter for amount; enter a valid new description | Edit succeeds; unchanged fields stay the same; status stays `pending` | Positive | State transition | Own | `test_edit_pending_expense_updates_selected_fields` | Not Run |
| TC-24 | Edit Expense | Edit an approved expense | Own approved expense selected for editing | `Expense is no longer pending.` shown; database unchanged | Negative | State transition | Own | `test_edit_decided_expense_rejected` | Not Run |
| TC-25 | Delete Expense | Delete a pending expense and confirm | Own pending expense; answer `y` | Expense and its approval row removed together; ID no longer listed | Positive | Transaction / integration | Own | `test_delete_pending_expense_cascades_approval` | Not Run |
| TC-26 | Delete Expense | Cancel delete, or delete another employee's expense | Answer `n` to confirm; then use another employee's expense ID | Non-`y` input leaves data unchanged; other employee's expense is refused (`AuthorizationError`) | Negative | Decision table | Own | `test_delete_cancel_and_other_owner_rejected` | Not Run |

## 6. Manager Review - List, Approve and Deny (Java)

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-27 | Pending List | List all pending expenses, oldest first | Seed pending and decided expenses for several employees | Only pending rows shown (ID, employee name, date, category, amount, description); oldest date first | Positive | Integration / query | Own | `ExpenseDaoIntegrationTest.getPendingExpenses_oldestFirst` | Not Run |
| TC-28 | Approve/Deny | Approve a pending expense with a comment | Expense `7`, decision=`a`, comment=`Approved` | Status `approved`; reviewer, comment and today's review date saved | Positive | State transition | Own | `ApprovalServiceTest.decide_approvalWithOptionalComment` | Not Run |
| TC-29 | Approve/Deny | Deny a pending expense with a valid comment | Expense `8`, decision=`d`, comment=`Missing receipt` | Status `denied`; reviewer, exact trimmed comment and review date saved | Positive | State transition | Own | `ApprovalServiceTest.decide_denialWithValidComment` | Not Run |
| TC-30 | Approve/Deny | Deny without a long enough comment | decision=`d` with empty comment, then with a 4-character comment | Validation error (denial needs 5-200 characters); `saveDecision` never called | Negative, Boundary | BVA | Own | `ApprovalServiceTest.decide_denialWithoutValidComment` | Not Run |
| TC-31 | Approve/Deny | Decide a missing or already decided expense | A nonexistent ID, then an already approved ID | Clear not-found or `Expense is no longer pending.` error; no data changed | Negative | State transition | Own | `ApprovalServiceTest.decide_missingOrDecidedExpense` | Not Run |

## 7. Manager Reports and CSV Export (Java)

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-32 | Reports | Report grouped by employee | Seed several employees with pending, approved and denied expenses | Each row shows name, count, total amount and total approved amount, to 2 decimals | Positive | Aggregation / integration | Own | `ReportServiceTest.reportByEmployee_totalsAndApprovedTotals` | Not Run |
| TC-33 | Reports | Report by category with no matching data | Category report with a valid date range that has no expenses | `No data for the selected criteria.` shown; no misleading totals | Negative | EP | Own | `ReportServiceTest.reportByCategory_noDataForCriteria` | Not Run |
| TC-34 | CSV Export | Export the displayed report to CSV | Display a report with at least one row; choose export | `reports/<type>_<YYYYMMDD_HHMMSS>.csv` created with header row, correct rows and 2-decimal totals | Positive | File / integration | Own | `ReportServiceTest.exportReport_writesHeaderAndRows` | Not Run |

## 8. Review History (Python)

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-35 | Review History | View approved and denied expenses | Seed approved and denied expenses with reviewer names and comments, including one empty comment | Only decided rows shown, newest review first, with status, reviewer, review date and exact comment; `(no comment)` for the empty one | Positive | Integration / query | Own | `test_review_history_shows_reviewer_comment_and_date` | Not Run |
| TC-36 | Review History | Employee has no reviewed expenses | Employee with only pending expenses, or none | No reviewed expenses reported; pending rows not shown | Negative | EP | Own | `test_review_history_empty` | Not Run |

## 9. Authorization (Service Layer)

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-37 | Authorization (Python) | Employee acts on another employee's expense | Employee A calls edit and delete services on employee B's expense | `AuthorizationError` raised for each; database unchanged | Negative | Authorization | Own | `test_employee_cannot_edit_or_delete_other_employee_expense` | Not Run |
| TC-38 | Authorization (Java) | Non-manager calls approve and report | Employee `User` calls approval and report service methods | `AuthorizationException` raised; no decision saved; no report query run | Negative | Authorization | Own | `ApprovalServiceTest.nonManagerCannotDecide`, `ReportServiceTest.nonManagerCannotReport` | Not Run |

## 10. Database, Logging and Error Handling

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-39 | Database | Create database on an empty path; constraints enforced | Temporary empty SQLite file; run schema and seed; then insert an invalid role, invalid status, orphan expense, and amount `0` | Schema and seed succeed; each invalid insert is rejected by the database | Positive, Negative | Database / integration | Own | `test_schema_creation_and_constraints` | Not Run |
| TC-40 | Logging / Errors | Logs hide passwords; errors are friendly | Failed login, validation error, missing `EM_DB_PATH`, unavailable database; inspect console and log files | Logs have time, level and message and no passwords; short friendly errors, no traceback; config/database failure exits with code 1 | Negative | Error guessing | Own | `test_logging_and_expected_error_messages` | Not Run |

## 11. Cross-App Cases (Manual)

These run with both apps against the same database (project document Section 9.6).
Fixed "today" is `2026-11-05`.

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-41 (XW-01) | Cross-app | Employee submits; manager approves; employee views history | Submit a valid expense in Python; approve in Java with comment `Approved`; open history in Python | Status `approved`; reviewer name, review date and exact comment shown | Positive | Cross-app contract | Own | Manual | Not Run |
| TC-42 (XW-02) | Cross-app | Employee submits; manager denies with a comment | Submit in Python; deny in Java with `Missing receipt`; open history in Python | History shows `denied` and the exact comment | Positive | Cross-app contract | Own | Manual | Not Run |
| TC-43 (XW-03) | Cross-app | Edit after manager has decided | Employee opens a pending expense for edit; manager decides it; employee saves the edit | `Expense is no longer pending.`; decided row unchanged | Negative | State transition | Own | Manual | Not Run |
| TC-44 (XW-04) | Cross-app | Manager decides an expense the employee deleted | Manager lists pending; employee deletes one and confirms; manager tries to decide it | Clear not-found error; no decision and no orphan approval row | Negative | State transition | Own | Manual | Not Run |
| TC-45 (XW-05) | Cross-app | Boundary values display identically in both apps | Submit in Python: `0.01`, `10000.00`, a 200-character description, date `2026-08-07` (exactly 90 days ago); list in Java | Java shows the same amounts to 2 decimals, full description and same date | Positive, Boundary | BVA | Own | Manual | Not Run |
| TC-46 (XW-06) | Cross-app | Totals match across employee view, manager report and SQL | Submit several expenses; approve/deny some; compare employee totals, manager report, and an SQL aggregate | All totals match, including 2-decimal rounding and approved-only totals | Positive | Aggregation / integration | Own | Manual | Not Run |

## 12. Additional Validation Cases

| ID | Feature | Description | Input | Expected Result | Type | Technique | Source | Automation | Status |
|----|---------|-------------|-------|-----------------|------|-----------|--------|------------|--------|
| TC-47 | Expense ID validation | Invalid expense ID before a manager decision | IDs `0`, `-1`, `1.5`, `abc`, empty (from `validation_cases.csv`) | Each rejected with a validation message; no DAO lookup or decision | Negative, Boundary | EP | Own | `InputValidatorTest.expenseIdCases` | Not Run |
| TC-48 | Menu choice validation | Invalid menu choices rejected; valid accepted | Empty, `abc`, out-of-range number, then a valid in-range number, in each app | Invalid choices re-prompt without running an action; valid choice runs the selected option | Negative, Boundary | EP | Own | `InputValidatorTest.menuChoiceCases` | Not Run |
| TC-49 | Report date range | Invalid date range rejected | Malformed date; range where start is after end | Date-range rule shown; report not queried; valid dates requested | Negative, Boundary | BVA | Own | `ReportServiceTest.invalidDateRangeRejected` | Not Run |
| TC-50 | Comment length | Denial and approval comment limits | Denial comments of exactly 5 and 200 characters; approval comment of 201 characters | 5 and 200 character denials accepted; 201-character approval rejected; nothing saved for it | Positive, Negative, Boundary | BVA | Own | `ApprovalServiceTest.commentBoundaries` | Not Run |

---

## 13. SQL Validation Queries (run directly on the database)

These are the 5 data-check queries required by the project document (Section 9.3). They are counted
separately from the 50 designed cases above. Each query lives in `sql/validation/`, is run from PyTest
(`employee-app-python/tests/sql/test_validation_queries.py`), and must return zero rows on valid data.
Each query is also run against deliberately bad data to prove it catches the problem.

| ID | Feature | Description | Query file | Expected Result | Type | Automation | Status |
|----|---------|-------------|------------|-----------------|------|------------|--------|
| SQL-01 | Referential integrity | Expenses with no matching user | `01_expenses_without_user.sql` | Zero rows | Negative | `test_validation_query_returns_no_rows_on_seed_data[01_expenses_without_user]`, `test_validation_query_detects_bad_data[01_expenses_without_user]` | Not Run |
| SQL-02 | Referential integrity | Expenses with no approval row | `02_expenses_without_approval.sql` | Zero rows | Negative | `...[02_expenses_without_approval]` (both tests) | Not Run |
| SQL-03 | Lifecycle rule | `pending` approvals that have a reviewer or review date | `03_pending_with_reviewer.sql` | Zero rows | Negative | `...[03_pending_with_reviewer]` (both tests) | Not Run |
| SQL-04 | Lifecycle rule | `approved`/`denied` approvals missing a reviewer or review date, or reviewed by a non-manager | `04_decided_missing_or_nonmanager.sql` | Zero rows | Negative | `...[04_decided_missing_or_nonmanager]` (both tests) | Not Run |
| SQL-05 | Shared data contract | Approvals whose `status` or `review_date` breaks the Section 8.3 contract | `05_contract_violations.sql` | Zero rows | Negative | `...[05_contract_violations]` (both tests) | Not Run |

---

## Coverage by feature

| Feature area | Test cases |
|--------------|-----------|
| Auth - Python login | TC-01 to TC-04 |
| Auth - Java login | TC-05 to TC-08 |
| Submit expense (validators) | TC-09 to TC-20 |
| View expenses / filtering | TC-21, TC-22 |
| Edit / delete pending expense | TC-23 to TC-26 |
| Manager pending list, approve / deny | TC-27 to TC-31, TC-50 |
| Manager reports | TC-32, TC-33, TC-49 |
| CSV export | TC-34 |
| Review history | TC-35, TC-36 |
| Authorization (service layer) | TC-37, TC-38 |
| Database, logging, error handling | TC-39, TC-40 |
| Cross-app (manual) | TC-41 to TC-46 |
| Shared validation data (ID, menu) | TC-47, TC-48 |
| SQL validation queries (direct on database) | SQL-01 to SQL-05 |

Planned automation names are design targets. Change them only if the implemented suites use a different
name, and record the final name in the execution record.
