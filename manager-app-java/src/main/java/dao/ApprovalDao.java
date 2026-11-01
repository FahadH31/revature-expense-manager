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

        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, expenseId);
            
            try(ResultSet resultSet = statement.executeQuery()){
                if(!resultSet.next()){
                    return null;
                }
                
                return resultSet.getString("status");
            }
        } 
    }

    public void saveDecision(int expenseId, String status, int reviewerId, String comment, String reviewDate, Connection conn) throws SQLException{
        String sql = """
        UPDATE approvals
        SET status = ?, reviewer_id = ?, comment = ?, review_date = ? 
        WHERE expense_id = ?
        """;
        
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, status);
            statement.setInt(2, reviewerId);
            statement.setString(3, comment);
            statement.setString(4, reviewDate);
            statement.setInt(5, expenseId);

            statement.executeUpdate();
        } 
    }

}