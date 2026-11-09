# Exploratory Testing

## Session 1 - Java Manager App 
**Charter: Explore the expense review flow (approvals, denials, and comment validation) with boundary lengths, whitespace padding, and invalid expense IDs to find 
  │ comment validation bypasses and state transition defects.**
**Tester: Fahad Hussain** 
**Date: 2026-11-09** 
**Duration:** 30 min
**Environment: Java Manager app on Windows 10, DB seeded from sql/schema.sql + sql/seed.sql**

### What I tried on Team 1's Manager app
| Probe                         | Input                                   | Observed | Assessment       |
| ------------------------------| --------------------------------------- | -------- | ---------------- |
| Denial comment < 5 chars      | "fail" (4 chars)                        | Rejected | OK               |                 
| Denial comment exactly 5 chars| "notes" (5 chars)                       | Accepted | OK               |
| Denial option whitespace only | "  " (2 spaces)                         | Rejected | OK               |                                                    
| Denial comment whitespace only| "     " (5 spaces)                      | Accepted | Fail             |                 
| Denial comment > 200 chars    | Generated text 201 chars in length      | Rejected | OK               |                  
| Review already approved ID    | 24                                      | Rejected | OK               |
| Non-existent int expense ID   | 999                                     | Rejected | OK               | 
| Non-existent text expense ID  | abc                                     | Rejected | OK               | 

### What I found on Team 1's manager app

**Bugs:**

- BUG-001: Denial comment accepts a whitespace-only string if it meets the minimum character length of 5.
  - Severity: Low


### What Team 1 testing ours tried

## Session 1 - Employee App

**Charter:** Explore the submit flow with values near VR-01 to VR-03 boundaries to find off-by-one or rounding issues.
**Tester:** Md Tanjeem Haider
**Date:** 2026-11-09
**Duration:** 30 min
**Environment:** Employee App on windows 11, DB seeded from sql/schema.sql + sql/seed.sql

### What I tried on Team 2's employee app

#### VR-01 Amount

| Probe                     | Input    | Observed | Assessment |
| ------------------------- | -------- | -------- | ---------- |
| Minimum valid             | 0.01     | Allowed  | OK         |
| Trailing zero on decimals | 0.010    | Denied   |  OK        |
| Maximum valid             | 10000.00 | Allowed  |  OK        |
| Trailing dot              | 45.      | Denied   |  OK        |
| Leading dot               | .5       | Denied   |  OK        |
| Leading zeros             | 00.01    | Allowed  |  OK        |
| Number with string        | +45      | Denied   |  OK        |
| Scientific notation       | 1e2      | Denied   |  OK        |
| Three decimals            | 12.345   | Denied   |  OK        |

#### VR-02 Description

| Probe                         | Input                            | Observed | Assessment            |
| ----------------------------- | -------------------------------- | -------- | --------------------- |
| Exactly 200 chars             | "a" 200 times                   | Allowed  | OK                    |
| Exactly 201 chars             | "a" 201 times                   | Denied   |  OK                   |
| 200 chars + 5 trailing spaces | "a" 200 times then " " 5 times | Allowed  | Should not be allowed |

#### VR-03 Category

| Probe              | Input | Observed | Assessment       |
| ------------------ | ----- | -------- | ---------------- |
| Leading zero       | 01    | Allowed  | should be denied |
| Positive sign      | +3    | Denied   | OK               |
| Float format       | 3.0   | Denied   |  OK              |
| Padded with spaces | " 3 " | allowed  |  OK              |

### What I found on Team 2's employee app

**Bugs:**

- BUG-001: Description does not count spaces towards character limit.
  - Severity: Med
- BUG-002: Category accepts values with leading zeros like "01" but it should reject it.
  - Severity: Low