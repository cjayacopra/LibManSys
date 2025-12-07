package libManSys;

import java.sql.*;

public class DbConnect {

    private final String url = "jdbc:mysql://localhost:3306/LibManSys";
    private final String username = "root";
    private final String password = "";

    public Connection con;

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}