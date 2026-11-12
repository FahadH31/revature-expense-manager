# Revature Expense Manager

Our console-based expense reimbursement system with two apps sharing a single SQLite database:
- **Python Employee App**: employees submit, view, edit, and delete their expense claims.
- **Java Manager App**: managers view pending expenses, approve/deny them with comments, and view/export spending reports.

---

## Prerequisites/Technical Versions

- Python 3.10+
- Java 17+ and Maven
- SQLite3

---

## 1. Database Setup

Both apps require an environment variable named `EM_DB_PATH` pointing to the SQLite database file.

### Set `EM_DB_PATH`

**Windows (PowerShell):**
```powershell
$env:EM_DB_PATH = "$PWD\expense_manager.db"
```

**Windows (CMD):**
```cmd
set EM_DB_PATH=%cd%\expense_manager.db
```

**Linux / Mac:**
```bash
export EM_DB_PATH="$(pwd)/expense_manager.db"
```

### Initialize and Seed Database

Run the schema and seed scripts using sqlite3:

```bash
sqlite3 expense_manager.db < sql/schema.sql
sqlite3 expense_manager.db < sql/seed.sql
```

Alternatively, you can run the Java seeder class from `manager-app-java`:
```bash
cd manager-app-java
mvn compile exec:java -Dexec.mainClass="db.DatabaseSeeder"
```

---

## 2. Test Accounts

| Role | Username | Password | Full Name | Notes |
|---|---|---|---|---|
| Manager | `fahadh` | `fahad123` | Fahad Hussain | Logs into Java app only |
| Manager | `divyan03` | `divyan123` | Divyan Varatharaj | Logs into Java app only |
| Employee | `test_emp_1` | `test123` | Test Employee 1 | Logs into Python app only |
| Employee | `test_emp_2` | `test123` | Test Employee 2 | Logs into Python app only |
| Employee | `test_emp_3` | `test123` | Test Employee 3 | Logs into Python app only |
| Employee | `test_emp_4` | `test123` | Test Employee 4 | Logs into Python app only |

*Note: Roles are checked on login. Entering employee credentials in the Java app or manager credentials in the Python app blocks access. 3 consecutive failed login attempts will exit the app.*

---

## 3. Python Employee App

### Setup

```bash
cd employee-app-python

# Create and activate virtual environment
python -m venv venv

# Windows
.\venv\Scripts\activate

# Mac/Linux
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
pip install pytest pytest-html
```

### Run the App

Make sure `EM_DB_PATH` is set in your terminal:
```bash
python employee_app/main.py
```

### Run Tests

```bash
# Run all tests
pytest

# Run with verbose output
pytest -v

# Run by marker
pytest -m smoke
pytest -m boundary
pytest -m negative
pytest -m integration

# Generate HTML test report
pytest --html=report.html --self-contained-html
```

---

## 4. Java Manager App

### Build and Run

```bash
cd manager-app-java

# Compile
mvn clean compile

# Run the app
mvn exec:java -Dexec.mainClass="Main"
```

### Run Tests

```bash
# Run all 51 tests
mvn test

# Run specific tag
mvn test -Dgroups="smoke"
mvn test -Dgroups="boundary"
mvn test -Dgroups="integration"

# Exclude integration tests
mvn test -DexcludedGroups="integration"

# Generate Surefire HTML test report
mvn surefire-report:report
# Report output: target/site/surefire.html
```

---

## 5. Main Rules / Constraints

- **Amount**: Must be between `0.01` and `10000.00` (max 2 decimal places).
- **Date**: Format `YYYY-MM-DD`. Cannot be in the future, cannot be older than 90 days. Pressing Enter defaults to today.
- **Description**: 1 to 200 characters.
- **Approvals**:
  - Approving: comment is optional (up to 200 chars).
  - Denying: comment is required (5 to 200 chars).
- **Reports**: Reports can be grouped by employee or category and exported to a CSV in `reports/`.

---

## Project Structure

```
├── sql/
│   ├── schema.sql
│   └── seed.sql
├── employee-app-python/
│   ├── employee_app/          # Python app source code
│   ├── tests/                 # PyTest test files
│   ├── requirements.txt
│   └── pytest.ini
├── manager-app-java/
│   ├── pom.xml
│   └── src/
│       ├── main/java/         # Java app source code
│       └── test/java/         # JUnit 5 & Mockito tests
├── docs/                      # Test cases, RTM, test plan
└── reports/                   # Exported CSV reports
```
