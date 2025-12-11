package libManSys;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReaderSearch extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private int accountId;
    private String readerName;
    private String email;
    private DbConnect dbConnect;
    private Connection conn;
    private JTextField txtSearchBooks;
    private JTable tableBooks;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ReaderSearch frame = new ReaderSearch("otelo.nobleza@example.com");
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ReaderSearch(String email) {
        this.email = email;

        dbConnect = new DbConnect();
        dbConnect.connect();
        conn = dbConnect.con;

        if (conn == null) {
            JOptionPane.showMessageDialog(this,
                "Database connection failed!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch user info
        try {
            String sql = "SELECT account_id, first_name, last_name FROM account WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                this.accountId = rs.getInt("account_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                this.readerName = firstName + " " + lastName;
            } else {
                JOptionPane.showMessageDialog(this,
                    "User not found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            rs.close();
            pst.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error fetching user data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        setTitle("Reader Dashboard - Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 248, 255));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBounds(0, 0, 884, 80);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);

        JLabel lblTitle = new JLabel("READER DASHBOARD");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(10, 11, 864, 35);
        headerPanel.add(lblTitle);

        JLabel lblWelcome = new JLabel("Welcome, " + readerName);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(10, 48, 864, 20);
        headerPanel.add(lblWelcome);

        // Main Content Area
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBounds(10, 91, 864, 459);
        contentPane.add(mainPanel);
        mainPanel.setLayout(null);

        // Search text field for books
        txtSearchBooks = new JTextField();
        txtSearchBooks.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtSearchBooks.setBounds(20, 20, 400, 35);
        mainPanel.add(txtSearchBooks);

        // Button for triggering search (optional)
        JButton btnSearchBooks = new JButton("Search");
        btnSearchBooks.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnSearchBooks.setBackground(new Color(100, 149, 237));
        btnSearchBooks.setForeground(Color.WHITE);
        btnSearchBooks.setBounds(430, 20, 100, 35);
        mainPanel.add(btnSearchBooks);

        // Table to show books
        tableBooks = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableBooks);
        scrollPane.setBounds(20, 70, 824, 370);
        mainPanel.add(scrollPane);

        // Load all books initially
        loadBooks("");

        // Search on button click
        btnSearchBooks.addActionListener(e -> {
            String keyword = txtSearchBooks.getText().trim();
            loadBooks(keyword);
        });

        // Also support live search on key release (optional)
        txtSearchBooks.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String keyword = txtSearchBooks.getText().trim();
                loadBooks(keyword);
            }
        });

        // Center frame
        setLocationRelativeTo(null);
    }

    // Method to load books from DB and show in table filtered by keyword (title or author)
    private void loadBooks(String keyword) {
        try {
            // Don't reconnect here; connection is already established in constructor
            // dbConnect.connect();  <-- REMOVE this line

            String sql = "SELECT * FROM books WHERE book_name LIKE ? OR book_author LIKE ?";
            PreparedStatement stmt = dbConnect.con.prepareStatement(sql);  // fixed variable name

            String likeSearch = "%" + keyword + "%";
            stmt.setString(1, likeSearch);
            stmt.setString(2, likeSearch);

            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Book ID");
            model.addColumn("Title");
            model.addColumn("Author");
            model.addColumn("Category");
            model.addColumn("Issue Date");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("book_author"),
                    rs.getString("book_category"),
                    rs.getDate("issue_date")
                });
            }

            tableBooks.setModel(model);   // fixed variable name

            rs.close();
            stmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load books.");
        }
    }
}