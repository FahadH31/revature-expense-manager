# Revature Expense Manager Test Plan

## Scope
Employee App in Python and Manager App in Java. All 10 user stories under 5.1 (employee app) and 5.2 (manager app) from project requirements document. All functional requirements under 6.1, 6.2, 6.3, 6.4 (authentication, employee app, manager app, system, respectively). Out of scope is UI styling/frontend, browser automation, API testing, CI/CD, containers, password hashing or real security, handling many users at the same time.

## Test Types
- Unit tests: validators and services in isolation
- Integration tests: services + real SQLite database
- Manual/exploratory: full flows through the UI

## Tools
- Python: pytest, pytest-html (for reports), pytest-mock
- Java: JUnit 5, Mockito 

## Entry Criteria
App is fully functional post-demo, user stories under 5.1 and 5.2 completed.

## Exit Criteria
All user stories/functional requirements covered in RTM, all tests green, HTML reports generated.

## Roles
Both of us (Fahad and Divyan) will work on Python & Java tests, both will do at least one exploratory testing session.  