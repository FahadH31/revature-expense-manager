# Java Prompt Log

## 2026-10-29
Prompt: Where should .env be for these apps to test database setup files

Output:               
  For Java, just set it in your terminal before running Maven. The simplest approach:                                                                                  
                                                                                                                                                                       
    # Set it once per terminal session                                                                                                                                 
    $env:EM_DB_PATH = "C:\Users\write\Desktop\Code\Revature\Project_1\revature-expense-manager\expense.db"                                                             
                                                                                                                                                                       
    # Then run                                                                                                                                                         
    mvn exec:java -Dexec.mainClass="Main"                                                                                                                              
                                                                                                                                                                       
  Or add it permanently to your system environment variables (search "Edit environment variables" in Windows) so you never have to set it again.                       
  ──────                                                                                                                                                               
  ## For tests specifically                                                                                                                                            
                                                                                                                                                                       
  Your tests should not use EM_DB_PATH at all. Tests create their own temporary database:                                                                              
                                                                                                                                                                       
  • Python: The conftest.py fixture creates a temp file using tmp_path and passes it directly to get_connection(path=...) — bypasses the env var entirely.             
  • Java: Tests call Database.connect("path/to/temp.db") directly — never call Database.dbPath().                                                                      
                                                                                                                                                                       
  This way tests always run clean regardless of what EM_DB_PATH is set to.                                                                                             


Prompt: So do I need to make a .env file?

Output:
▸ Thought for 1s
  No, you don't have to. A .env file is just a convenience so you don't retype the $env:EM_DB_PATH = "..." command every time you open a new terminal.                 
                                                                                                                                                                       
  The project spec only requires that EM_DB_PATH is set as an environment variable. How you set it doesn't matter.                                                     
  ──────                                                                                                                                                               
  Your two real options are:                                                                                                                                           
                                                                                                                                                                       
  Option 1 — Just set it in the terminal each session (no extra setup)                                                                                                 
                                                                                                                                                                       
    $env:EM_DB_PATH = "C:\Users\write\Desktop\Code\Revature\Project_1\revature-expense-manager\expense.db"                                                             
                                                                                                                                                                       
  Run this once when you open a terminal and you're good for that session.                                                                                             
                                                                                                                                                                       
  Option 2 — Set it permanently in Windows (never think about it again)                                                                                                
                                                                                                                                                                       
  1. Search "Edit the system environment variables" in the Start menu                                                                                                  
  2. Click Environment Variables                                                                                                                                       
  3. Under User variables, click New                                                                                                                                   
  4. Name: EM_DB_PATH | Value: the full path to your expense.db file                                                                                                   
  5. Click OK                                                                                                                                                          
                                                                                                                                                                       
  Once set permanently, both apps and your tests will find it automatically with no extra steps.                                                                       
  ──────                                                                                                                                                               
  Bottom line: Skip the .env file entirely. Just set EM_DB_PATH permanently in Windows environment variables and move on.


## 2026-10-30
Prompt: Cannot make a static reference to the non-static method login(String, String, Connection) from the type AuthService. I get this IDE error on line 27 in Menu.java

Output: 
login() in AuthService is an instance method, not a static method. You are calling it incorrectly here:

AuthService.login(username, password, conn);

Create an AuthService object first, then call the method on that object:

private final AuthService authService = new AuthService();

Then line 27 should be:

User user = authService.login(username, password, conn);

That is why the IDE reports “Cannot make a static reference…”—AuthService.login(...) tries to call an instance method through the class name.

## 2026-11-01 (Fahad)
Prompt: Review my implementation of ApprovalDao.java for correctness, and complete the decide() function for me in ApprovalService.java (give me a full easy to understand explanation of your code and why, in the context that I am more familiar with python)

Output:
### 1. Code Review of `ApprovalDao.java`

Your DAO has the right general idea, but there are a few syntax, typing, and JDBC bugs preventing it from compiling and running:

