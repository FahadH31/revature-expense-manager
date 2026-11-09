# Designed Test Cases - Revature Expense Manager

These cases cover both applications, the shared SQLite database, and the six required cross-app
scenarios (XW-01 to XW-06). They are maintained in `test-cases.xlsx`; this file is the Markdown copy of it.

Requirement traceability (requirement -> test case -> automated test -> defect) is kept in
[`rtm.md`](rtm.md), not in this file.

## Summary

| Item | Count |
|------|-------|
| Total designed test cases | 55 |
| Positive | 24 |
| Negative (includes negative boundary cases) | 31 |
| Manual | 49 |
| Automated (PyTest) | 6 |
| Written by the team (`Self`) | 37 |
| AI-assisted | 18 |
| Cross-app cases (XW-01 to XW-06) | 6 (TC-039 to TC-044) |
| SQL validation queries | 5 (TC-045 to TC-049) |

## Column guide

| Column | Meaning |
|--------|---------|
| ID | Test case ID (`TC-001` to `TC-055`). |
| Requirement IDs | Requirements covered (FR = functional, VR = validation rule). |
| Title | Short name of the test. |
| Technique | EP = equivalence partitioning, BVA = boundary value analysis, State Transition, Decision table, Error Guessing, Boundary. |
| Type | Positive or Negative. |
| Source | `Self` (written by the team) or `AI-Assisted`. |
| Automation | `Manual` or `Automated (PyTest)`. |
| Preconditions | State needed before the test starts. |
| Steps | Actions to perform. |
| Test Data | Inputs used, in step order. |
| Expected Result | What should happen. |
| Status | `Not Run` until a test cycle is executed. |

The spreadsheet also has Actual Result, Defect ID, Cycle and Executed By/Date columns. They are filled in
at execution time and are left out here until then.

Fixed "today" for date-based cases is `2026-11-08`.

---


## 1. Authentication, Roles and Lockout

Employee (Python) and Manager (Java) login, role checks and the 3-attempt lockout.

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-001 | FR-A1 | Employee App login - successful | EP | Positive | Self | Manual | - Python venv activated<br>- Python app running in terminal | 1. Enter valid username<br>2. Enter valid password | - test_emp_1<br>- test123 | Terminal shows<br>"Welcome, Test Employee 1" | Not Run |
| TC-002 | FR-A1 | Employee App login - incorrect username | EP | Negative | Self | Manual | - Python venv activated<br>- Python app running in terminal | 1. Enter invalid username<br>2. Enter valid password | - wronguser<br>- test123 | Terminal shows<br>"Invalid username or password." | Not Run |
| TC-003 | FR-A1 | Employee App login - incorrect password | EP | Negative | Self | Manual | - Python venv activated<br>- Python app running in terminal | 1. Enter valid username<br>2. Enter invalid password | - test_emp_1<br>- wrongpass | Terminal shows<br>"Invalid username or password." | Not Run |
| TC-004 | FR-A1 | Employee App login - empty username | EP | Negative | Self | Manual | - Python venv activated<br>- Python app running in terminal | 1. Press 'Enter' key instead of entering a username | N/A | Terminal reprompts "Username: " and the failed attempt counter<br>is not increased | Not Run |
| TC-005 | FR-A1 | Manager App login - successful | EP | Positive | Self | Manual | - Java app compiled<br>- Java app running in terminal | 1. Enter valid username<br>2. Enter valid password | - fahadh<br>- fahad123 | Terminal shows<br>"Welcome, Fahad Hussain" | Not Run |
| TC-006 | FR-A1 | Manager App login - incorrect username | EP | Negative | Self | Manual | - Java app compiled<br>- Java app running in terminal | 1. Enter invalid username<br>2. Enter valid password | - wronguser<br>- fahad123 | Terminal shows<br>"Invalid username or password." | Not Run |
| TC-007 | FR-A1 | Manager App login - incorrect password | EP | Negative | Self | Manual | - Java app compiled<br>- Java app running in terminal | 1. Enter valid username<br>2. Enter invalid password | - fahadh<br>- wrongpass | Terminal shows<br>"Invalid username or password." | Not Run |
| TC-008 | FR-A1 | Manager App login - empty username | EP | Negative | Self | Manual | - Java app compiled<br>- Java app running in terminal | 1. Press 'Enter' key instead of entering a username | N/A | Terminal shows (reprompts)<br>"Username: " | Not Run |
| TC-009 | FR-A2 | Employee App login - login attempt with manager credentals | EP | Negative | Self | Manual | - Python venv activated<br>- Python app running in terminal | 1. Enter valid manager username<br>2. Enter valid manager password | - divyan03<br>- divyan123 | Terminal shows<br>"This account does not have access to this app" and app exits. | Not Run |
| TC-010 | FR-A2 | Manager App login - login attempt with employee credentals | EP | Negative | Self | Manual | - Java app compiled<br>- Java app running in terminal | 1. Enter valid employee username<br>2. Enter valid employee password | - test_emp_1<br>- test123 | Terminal shows<br>"This account does not have access to this app" and app exits. | Not Run |
| TC-011 | FR-A3 | Employee App login - third failed login attempt | State Transition | Negative | Self | Manual | - Python venv activated<br>- Python app running in terminal<br>- 2 failed attempts to login in current session | 1. Enter invalid username and/or password | - wronguser<br>- wrongpass | Terminal shows "Too many failed attempts" and the app exits<br>with code 1. | Not Run |
| TC-012 | FR-A3 | Manager App login - third failed login attempt | State Transition | Negative | Self | Manual | - Java app compiled<br>- Java app running in terminal<br>- 2 failed attempts to login in current session | 1. Enter invalid username and/or password | - wronguser<br>- wrongpass | Terminal shows "Too many failed attempts"and the app exits<br>with code 1. | Not Run |


