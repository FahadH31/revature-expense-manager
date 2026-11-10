package validators;

public final class InputValidator {
    private InputValidator() {}

    // validate expense ID is not null, blank, or a negative number
    public static boolean isValidExpenseId(String input){
        try {
            if (input == null || input.isBlank()){
                throw new NumberFormatException("ID cannot be null or empty.");
            }
            
            int num = Integer.parseInt(input.trim());

            if (num <= 0){
                throw new NumberFormatException("ID cannot be a negative number.");
            }

        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    // make sure menu option is in range, not empty, and a whole number
    public static boolean isValidMenuChoice(String input){
        try {
            if((input == null) || input.isBlank()){
                throw new NumberFormatException("Menu choice cannot be null or empty");
            }

            int num = Integer.parseInt(input.trim());

            if((num < 1) || (num > 3)){
                return false;
            }  

            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDenialComment(String input){
        if(input == null){
            return false;
        }
        String trimmed = input.trim();

        if((trimmed.length() >= 5) && (trimmed.length() <= 200)){
            return true;
        }

        return false;        
    }

    public static boolean isValidApprovalComment(String input){
        if((input == null) || input.trim().isBlank()){
            return true;
        } else {
            if (input.trim().length() <=200){
                return true;
            }
            return false;
        }
    }
}