#### Issues in `findStatus()`:
1. **Wrong type on parameter**: `statement.setString(1, expenseId);` — In Java (unlike Python), types are strictly checked. `expenseId` is an `int`, so you must use `statement.setInt(1, expenseId)`.
2. **Missing semicolon**: Line 24 `return resultSet.getString("status")` is missing a `;`.

#### Issues in `saveDecision()`:
3. **SQL syntax error (extra comma)**:
   ```sql
   SET status = ?, reviewer_id = ?, comment = ?, review_date = ?, WHERE expense_id = ?
   --                                                            ^ Trailing comma before WHERE
   ```
4. **Missing semicolon**: Line 33 missing a `;` after `"""`.
5. **Wrong types on parameters**:
   * Line 37: `reviewerId` is an `int` ➔ `statement.setInt(2, reviewerId)`.
   * Line 40: You wrote `statement.setString(5, conn)`. The 5th placeholder is `WHERE expense_id = ?`, and `conn` is the database connection object. It should be `statement.setInt(5, expenseId)`.
6. **`executeQuery()` vs `executeUpdate()` & return type mismatch**:
   * `executeQuery()` is **only** for `SELECT` queries that produce a `ResultSet`.
   * `UPDATE`, `INSERT`, and `DELETE` queries must use `statement.executeUpdate()`.
   * The method return type is `void`, but the inner block attempts to `return resultSet.getString("status")` and `return null`, which is a compiler error.

---

#### Corrected `ApprovalDao.java`

You can update ApprovalDao.java with this:

```java
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApprovalDao {

    public String findStatus(int expenseId, Connection conn) throws SQLException {
        String sql = """
            SELECT status
            FROM approvals
            WHERE expense_id = ?
            """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, expenseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return resultSet.getString("status");
            }
        }
    }

    public void saveDecision(int expenseId, String status, int reviewerId,
                             String comment, String reviewDate, Connection conn) throws SQLException {
        String sql = """
            UPDATE approvals
            SET status = ?, reviewer_id = ?, comment = ?, review_date = ?
            WHERE expense_id = ?
            """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, reviewerId);
            statement.setString(3, comment);
            statement.setString(4, reviewDate);
            statement.setInt(5, expenseId);

            statement.executeUpdate();
        }
    }
}
```

---

### 2. Complete Implementation of `ApprovalService.java`

Here is the complete implementation for ApprovalService.java

```java
package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import dao.ApprovalDao;
import exceptions.AuthorizationException;
import exceptions.InvalidStateException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.User;

public class ApprovalService {
    private final ApprovalDao approvalDao;

    public ApprovalService() {
        this(new ApprovalDao());
    }

    public ApprovalService(ApprovalDao approvalDao) {
        this.approvalDao = approvalDao;
    }

    public void decide(User manager, int expenseId, String decision,
                       String comment, Connection conn) throws SQLException {
        // 1. Authorization: Only a user with the 'manager' role can approve or deny
        if (manager == null || !"manager".equalsIgnoreCase(manager.getRole())) {
            throw new AuthorizationException("User is not authorized to review expenses.");
        }

        // 2. Existence & Status: Query database for current status
        String currentStatus = approvalDao.findStatus(expenseId, conn);
        if (currentStatus == null) {
            throw new NotFoundException("Expense with ID " + expenseId + " not found.");
        }
        if (!"pending".equalsIgnoreCase(currentStatus)) {
            throw new InvalidStateException("Expense is no longer pending.");
        }

        // 3. Decision Validation: Normalize input to 'approved' or 'denied'
        String cleanDecision = decision == null ? "" : decision.trim().toLowerCase();
        String finalStatus;
        if ("a".equals(cleanDecision) || "approved".equals(cleanDecision)) {
            finalStatus = "approved";
        } else if ("d".equals(cleanDecision) || "denied".equals(cleanDecision)) {
            finalStatus = "denied";
        } else {
            throw new ValidationException("Decision must be 'a' (approve) or 'd' (deny).");
        }

        // 4. Comment Validation (VR-05 & VR-06)
        String trimmedComment = comment == null ? "" : comment.trim();
        if ("denied".equals(finalStatus)) {
            if (trimmedComment.length() < 5 || trimmedComment.length() > 200) {
                throw new ValidationException("Denial requires a comment between 5 and 200 characters.");
            }
        } else {
            // Approvals: comment is optional, but if provided cannot exceed 200 chars
            if (trimmedComment.length() > 200) {
                throw new ValidationException("Comment cannot exceed 200 characters.");
            }
        }

        // If empty on approval, persist null so database reflects an empty comment cleanly
        String commentToSave = trimmedComment.isEmpty() ? null : trimmedComment;

        // 5. Date generation (YYYY-MM-DD format)
        String reviewDate = LocalDate.now().toString();

        // 6. Persist to database
        approvalDao.saveDecision(expenseId, finalStatus, manager.getId(), commentToSave, reviewDate, conn);
    }
}
```