## 2. Submit Expense (Python)

Successful submission plus amount, description and date validation, including boundaries.

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-013 | FR-E1, VR-01, VR-02, VR-03, VR-04, VR-09 | Employee App submit expense - successful | Decision table | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter a valid $ amount<br>3. Enter a valid category (from numbered list)<br>4. Enter valid description<br>5. Enter valid expense date | - 1<br>- 50.00<br>- 1 (travel)<br>- "Uber to airport"<br>- 2026-11-08 | Row is successfully saved into expenses table and approvals<br>table with status 'pending'. Expense ID is printed in the console. | Not Run |
| TC-014 | FR-E1, VR-01, VR-09 | Employee App submit expense - invalid expense amount entered | Decision table | Negative | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter invalid $ amount | - 1<br>- 20000 | Console prints the error message for invalid amount entered.<br>User is reprompted to enter amount. | Not Run |
| TC-018 | FR-E1, VR-01 | Employee App submit expense - minimum valid amount boundary (0.01) | BVA | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter amount 0.01<br>3. Enter valid category, description, and date | - 1<br>- 0.01<br>- 1<br>- Taxi fare<br>- 2026-11-08 | Expense is successfully saved with status 'pending' and new ID is printed. | Not Run |
| TC-019 | FR-E1, VR-01 | Employee App submit expense - maximum valid amount boundary (10000.00) | BVA | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter amount 10000.00<br>3. Enter valid category, description, and date | - 1<br>- 10000.00<br>- 3<br>- Annual team conference<br>- 2026-11-08 | Expense is successfully saved with status 'pending' and new ID is printed. | Not Run |
| TC-020 | FR-E1, VR-01 | Employee App submit expense - amount zero | BVA | Negative | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter amount 0 | - 1<br>- 0 | App displays amount rule and reprompts for amount. | Not Run |
| TC-021 | FR-E1, VR-01 | Employee App submit expense - amount exceeds maximum boundary (10000.01) | BVA | Negative | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter amount 10000.01 | - 1<br>- 10000.01 | App displays amount rule and reprompts for amount. | Not Run |
| TC-022 | FR-E1, VR-02 | Employee App submit expense - description spaces only | EP | Negative | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter valid amount and category<br>3. Enter spaces only for description | - 1<br>- 25.00<br>- 2<br>- "    " | App displays description rule (1-200 characters after trimming) and reprompts. | Not Run |
| TC-023 | FR-E1, VR-04 | Employee App submit expense - empty date defaults to today | EP | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter valid amount, category, and description<br>3. Press Enter on date prompt | - 1<br>- 15.50<br>- 4<br>- Notebook and pens<br>- (Press Enter) | Expense is saved with today's date in YYYY-MM-DD format and new ID is printed. | Not Run |
| TC-024 | FR-E1, VR-04 | Employee App submit expense - date older than 90 days | BVA | Negative | Self | Manual | - Logged into python employee app | 1. Enter option '1' (submit expense)<br>2. Enter valid amount, category, and description<br>3. Enter date 91 days ago | - 1<br>- 40.00<br>- 1<br>- Flight baggage<br>- 2026-08-09 | App displays date rule (cannot be more than 90 days ago) and reprompts. | Not Run |


