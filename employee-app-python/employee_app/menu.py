from services import login
from exceptions import AuthenticationError, AuthorizationError

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
        except AuthenticationError as error: 
            print(error)
            attempts += 1
    
    print("Too many failed attempts.")
    exit(1)


# main app loop (incomplete)
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
        # read input
        # call the right handler function based on choice
        # if invalid choice print message and loop again
        pass