---

### 3. Step-by-Step Explanation (Coming from Python)

#### Constructors & Dependency Injection
```java
public ApprovalService() {
    this(new ApprovalDao());
}

public ApprovalService(ApprovalDao approvalDao) {
    this.approvalDao = approvalDao;
}
```
* In Python, you might write `def __init__(self, approval_dao=None): self.approval_dao = approval_dao or ApprovalDao()`.
* In Java, constructor chaining (`this(...)`) allows normal code to call `new ApprovalService()` while unit tests in Phase 2 can inject a mock DAO: `new ApprovalService(mockDao)`.

#### Step 1: Authorization Check (`FR-A4`)
```java
if (manager == null || !"manager".equalsIgnoreCase(manager.getRole())) {
    throw new AuthorizationException("User is not authorized to review expenses.");
}
```
* **Why**: The project plan specifically states that even if the CLI menu only lets managers click "Review", business logic must never trust the UI. If a non-manager object is passed into `decide()`, it immediately throws `AuthorizationException`.
* `equalsIgnoreCase()` avoids issues if the database role is `"Manager"` vs `"manager"`.

#### Step 2: Existence & State Verification (`US-M3`)
```java
String currentStatus = approvalDao.findStatus(expenseId, conn);
if (currentStatus == null) {
    throw new NotFoundException("Expense with ID " + expenseId + " not found.");
}
if (!"pending".equalsIgnoreCase(currentStatus)) {
    throw new InvalidStateException("Expense is no longer pending.");
}
```
* `findStatus` returns `null` if the row doesn't exist ➔ throws `NotFoundException`.
* If it returns `"approved"` or `"denied"` ➔ throws `InvalidStateException`. This protects against race conditions (e.g. two managers trying to review the same expense at the same time).

#### Step 3: Normalizing Decision Input
```java
String cleanDecision = decision == null ? "" : decision.trim().toLowerCase();
```
* Users may type `"a"`, `"A"`, `"d"`, or `"D"`. Handling `null` first prevents a `NullPointerException` (Java's equivalent of `AttributeError: 'NoneType' object has no attribute 'lower'`).

#### Step 4: Comment Validation Rules (`VR-05` and `VR-06`)
```java
String trimmedComment = comment == null ? "" : comment.trim();
if ("denied".equals(finalStatus)) {
    if (trimmedComment.length() < 5 || trimmedComment.length() > 200) {
        throw new ValidationException("Denial requires a comment between 5 and 200 characters.");
    }
}
```
* **Denials**: A manager cannot reject an employee's money without explaining why (minimum 5 chars, max 200).
* **Approvals**: Comments are optional. If skipped, `commentToSave` becomes `null`, matching SQLite's `NULL` for empty comments.

#### Step 5 & 6: Date & Persistence
```java
String reviewDate = LocalDate.now().toString();
approvalDao.saveDecision(expenseId, finalStatus, manager.getId(), commentToSave, reviewDate, conn);
```
* `LocalDate.now().toString()` produces the ISO `"YYYY-MM-DD"` date string expected by the SQLite schema.
* Finally, it hands off all 5 parameters to the DAO to execute the SQL `UPDATE`.