## 3. View Expenses (Python)

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-015 | FR-E2, VR-09 | Employee App view expense - no status filtering, successful | EP | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '2' (view expenses)<br>2. Press enter to skip entering a status to filter by | 2.0 | Console prints all of the user's own expenses of all status types. | Not Run |
| TC-025 | FR-E2, VR-09 | Employee App view expenses - filter by status 'pending' | EP | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '2' (view expenses)<br>2. Enter filter 'pending' | - 2<br>- pending | Console prints only expenses belonging to the user that have status 'pending', newest first. | Not Run |
| TC-026 | FR-E2 | Employee App view expenses - no expenses found | EP | Negative | Self | Manual | - Logged into python employee app<br>- User account has no submitted expenses in database | 1. Enter option '2' (view expenses) | - 2 | Console prints "No expenses found." | Not Run |


## 4. Edit and Delete Pending Expense (Python)

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-016 | FR-E3, VR-01, VR-02, VR-03, VR-04, VR-08, VR-09 | Employee App edit pending expense - all fields edited successful | Decision table | Positive | Self | Manual | - Logged into python employee app | 1. Enter option '3' (edit expense)<br>2. Enter ID of pending expense to edit<br>3. Enter valid options for amount, category,<br>description, and expense date. | - 3<br>- (insert valid ID)<br>- 75.00<br>- 2 (meals)<br>- "Lunch for team"<br>- 2026-11-07 | Expense is succesfully edited in the database and the user is informed. | Not Run |
| TC-017 | FR-A4 | Employee App edit expense - attempt to edit another employee expense | EP | Negative | Self | Manual | - Python venv activated<br>- Python app running in terminal<br>- Logged into python employee app as test_emp_1 | 1. Enter option '3' (edit expense)<br>2. Enter ID of an expense belonging to test_emp_2 | - 3<br>- 2 | App refuses action and displays error that user is unauthorized. | Not Run |
| TC-027 | FR-E3, FR-S2 | Employee App edit pending expense - attempt to edit already approved expense | State Transition | Negative | AI-Assisted | Manual | - Logged into python employee app<br>- Expense ID 3 has status 'approved' in database | 1. Enter option '3' (edit expense)<br>2. Enter ID of approved expense | - 3<br>- 3 | Console prints "Expense is no longer pending." and no changes are saved. | Not Run |
| TC-028 | FR-E3 | Employee App edit pending expense - press enter keeps existing value | EP | Positive | AI-Assisted | Manual | - Logged into python employee app<br>- User has pending expense ID 1 | 1. Enter option '3' (edit expense)<br>2. Enter ID 1<br>3. Press enter on amount and category prompts<br>4. Enter new description | - 3<br>- 1<br>- (Enter)<br>- (Enter)<br>- "Updated client meeting lunch" | Amount and category remain unchanged, description updates, and user is notified. | Not Run |
| TC-029 | FR-E4 | Employee App delete pending expense - confirmed with 'y' | EP | Positive | AI-Assisted | Manual | - Logged into python employee app<br>- User has pending expense ID 2 | 1. Enter option '4' (delete expense)<br>2. Enter ID 2<br>3. Enter 'y' to confirm | - 4<br>- 2<br>- y | Expense row and its approvals row are deleted from database. Console confirms deletion. | Not Run |
| TC-030 | FR-E4 | Employee App delete pending expense - cancelled with 'n' | EP | Negative | AI-Assisted | Manual | - Logged into python employee app<br>- User has pending expense ID 2 | 1. Enter option '4' (delete expense)<br>2. Enter ID 2<br>3. Enter 'n' to cancel | - 4<br>- 2<br>- n | Deletion is cancelled and the expense remains unchanged in database. | Not Run |


