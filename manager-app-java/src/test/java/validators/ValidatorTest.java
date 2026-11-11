package validators;
import org.junit.jupiter.api.DisplayName;                                                                                                               
import org.junit.jupiter.api.Tag;                                                                                                                       
import org.junit.jupiter.params.ParameterizedTest;                                                                                                      
import org.junit.jupiter.params.provider.CsvFileSource;                                                                                                 
import static org.junit.jupiter.api.Assertions.assertEquals;   

public class ValidatorTest {
    // Denial comment boundaries    
    @ParameterizedTest
    @Tag("boundary")
    @CsvFileSource(resources = "/denial_comment_tests.csv", numLinesToSkip = 1, nullValues = "null")
    @DisplayName("Covers VR-05 (denial comment boundaries)")
    void testDenialCommentBoundaries(String comment, boolean expected){
        assertEquals(expected, InputValidator.isValidDenialComment(comment));
    }
    
    // Approval comment boundaries
    @ParameterizedTest
    @Tag("boundary")
    @CsvFileSource(resources = "/approval_comment_tests.csv", numLinesToSkip = 1, nullValues = "null")
    @DisplayName("Covers VR-06 (approval comment boundaries)")
    void testApprovalCommentBoundaries(String comment, boolean expected){
        assertEquals(expected, InputValidator.isValidApprovalComment(comment));
    }

    // Shared employee/manager validation rows (tests for menu options and expense IDs)
    @ParameterizedTest
    @Tag("smoke")
    @CsvFileSource(resources = "/shared_validation_cases.csv", numLinesToSkip = 1)
    @DisplayName("Covers VR-08, VR-09 (shared cross app validation cases)")
    void testSharedValidationCases(String field, String input, boolean expected){
        if ("expense_id".equalsIgnoreCase(field)) {
            assertEquals(expected, InputValidator.isValidExpenseId(input));
        } else if ("menu_choice".equalsIgnoreCase(field)) {
            assertEquals(expected, InputValidator.isValidMenuChoice(input));
        }
    }
}