from dotenv import load_dotenv
load_dotenv()
from db import get_connection
from menu import run_login # , run_menu


def main():
    """
    Entry function for starting main application console menus.
    
    - Uses get_connection() from db.py to open up the database connection
    - Then runs the login loop with the database connection
    - Then if successfully returns, runs the main menu for the app (not yet implemented)
    """
    with get_connection() as connection:   
        user = run_login(connection)    # run login loop
        # run_menu(user, connection)      # run main app menu once logged in 

if __name__ == "__main__":
    main()