## 5. Review History (Python)

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-031 | FR-E5 | Employee App review history - view approved expense with comment | EP | Positive | AI-Assisted | Manual | - Logged into python employee app<br>- User has at least one reviewed expense in database | 1. Enter option '5' (review history) | - 5 | Console prints reviewed expenses showing status, reviewer name, review date, and comment, newest review first. | Not Run |
| TC-032 | FR-E5 | Employee App review history - approved expense with empty comment shows '(no comment)' | EP | Positive | AI-Assisted | Manual | - Logged into python employee app<br>- User has approved expense where comment is null or empty | 1. Enter option '5' (review history) | - 5 | Review history displays "(no comment)" in the comment field. | Not Run |


## 6. Manager Review and Reports (Java)

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-033 | FR-M1, VR-09 | Manager App list pending - view all pending expenses | EP | Positive | AI-Assisted | Manual | - Logged into java manager app | 1. Enter option '1' (list pending expenses) | - 1 | Console lists all pending expenses with ID, employee name, date, category, amount, and description, sorted oldest first. | Not Run |
| TC-034 | FR-M1 | Manager App list pending - no pending expenses found | EP | Negative | AI-Assisted | Manual | - Logged into java manager app<br>- No expenses with status 'pending' exist in database | 1. Enter option '1' (list pending expenses) | - 1 | Console prints "No pending expenses." | Not Run |
| TC-035 | FR-M2, VR-06 | Manager App review - approve pending expense with comment | EP | Positive | AI-Assisted | Manual | - Logged into java manager app<br>- Pending expense exists with ID 1 | 1. Enter option '2' (review expense)<br>2. Enter expense ID 1<br>3. Enter decision 'a'<br>4. Enter comment 'Looks good' | - 2<br>- 1<br>- a<br>- Looks good | Status updates to 'approved', reviewer ID set to manager, review date set to today, and comment saved. | Not Run |
| TC-036 | FR-M2, VR-05 | Manager App review - deny pending expense with valid comment | EP | Positive | AI-Assisted | Manual | - Logged into java manager app<br>- Pending expense exists with ID 2 | 1. Enter option '2' (review expense)<br>2. Enter expense ID 2<br>3. Enter decision 'd'<br>4. Enter comment 'Missing itemized receipt' | - 2<br>- 2<br>- d<br>- Missing itemized receipt | Status updates to 'denied', reviewer ID and review date saved, and comment recorded. | Not Run |
| TC-037 | FR-M2, VR-05 | Manager App review - deny pending expense with empty comment | EP | Negative | AI-Assisted | Manual | - Logged into java manager app<br>- Pending expense exists with ID 2 | 1. Enter option '2' (review expense)<br>2. Enter expense ID 2<br>3. Enter decision 'd'<br>4. Press Enter without entering comment | - 2<br>- 2<br>- d<br>- (Enter) | App rejects denial, displays rule requiring 5-200 character comment, and reprompts. | Not Run |
| TC-038 | FR-M2, FR-S2 | Manager App review - attempt to decide already decided expense | State Transition | Negative | AI-Assisted | Manual | - Logged into java manager app<br>- Expense ID 1 already decided ('approved') | 1. Enter option '2' (review expense)<br>2. Enter expense ID 1 | - 2<br>- 1 | App shows clear error that expense is already decided, and database remains unchanged. | Not Run |
| TC-053 | FR-M3 | Manager App reports - View report by employee and category | EP | Positive | AI-Assisted | Manual | - Logged into java manager app<br>- Seed data contains submitted and decided expenses | 1. Enter option '3' (reports)<br>2. Select report by employee<br>3. Select report by category | - Option 3 (reports)<br>- Option 1 (by employee)<br>- Option 2 (by category) | Console displays formatted table showing employee/category name count total amount and total approved formatted to 2 decimals. | Not Run |


