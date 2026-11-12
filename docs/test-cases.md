# Test Cases

| ID | Title | Technique | Type | Automation |
|---|---|---|---|---|
| [TC-001](#tc-001) | Employee App login - successful | EP | Positive | Automated |
| [TC-002](#tc-002) | Employee App login - incorrect username | EP | Negative | Automated |
| [TC-003](#tc-003) | Employee App login - incorrect password | EP | Negative | Automated |
| [TC-004](#tc-004) | Employee App login - empty username | EP | Negative | Automated |
| [TC-005](#tc-005) | Manager App login - successful | EP | Positive | Automated |
| [TC-006](#tc-006) | Manager App login - incorrect username | EP | Negative | Automated |
| [TC-007](#tc-007) | Manager App login - incorrect password | EP | Negative | Automated |
| [TC-008](#tc-008) | Manager App login - empty username | EP | Negative | Automated |
| [TC-009](#tc-009) | Employee App login - login attempt with manager credentals | EP | Negative | Automated |
| [TC-010](#tc-010) | Manager App login - login attempt with employee credentals | EP | Negative | Automated |
| [TC-011](#tc-011) | Employee App login - third failed login attempt | State Transition | Negative | Manual |
| [TC-012](#tc-012) | Manager App login - third failed login attempt | State Transition | Negative | Manual |
| [TC-013](#tc-013) | Employee App submit expense - successful | Decision table | Positive | Automated |
| [TC-014](#tc-014) | Employee App submit expense - invalid expense amount entered | Decision table | Negative | Automated |
| [TC-015](#tc-015) | Employee App view expense - no status filtering, successful | EP | Positive | Automated |
| [TC-016](#tc-016) | Employee App edit pending expense - all fields edited successful | Decision table | Positive | Automated |
| [TC-017](#tc-017) | Employee App edit expense - attempt to edit another employee expense | EP | Negative | Automated |
| [TC-018](#tc-018) | Employee App submit expense - minimum valid amount boundary (0.01) | BVA | Positive | Automated |
| [TC-019](#tc-019) | Employee App submit expense - maximum valid amount boundary (10000.00) | BVA | Positive | Automated |
| [TC-020](#tc-020) | Employee App submit expense - amount zero | BVA | Negative | Automated |
| [TC-021](#tc-021) | Employee App submit expense - amount exceeds maximum boundary (10000.01) | BVA | Negative | Automated |
| [TC-022](#tc-022) | Employee App submit expense - description spaces only | EP | Negative | Automated |
| [TC-023](#tc-023) | Employee App submit expense - empty date defaults to today | EP | Positive | Automated |
| [TC-024](#tc-024) | Employee App submit expense - date older than 90 days | BVA | Negative | Automated |
| [TC-025](#tc-025) | Employee App view expenses - filter by status 'pending' | EP | Positive | Manual |
| [TC-026](#tc-026) | Employee App view expenses - no expenses found | EP | Negative | Manual |
| [TC-027](#tc-027) | Employee App edit pending expense - attempt to edit already approved expense | State Transition | Negative | Automated |
| [TC-028](#tc-028) | Employee App edit pending expense - press enter keeps existing value | EP | Positive | Manual |
| [TC-029](#tc-029) | Employee App delete pending expense - confirmed with 'y' | EP | Positive | Manual |
| [TC-030](#tc-030) | Employee App delete pending expense - cancelled with 'n' | EP | Negative | Manual |
| [TC-031](#tc-031) | Employee App review history - view approved expense with comment | EP | Positive | Manual |
| [TC-032](#tc-032) | Employee App review history - approved expense with empty comment shows '(no comment)' | EP | Positive | Manual |
| [TC-033](#tc-033) | Manager App list pending - view all pending expenses | EP | Positive | Automated |
| [TC-034](#tc-034) | Manager App list pending - no pending expenses found | EP | Negative | Manual |
| [TC-035](#tc-035) | Manager App review - approve pending expense with comment | EP | Positive | Automated |
| [TC-036](#tc-036) | Manager App review - deny pending expense with valid comment | EP | Positive | Automated |
| [TC-037](#tc-037) | Manager App review - deny pending expense with empty comment | EP | Negative | Automated |
| [TC-038](#tc-038) | Manager App review - attempt to decide already decided expense | State Transition | Negative | Automated |
| [TC-039](#tc-039) | Cross-app XW-01 - Employee submits, manager approves with comment, employee views history | State Transition | Positive | Manual |
| [TC-040](#tc-040) | Cross-app XW-02 - Employee submits, manager denies with comment, employee views history | State Transition | Positive | Manual |
| [TC-041](#tc-041) | Cross-app XW-03 - Employee edits while manager decides, edit refused | State Transition | Negative | Manual |
| [TC-042](#tc-042) | Cross-app XW-04 - Manager lists pending, employee deletes one, manager tries to decide | State Transition | Negative | Manual |
| [TC-043](#tc-043) | Cross-app XW-05 - Employee submits boundary values, manager views matching values | BVA | Positive | Manual |
| [TC-044](#tc-044) | Cross-app XW-06 - Manager report totals match employee totals and SQL query | EP | Positive | Automated |
| [TC-045](#tc-045) | SQL validation 1 - Expenses with no matching user | Error Guessing | Negative | Automated |
| [TC-046](#tc-046) | SQL validation 2 - Expenses with no approval row | Error Guessing | Negative | Automated |
| [TC-047](#tc-047) | SQL validation 3 - Pending approvals having reviewer or review date | State Transition | Negative | Automated |
| [TC-048](#tc-048) | SQL validation 4 - Decided approvals missing reviewer or non-manager reviewer | Error Guessing | Negative | Automated |
| [TC-049](#tc-049) | SQL validation 5 - Approvals breaking status or date contract | Boundary | Negative | Automated |
| [TC-050](#tc-050) | Logging - Actions logged and passwords never logged or printed | Error Guessing | Positive | Manual |
| [TC-051](#tc-051) | System startup - Missing or invalid EM_DB_PATH environment variable | Error Guessing | Negative | Manual |
| [TC-052](#tc-052) | Database schema - Direct SQL insert violating constraints fails | Error Guessing | Negative | Automated |
| [TC-053](#tc-053) | Manager App reports - View report by employee and category | EP | Positive | Automated |
| [TC-054](#tc-054) | Employee App navigation - Full menu navigation and exit flow | EP | Positive | Manual |
| [TC-055](#tc-055) | Manager App navigation - Full menu navigation and exit flow | EP | Positive | Manual |
| [TC-056](#tc-056) | Employee App login - empty password | EP | Negative | Automated |
| [TC-057](#tc-057) | Employee App login - empty username and password | EP | Negative | Automated |
| [TC-058](#tc-058) | Manager App login - empty password | EP | Negative | Automated |
| [TC-059](#tc-059) | Manager App login - empty username and password | EP | Negative | Automated |