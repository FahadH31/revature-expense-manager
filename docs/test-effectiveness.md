# Test Effectiveness Check

One bug injected per app on branch `testing/test-effectiveness` (overview 9.11).

## Summary

| # | App | File / function | Injected bug | Caught? | Failing tests | Requirement | Reverted |
|---|-----|-----------------|--------------|---------|---------------|-------------|----------|
| 1 | Python employee app | `employee-app-python/employee_app/validators.py` — `validate_amount` | `<=` changed to `<` in the upper-bound check | Yes | 2 (`test_validate_amount_accepts_valid[10000]`, `[10000.00]`) | VR-01 | Pending |
| 2 | Java manager app | _TBD_ | _TBD_ | _TBD_ | _TBD_ | _TBD_ | _TBD_ |

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

## Bug 2 — Java manager app

_To be completed._
