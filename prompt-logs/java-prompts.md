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