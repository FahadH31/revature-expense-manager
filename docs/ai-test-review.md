# AI Test Review Log

Tracks test case ideas and feedback from AI, whether they were kept, changed, or rejected, and why.

| # | Suggestion | Requirement ID | Decision | Reason | Resulting TC ID |
|---|---|---|---|---|---|
| 1 | Check that pressing Enter on username doesn't count towards the 3-attempt lockout | FR-A1, FR-A3 | Changed | Spec says empty input doesn't count as a failed attempt, so updated expected result to check that the counter doesn't go up. | TC-004 |
| 2 | Add missing period at the end of "Invalid username or password." | FR-A1 | Changed | Project document includes the period at the end. Fixed the expected result to match word-for-word. | TC-006, TC-007 |
| 3 | Check that empty username in Manager App also doesn't count as failed attempt | FR-A1, FR-A3 | Changed | Same issue as TC-004. Added check for the attempt counter in the Java app. | TC-008 |
| 4 | Replace "(insert valid ID)" with actual ID and specify the confirmation message | FR-E3 | Changed | Needed runnable test data from seed data and clearer wording on what success looks like on screen. | TC-016 |
| 5 | Combine Employee and Manager login tests into single rows with an "App" column | FR-A1 | Rejected | Combining tests for both apps into one row makes results and bug logging messy. Also not mentioned in the project doc appendix. Keeping separate is cleaner. | N/A |
| 6 | Add a manual console test for non-manager calling approval service | FR-A4 | Rejected | Employees can't log into the Manager App console, can't access approval service. No point doing this from menu. | N/A |
| 7 | Add test for trying to edit an already approved expense | FR-E3, FR-S2 | Kept | Needed a negative test to verify pending status is enforced and shows "Expense is no longer pending." | TC-027 |
| 8 | Add test for pressing Enter to keep existing values when editing an expense | FR-E3 | Kept | Spec says "Enter keeps the current value", good happy-path scenario to have covered. | TC-028 |
| 9 | Add tests for deleting pending expense with 'y' confirmation and cancelling with 'n' | FR-E4 | Kept | FR-E4 wasn't covered yet. Needed to make sure 'y' deletes both expense and approval rows, and 'n' cancels cleanly. | TC-029, TC-030 |
| 10 | Add test for approved expense showing "(no comment)" when comment is empty | FR-E5 | Kept | Spec specifically calls out showing "(no comment)" if blank, needed to check that exact text. | TC-032 |
| 11 | Add test for denying expense with empty comment | FR-M2, VR-05 | Kept | Denials require a 5-200 character comment (unlike approvals which are optional), so empty comment should get rejected. | TC-037 |
| 12 | Add test for trying to review an already decided expense | FR-M2, FR-S2 | Kept | Once an expense is approved or denied it can't be changed. Needed to verify the app blocks it. | TC-038 |
| 13 | Add test checking that passwords are never written to log files | FR-S3 | Kept | FR-S3 explicitly says passwords can never be logged or printed, so checking log output is a must. | TC-050 |
| 14 | Add test for startup failure when EM_DB_PATH is missing or wrong | FR-S4 | Kept | Need to make sure the app handles bad or missing DB path cleanly and exits with code 1 instead of crashing. | TC-051 |
| 15 | Add full menu navigation flow tests for both apps | VR-09 | Kept | Section 9.1 specifically requires at least 1 menu-flow test per app to verify the console menus work smoothly. | TC-054, TC-055 |
