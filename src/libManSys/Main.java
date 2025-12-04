package libManSys;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        DbConnect dbCon = new DbConnect();
        dbCon.connect();

        if (dbCon.con == null) {
            System.err.println("Database connection was not established. Exiting.");
            return;
        }

        String queryAll = "SELECT * FROM account";

        try (Connection con = dbCon.con;
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(queryAll)) {

            System.out.println("Query executed successfully. Printing results:");
            while (rs.next()) {

                int id = rs.getInt("account_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String role = rs.getString("role");


                System.out.println("ID: " + id + ", First Name: " + firstName + ", Last Name: " + lastName + ", Role: " + role);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query or processing results!");
            e.printStackTrace();
        }
    }
}