package libManSys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BookBorrow extends JFrame {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private int accountId;
    private String email;

    private JTextField txtSearch;
    private JTable tableBooks;
    private DefaultTableModel tableModel;

    public BookBorrow(Connection conn, int accountId, String email) {
        this.conn = conn;
        this.accountId = accountId;
        this.email = email;

        setTitle("Borrow a Book");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Books:"));
        txtSearch = new JTextField(30);
        searchPanel.add(txtSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Table Setup
        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Book Name", "Author"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // disable editing
            }
        };
        tableBooks = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableBooks);
        add(scrollPane, BorderLayout.CENTER);

        // Borrow Button
        JButton btnBorrow = new JButton("Borrow Selected Book");
        add(btnBorrow, BorderLayout.SOUTH);

        // Load all books initially
        loadBooks("");

        // Search filter action
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String query = txtSearch.getText();
                loadBooks(query);
            }
        });

        // Borrow button action
        btnBorrow.addActionListener(e -> borrowSelectedBook());
    }

    private void loadBooks(String searchQuery) {
        try {
            tableModel.setRowCount(0); // clear table

            String sql = "SELECT book_id, book_name, book_author FROM books WHERE book_name LIKE ? OR book_author LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            String likeQuery = "%" + searchQuery + "%";
            pst.setString(1, likeQuery);
            pst.setString(2, likeQuery);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("book_author")
                });
            }
            rs.close();
            pst.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading books: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void borrowSelectedBook() {
        int selectedRow = tableBooks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to borrow.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookName = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            // Check if already borrowed and not returned
            String alreadyBorrowedSql = "SELECT book_id FROM transactions " +
                                        "WHERE account_id = ? AND book_id = ? AND transaction_type = 'borrow' " +
                                        "AND book_id NOT IN (" +
                                        "  SELECT book_id FROM transactions " +
                                        "  WHERE account_id = ? AND transaction_type = 'return'" +
                                        ")";
            PreparedStatement alreadyPst = conn.prepareStatement(alreadyBorrowedSql);
            alreadyPst.setInt(1, accountId);
            alreadyPst.setInt(2, bookId);
            alreadyPst.setInt(3, accountId);
            ResultSet alreadyRs = alreadyPst.executeQuery();

            if (alreadyRs.next()) {
                JOptionPane.showMessageDialog(this,
                    "You have already borrowed this book!\nPlease return it first before borrowing again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                alreadyRs.close();
                alreadyPst.close();
                return;
            }
            alreadyRs.close();
            alreadyPst.close();

            // Get contact number
            String contactSql = "SELECT contact_number FROM account WHERE account_id = ?";
            PreparedStatement contactPst = conn.prepareStatement(contactSql);
            contactPst.setInt(1, accountId);
            ResultSet contactRs = contactPst.executeQuery();
            contactRs.next();
            String contactNumber = contactRs.getString("contact_number");
            contactRs.close();
            contactPst.close();

            // Insert borrow transaction
            String sql = "INSERT INTO transactions (transaction_type, book_id, book_name, account_id, contact_number, email, `date`) " +
                         "VALUES ('borrow', ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setString(2, bookName);
            pst.setInt(3, accountId);
            pst.setString(4, contactNumber);
            pst.setString(5, email);
            pst.setDate(6, new java.sql.Date(System.currentTimeMillis()));

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Book borrowed successfully!\n\nBook: " + bookName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            pst.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error borrowing book: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
