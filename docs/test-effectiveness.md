# Test Effectiveness Check

One bug injected per app on branch `testing/test-effectiveness` (overview 9.11).

## Summary

| # | App | File / function | Injected bug | Caught? | Failing tests | Requirement |
|---|-----|-----------------|--------------|---------|---------------|-------------|
| 1 | Python employee app | `employee-app-python/employee_app/validators.py` — `validate_amount` | `<=` changed to `<` in the upper-bound check | Yes | 2 (`test_validate_amount_accepts_valid[10000]`, `[10000.00]`) | VR-01 |
| 2 | Java manager app | `manager-app-java/src/main/java/services/ApprovalService.java` — `decide` | `<` changed to `>` in denial comment min-length check | Yes | 3 (`testDenyPendingExpenseSuccess`, `testCannotDenyWithoutComment`, `testDenialWithShortCommentThrows`) | VR-05 |

## Bug 1 — Python: amount upper bound

```diff
-    return 0 < value <= 10000.00
+    return 0 < value < 10000.00
```

Caught by `pytest`. The boundary values 10000 and 10000.00 are now rejected:

```
tests/test_validators.py::test_validate_amount_accepts_valid[10000] FAILED
tests/test_validators.py::test_validate_amount_accepts_valid[10000.00] FAILED
```

No new tests needed.

## Bug 2 — Java: denial comment minimum length

```diff
-            if (trimmedComment.length() < 5 || trimmedComment.length() > 200) {
+            if (trimmedComment.length() > 5 || trimmedComment.length() > 200) {
```

Caught by `mvn test`. Valid denial comments are now rejected and short/empty comments are accepted:

```
[ERROR] services.ApprovalServiceTest.testDenyPendingExpenseSuccess
[FAILURE] services.ApprovalServiceTest.testCannotDenyWithoutComment
[FAILURE] services.ApprovalServiceTest.testDenialWithShortCommentThrows
```

No new tests needed.
