package libManSys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BorrowedBooks extends JFrame {

    private static final long serialVersionUID = 1L;
    private Connection conn;
    private int accountId;

    private JTable table;
    private DefaultTableModel tableModel;

    public BorrowedBooks(Connection conn, int accountId) {
        this.conn = conn;
        this.accountId = accountId;

        setTitle("My Borrowed Books");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Book Name", "Author"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // prevent editing
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBorrowedBooks();
    }

    private void loadBorrowedBooks() {
        try {
            tableModel.setRowCount(0);

            String sql = 
                "SELECT t.book_id, t.book_name, b.book_author " +
                "FROM transactions t " +
                "JOIN books b ON t.book_id = b.book_id " +
                "WHERE t.account_id = ? AND t.transaction_type = 'borrow' " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM transactions t2 " +
                "    WHERE t2.account_id = t.account_id " +
                "    AND t2.book_id = t.book_id " +
                "    AND t2.transaction_type = 'return' " +
                "    AND t2.transaction_id > t.transaction_id" +
                ")";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, accountId);
            ResultSet rs = pst.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                tableModel.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("book_author")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this, 
                    "You have no borrowed books currently.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            pst.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading borrowed books: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