## 7. Cross-App Scenarios XW-01 to XW-06 (Manual)

These run with both apps against the same database.

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-039 | FR-S5, FR-E1, FR-M2, FR-E5 | Cross-app XW-01 - Employee submits, manager approves with comment, employee views history | State Transition | Positive | Self | Manual | - Both apps running in terminal<br>- Shared SQLite database connected | 1. In Python app, employee submits valid expense<br>2. In Java app, manager lists pending and approves with comment<br>3. In Python app, employee opens review history | - Submit: $60.00, Meals<br>- Approve: 'Approved for reimbursement' | Python app displays status 'approved', manager name, today's date, and the manager comment. | Not Run |
| TC-040 | FR-S5, FR-E1, FR-M2, FR-E5 | Cross-app XW-02 - Employee submits, manager denies with comment, employee views history | State Transition | Positive | Self | Manual | - Both apps running in terminal<br>- Shared SQLite database connected | 1. In Python app, employee submits valid expense<br>2. In Java app, manager denies with comment 'Over team meal limit'<br>3. In Python app, employee checks review history | - Submit: $120.00, Meals<br>- Deny: 'Over team meal limit' | Python app displays status 'denied' and the exact comment 'Over team meal limit'. | Not Run |
| TC-041 | FR-S5, FR-E3, FR-M2 | Cross-app XW-03 - Employee edits while manager decides, edit refused | State Transition | Negative | Self | Manual | - Both apps running in terminal<br>- Pending expense exists with ID 4 | 1. In Python app, employee opens expense ID 4 for editing<br>2. In Java app, manager approves expense ID 4<br>3. In Python app, employee attempts to save edits | - Expense ID: 4<br>- Java decision: approve<br>- Python edit: amount $80.00 | Python app refuses edit and displays "Expense is no longer pending." | Not Run |
| TC-042 | FR-S5, FR-E4, FR-M2 | Cross-app XW-04 - Manager lists pending, employee deletes one, manager tries to decide | State Transition | Negative | Self | Manual | - Both apps running in terminal<br>- Pending expense exists with ID 5 | 1. In Java app, manager lists pending expenses (ID 5 visible)<br>2. In Python app, employee deletes expense ID 5 with 'y' confirmation<br>3. In Java app, manager tries to approve expense ID 5 | - Expense ID: 5<br>- Confirm: 'y'<br>- Manager decision: 'a' | Java app shows clear error that expense cannot be found/decided, and no data is corrupted. | Not Run |
| TC-043 | FR-S5, VR-01, VR-02, VR-04 | Cross-app XW-05 - Employee submits boundary values, manager views matching values | BVA | Positive | Self | Manual | - Both apps running in terminal<br>- Shared SQLite database connected | 1. In Python app, employee submits expense with amount 0.01 and 90-day-old date<br>2. In Java app, manager lists pending expenses | - Amount: 0.01<br>- Date: 90 days ago | Java app displays exact amount 0.01, correct date, and matching description. | Not Run |
| TC-044 | FR-S5, FR-M3 | Cross-app XW-06 - Manager report totals match employee totals and SQL query | EP | Positive | Self | Manual | - Both apps running in terminal<br>- Multiple expenses submitted and decided across users | 1. In Java app, manager runs report by employee<br>2. In Python app, check employee expenses<br>3. Run SQL query directly against SQLite database | - Option 3 in manager app | Manager report count, total amount, and total approved match Python app totals and SQL query results with 2 decimals. | Not Run |


## 8. SQL Validation Queries

