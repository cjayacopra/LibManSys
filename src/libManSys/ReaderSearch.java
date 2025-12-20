package libManSys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.*;

public class ReaderSearch extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JPanel sidebar;
    private int accountId;
    private String readerName;
    private String email;
    private DbConnect dbConnect;
    private Connection conn;
    private JTextField txtSearchBooks;
    private JTable tableBooks;
    private boolean isDarkMode = false;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	UIManager.setLookAndFeel(new FlatIntelliJLaf());
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
        fetchUserData();
        
        // Initialize UI
        initialize();
        
        // Load all books initially
        loadBooks("");
    }
    
    private void fetchUserData() {
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
        }
    }

    private void initialize() {
        setTitle("Search Books - LibManSys");
        setBounds(100, 100, 1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        sidebar = createSidebar();
        getContentPane().add(sidebar, BorderLayout.WEST);

        // Main Panel
        JPanel mainPanel = createMainPanel();
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        updateTheme();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("LibManSys");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subtitleLabel = new JLabel("Book Search");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidebar.add(subtitleLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel userLabel = new JLabel("User: " + readerName);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidebar.add(userLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back to Dashboard button
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, backButton.getPreferredSize().height));
        backButton.addActionListener(e -> backToDashboard());
        sidebar.add(backButton);

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        
        // Theme toggle button
        JButton themeToggleButton = new JButton("Toggle Theme");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 16));
        themeToggleButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        themeToggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, themeToggleButton.getPreferredSize().height));
        themeToggleButton.addActionListener(e -> toggleTheme());
        sidebar.add(themeToggleButton);

        return sidebar;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Search Books in Library", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel searchLabel = new JLabel("Search by Title or Author:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        
        JPanel searchFieldPanel = new JPanel(new BorderLayout(10, 0));
        txtSearchBooks = new JTextField();
        txtSearchBooks.setFont(new Font("Arial", Font.PLAIN, 16));
        txtSearchBooks.setPreferredSize(new Dimension(0, 35));
        searchFieldPanel.add(txtSearchBooks, BorderLayout.CENTER);

        JButton btnSearchBooks = new JButton("Search");
        btnSearchBooks.setFont(new Font("Arial", Font.BOLD, 14));
        btnSearchBooks.setPreferredSize(new Dimension(100, 35));
        btnSearchBooks.addActionListener(e -> {
            String keyword = txtSearchBooks.getText().trim();
            loadBooks(keyword);
        });
        searchFieldPanel.add(btnSearchBooks, BorderLayout.EAST);
        
        searchPanel.add(searchFieldPanel, BorderLayout.CENTER);
        
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Books"));
        
        tableBooks = new JTable();
        tableBooks.setFont(new Font("Arial", Font.PLAIN, 14));
        tableBooks.setRowHeight(25);
        tableBooks.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableBooks.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableBooks.getTableHeader().setReorderingAllowed(false);
        
        // Center align table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(tableBooks);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JLabel infoLabel = new JLabel("Double-click a row to view book details");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoPanel.add(infoLabel);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Live search on key release
        txtSearchBooks.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String keyword = txtSearchBooks.getText().trim();
                loadBooks(keyword);
            }
        });
        
        // Double-click to view details
        tableBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = tableBooks.getSelectedRow();
                    if (selectedRow != -1) {
                        showBookDetails(selectedRow);
                    }
                }
            }
        });

        return mainPanel;
    }

    private void loadBooks(String keyword) {
        try {
            String sql = "SELECT * FROM books WHERE book_name LIKE ? OR book_author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String likeSearch = "%" + keyword + "%";
            stmt.setString(1, likeSearch);
            stmt.setString(2, likeSearch);

            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };
            
            model.addColumn("Book ID");
            model.addColumn("Title");
            model.addColumn("Author");
            model.addColumn("Category");
            model.addColumn("Issue Date");

            int rowCount = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("book_author"),
                    rs.getString("book_category"),
                    rs.getDate("issue_date")
                });
                rowCount++;
            }

            tableBooks.setModel(model);
            
            // Center align numeric columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            tableBooks.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Book ID
            
            // Set column widths
            tableBooks.getColumnModel().getColumn(0).setPreferredWidth(80);  // Book ID
            tableBooks.getColumnModel().getColumn(1).setPreferredWidth(250); // Title
            tableBooks.getColumnModel().getColumn(2).setPreferredWidth(200); // Author
            tableBooks.getColumnModel().getColumn(3).setPreferredWidth(150); // Category
            tableBooks.getColumnModel().getColumn(4).setPreferredWidth(120); // Issue Date
            
            // Update info label
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(this,
                    "No books found matching your search criteria.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            stmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to load books: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showBookDetails(int row) {
        int bookId = (int) tableBooks.getValueAt(row, 0);
        String title = (String) tableBooks.getValueAt(row, 1);
        String author = (String) tableBooks.getValueAt(row, 2);
        String category = (String) tableBooks.getValueAt(row, 3);
        Object issueDate = tableBooks.getValueAt(row, 4);
        
        String details = "BOOK DETAILS\n\n" +
                        "Book ID: " + bookId + "\n" +
                        "Title: " + title + "\n" +
                        "Author: " + author + "\n" +
                        "Category: " + category + "\n" +
                        "Issue Date: " + (issueDate != null ? issueDate.toString() : "N/A");
        
        int option = JOptionPane.showOptionDialog(this,
            details,
            "Book Details",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Borrow This Book", "Close"},
            "Close");
        
        if (option == 0) { // Borrow This Book
            openBorrowBook();
        }
    }

    private void backToDashboard() {
        try {
            if (conn != null && !conn.isClosed()) {
                // Don't close connection, let Reader_dashboard manage it
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        this.dispose();
        Reader_dashboard dashboard = new Reader_dashboard(email);
        dashboard.setVisible(true);
    }
    
    private void openBorrowBook() {
        BookBorrow borrowFrame = new BookBorrow(conn, accountId, email);
        borrowFrame.setVisible(true);
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        updateTheme();
    }

    private void updateTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);

            // Apply custom sidebar colors
            if (isDarkMode) {
                Color bgColor = new Color(23, 35, 51);
                Color fgColor = Color.WHITE;
                sidebar.setBackground(bgColor);
                for (Component comp : sidebar.getComponents()) {
                    if (comp instanceof JLabel || comp instanceof JButton) {
                        comp.setForeground(fgColor);
                    }
                    if (comp instanceof JButton) {
                        comp.setBackground(bgColor);
                        ((JButton) comp).setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                    }
                }
            } else {
                sidebar.setBackground(UIManager.getColor("Panel.background"));
                for (Component comp : sidebar.getComponents()) {
                    if (comp instanceof JLabel) {
                        comp.setForeground(UIManager.getColor("Label.foreground"));
                    } else if (comp instanceof JButton) {
                        comp.setForeground(UIManager.getColor("Button.foreground"));
                        comp.setBackground(UIManager.getColor("Button.background"));
                        ((JButton) comp).setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}