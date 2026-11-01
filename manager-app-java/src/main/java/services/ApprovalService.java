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
                                                                                                                                                                
    public void decide(User manager, int expenseId, String decision, String comment, Connection conn) throws SQLException {                                                                                   
        // Only a user with the manager role can approve or deny                                                                            
        if (manager == null || !"manager".equalsIgnoreCase(manager.getRole())) {                                                                                
            throw new AuthorizationException("User is not authorized to review expenses.");                                                                     
        }                                                                                                                                                       
                                                                                                                                                                
        // Query database for current status                                                                                             
        String currentStatus = approvalDao.findStatus(expenseId, conn);                                                                                         
        if (currentStatus == null) {                                                                                                                            
            throw new NotFoundException("Expense with ID " + expenseId + " not found.");                                                                        
        }                                                                                                                                                       
        if (!"pending".equalsIgnoreCase(currentStatus)) {                                                                                                       
            throw new InvalidStateException("Expense is no longer pending.");                                                                                   
        }                                                                                                                                                       
                                                                                                                                                                
        // Normalize input to 'approved' or 'denied'                                                                                    
        String cleanDecision = decision == null ? "" : decision.trim().toLowerCase();                                                                           
        String finalStatus;                                                                                                                                     
        if ("a".equals(cleanDecision) || "approved".equals(cleanDecision)) {                                                                                    
            finalStatus = "approved";                                                                                                                           
        } else if ("d".equals(cleanDecision) || "denied".equals(cleanDecision)) {                                                                               
            finalStatus = "denied";                                                                                                                             
        } else {                                                                                                                                                
            throw new ValidationException("Decision must be 'a' (approve) or 'd' (deny).");                                                                     
        }                                                                                                                                                       
                                                                                                                                                                
        // Comment Validation                                                                                                                
        String trimmedComment = comment == null ? "" : comment.trim();                                                                                          
        if ("denied".equals(finalStatus)) {                                                                                                                     
            if (trimmedComment.length() < 5 || trimmedComment.length() > 200) {                                                                                 
                throw new ValidationException("Denial requires a comment between 5 and 200 characters.");                                                       
            }                                                                                                                                                   
        } else {                                                                                                                                                
            // comment is optional, but if provided cannot exceed 200 chars                                                                          
            if (trimmedComment.length() > 200) {                                                                                                                
                throw new ValidationException("Comment cannot exceed 200 characters.");                                                                         
            }                                                                                                                                                   
        }                                                                                                                                                       
                                                                                                                                                                
        // If empty on approval, persist null so database reflects an empty comment cleanly                                                                     
        String commentToSave = trimmedComment.isEmpty() ? null : trimmedComment;                                                                                
                                                                                                                                                                
        // Date generation (YYYY-MM-DD format)                                                                                                               
        String reviewDate = LocalDate.now().toString();                                                                                                         
                                                                                                                                                                
        // Persist to database                                                                                                                               
        approvalDao.saveDecision(expenseId, finalStatus, manager.getId(), commentToSave, reviewDate, conn);                                                     
    }                                                                                                                                                           
}                                      