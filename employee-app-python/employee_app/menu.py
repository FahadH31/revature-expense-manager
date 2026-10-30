import validators
from services import login, submit_expense, get_expenses
from exceptions import AuthenticationError, AuthorizationError


HEADER_DIVIDER = "=" * 60
DIVIDER = "-" * 60

# login loop
def run_login(connection):
    attempts = 0

    while attempts < 3:
        username = input("Enter username: ").strip() 
        password = input("Enter password: ").strip()

        # if username or password left blank, don't count as attempt and reloop
        if not username or not password:
            print("Username or password left blank.")
            continue

        try:
            user = login(username, password, connection)        # login() returns the row as a dictionary
            print(f"Welcome, {user['full_name']}")              # need to print full name from DB here.
            return user                                         # return the row for next loop execution from main.py
        except AuthorizationError as error:
            print(error)
            exit(0)
        except AuthenticationError as error: 
            print(error)
            attempts += 1
    
    print("Too many failed attempts.")
    exit(1)


# main app loop                                                                                                                                                    
def run_menu(user, connection):                                                                                                                                    
    options = {                                                                                                                                                    
        "1": "Submit expense",                                                                                                                                     
        "2": "View my expenses",                                                                                                                                   
        "3": "Edit pending expense",                                                                                                                               
        "4": "Delete pending expense",                                                                                                                             
        "5": "Review history",                                                                                                                                     
        "0": "Exit"                                                                                                                                                
    }                                                                                                                                                              
                                                                                                                                                                    
    while True:                                                                                                                                                    
        # print the menu options                                                                                                                                   
        print("\nOptions", HEADER_DIVIDER)                                                                                                                              
        for option in options:                                                                                                                                     
            print(option, options[option])                                                                                                                         
        print(HEADER_DIVIDER, "\n")                                                                                                                              
        user_input = input("Choose an option: ").strip()                                                                                                           
                                                                                                                                                                    
        if user_input == "0":                                                                                                                                      
            print("Exiting.")                                                                                                                                      
            break                                                                                                                                                  
        elif user_input == "1":                                                                                                                                    
            print("Submit expense option chosen.")                                                                                                                                                                                                                                           
                                                                                                                                                                    
            # 1. Validate amount                                                                                                                                   
            while True:                                                                                                                                            
                amount = input("Enter the amount (e.g. '54.99'): ").strip()                                                                                        
                if validators.validate_amount(amount):                                                                                                             
                    break                                                                                                                                          
                print("Invalid amount. Must be > 0 and <= 10,000.00 with up to 2 decimal places.")                                                                 
                                                                                                                                                                    
            # Get and print category options, then validate choice                                                                                                 
            cat_rows = connection.execute("SELECT id, name FROM categories ORDER BY id").fetchall()                                                                
            valid_ids = [row["id"] for row in cat_rows]                                                                                                            
            print("\nAvailable Categories:")                                                                                                                       
            for cat in cat_rows:                                                                                                                                   
                print(f"  {cat['id']}. {cat['name']}")                                                                                                             
                                                                                                                                                                    
            while True:                                                                                                                                            
                category = input("Enter the ID of expense category (e.g. '3'): ").strip()                                                                          
                if validators.validate_category(category, valid_ids):                                                                                              
                    break                                                                                                                                          
                print(f"Invalid category ID. Must be one of: {valid_ids}.")                                                                                        
                                                                                                                                                                    
            # 2. Validate description                                                                                                                              
            while True:                                                                                                                                            
                description = input("Enter a description (max 200 chars): ").strip()                                                                               
                if validators.validate_description(description):                                                                                                   
                    break                                                                                                                                          
                print("Invalid description. Entry must be between 1 and 200 characters.")                                                                          
                                                                                                                                                                    
            # 3. Validate expense date                                                                                                                             
            while True:                                                                                                                                            
                expense_date = input("Enter the date of the expense (YYYY-MM-DD). If the date is today, hit enter: ").strip()                                      
                if validators.validate_date(expense_date):                                                                                                         
                    break                                                                                                                                          
                print("Invalid date. Must follow YYYY-MM-DD format and cannot be in the future or older than 90 days.")                                            
                                                                                                                                                                    
            new_id = submit_expense(user, amount, category, description, expense_date, connection)                                                                 
            print(f"Successfully submitted expense with ID: {new_id}")                                                                                             
                                                                                                                                                                    
        elif user_input == "2":                                                                                                                                    
            print("Get expenses option chosen.")                                                                                                                                                                                                                                             
            status_filter = input("Enter a status to filter by (leave blank for no filter): ").strip().lower()                                                     
            if not status_filter:                                                                                                                                  
                status_filter = None                                                                                                                               
                                                                                                                                                                    
            expenses = get_expenses(user, connection, status_filter)                                                                                               
            if not expenses:                                                                                                                                       
                print("No expenses found.")                                                                                                                        
            else:                                                                                                                                                  
                print(f"\n{'ID':<5} | {'Date':<12} | {'Category':<16} | {'Amount':<10} | {'Status':<10} | {'Description'}")                                        
                print("-" * 100)                                                                                                                                    
                for exp in expenses:                                                                                                                               
                    print(f"{exp['id']:<5} | {exp['expense_date']:<12} | {exp['category']:<16} | ${exp['amount']:<9.2f} | {exp['status']:<10} | {exp['description']}")                                                                                                                                            
        else:                                                                                                                                                      
            print("Invalid choice. Please try again.")                                                                                                             
            continue                    
    
    # call the right handler function based on choice