Each query must return zero rows on valid data. They are run from PyTest.

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-045 | FR-S1, FR-S5 | SQL validation 1 - Expenses with no matching user | Error Guessing | Negative | Self | Automated (PyTest) | - Database populated with test data | 1. Run SQL validation query 1 against SQLite database | sql/validation/query_1_orphan_expenses.sql | Query returns 0 rows (no orphan expense records exist). | Not Run |
| TC-046 | FR-E1, FR-S1 | SQL validation 2 - Expenses with no approval row | Error Guessing | Negative | Self | Automated (PyTest) | - Database populated with test data | 1. Run SQL validation query 2 against SQLite database | sql/validation/query_2_missing_approvals.sql | Query returns 0 rows (every expense has exactly one approval record). | Not Run |
| TC-047 | FR-S2, FR-S5 | SQL validation 3 - Pending approvals having reviewer or review date | State Transition | Negative | Self | Automated (PyTest) | - Database populated with test data | 1. Run SQL validation query 3 against SQLite database | sql/validation/query_3_pending_contract.sql | Query returns 0 rows (pending records never have reviewer or review date). | Not Run |
| TC-048 | FR-S2, FR-S5 | SQL validation 4 - Decided approvals missing reviewer or non-manager reviewer | Error Guessing | Negative | Self | Automated (PyTest) | - Database populated with test data | 1. Run SQL validation query 4 against SQLite database | sql/validation/query_4_decided_reviewers.sql | Query returns 0 rows (all decided records have a valid manager reviewer and date). | Not Run |
| TC-049 | FR-S5, FR-S2 | SQL validation 5 - Approvals breaking status or date contract | Boundary | Negative | Self | Automated (PyTest) | - Database populated with test data | 1. Run SQL validation query 5 against SQLite database | sql/validation/query_5_contract_integrity.sql | Query returns 0 rows (all records follow lowercase status and YYYY-MM-DD format). | Not Run |


## 9. Logging, Startup and Database Constraints

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-050 | FR-S3 | Logging - Actions logged and passwords never logged or printed | Error Guessing | Positive | AI-Assisted | Manual | - Python and Java apps configured<br>- Log files initialized in logs/ | 1. Log in to both apps with valid and invalid attempts<br>2. Submit and review expenses in both apps<br>3. Inspect logs/employee_app.log and logs/manager_app.log | - Valid and invalid credentials<br>- Submit/review actions | Log files record timestamp level and message for all actions. Passwords are never logged or printed in terminal. | Not Run |
| TC-051 | FR-S4 | System startup - Missing or invalid EM_DB_PATH environment variable | Error Guessing | Negative | AI-Assisted | Manual | - EM_DB_PATH environment variable unset or pointing to invalid path | 1. Launch Python Employee App in terminal<br>2. Launch Java Manager App in terminal | EM_DB_PATH="" or "invalid/path.db" | Both apps display clear error message indicating database cannot be opened and exit with exit code 1. | Not Run |
| TC-052 | FR-S1 | Database schema - Direct SQL insert violating constraints fails | Error Guessing | Negative | AI-Assisted | Automated (PyTest) | - Database initialized with schema.sql | 1. Execute direct SQL INSERT with amount <= 0<br>2. Execute direct SQL INSERT with duplicate username<br>3. Execute direct SQL INSERT with invalid role | - amount: -10.00<br>- role: 'admin' | SQLite database rejects statements and throws constraint violation errors. | Not Run |


## 10. Menu Navigation

| ID | Requirement IDs | Title | Technique | Type | Source | Automation | Preconditions | Steps | Test Data | Expected Result | Status |
|----|----|----|----|----|----|----|----|----|----|----|----|
| TC-054 | VR-09 | Employee App navigation - Full menu navigation and exit flow | EP | Positive | AI-Assisted | Manual | - Python app running in terminal | 1. Log in as employee<br>2. Navigate to Submit, View, Edit, Delete, History menus<br>3. Return to main menu and select option '0' to exit | - Credentials: test_emp_1 / test123<br>- Menu choices: 1, 2, 3, 4, 5, 0 | App smoothly transitions through each menu prompt without errors and exits cleanly on option 0. | Not Run |
| TC-055 | VR-09 | Manager App navigation - Full menu navigation and exit flow | EP | Positive | AI-Assisted | Manual | - Java app running in terminal | 1. Log in as manager<br>2. Navigate to List Pending, Review, Reports menus<br>3. Return to main menu and select option '0' to exit | - Credentials: divyan03 / divyan123<br>- Menu choices: 1, 2, 3, 0 | App smoothly transitions through each menu option without exceptions and exits cleanly on option 0. | Not Run |
