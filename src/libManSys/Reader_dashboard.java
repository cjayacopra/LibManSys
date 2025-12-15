package libManSys;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class Reader_dashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    
    private int accountId;
    private String readerName;
    private String email;
    private DbConnect dbConnect;
    private Connection conn;
    
    private boolean isDarkMode = false;
    
    // Content panels
    private JPanel borrowedBooksContentPanel;
    private JPanel transactionHistoryContentPanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Reader_dashboard window = new Reader_dashboard("otelo.nobleza@example.com");
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Reader_dashboard(String email) {
        this.email = email;
        
        // Initialize database connection
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
        
        // Fetch user data from database
        fetchUserData();
        
        // Initialize UI
        initialize();
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
        setTitle("Reader Dashboard - LibManSys");
        setBounds(100, 100, 1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            Image icon = new ImageIcon(getClass().getResource("/assets/LibManSys_Icon.png")).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        sidebar = createSidebar();
        getContentPane().add(sidebar, BorderLayout.WEST);

        // Main Panel with CardLayout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Add all panels
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createSearchBooksPanel(), "Search Books");
        mainPanel.add(createBorrowedBooksPanel(), "Borrowed Books");
        mainPanel.add(createBorrowBookPanel(), "Borrow Book");
        mainPanel.add(createReturnBookPanel(), "Return Book");
        mainPanel.add(createTransactionHistoryPanel(), "Transaction History");
        mainPanel.add(createProfilePanel(), "Profile");

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

        JLabel subtitleLabel = new JLabel("Reader Portal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidebar.add(subtitleLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Navigation buttons
        sidebar.add(createNavButton("Dashboard", "Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Search Books", "Search Books"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("My Borrowed Books", "Borrowed Books"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Borrow a Book", "Borrow Book"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Return a Book", "Return Book"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Transaction History", "Transaction History"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("My Profile", "Profile"));
        
        sidebar.add(Box.createVerticalGlue());
        
        // Theme toggle button
        JButton themeToggleButton = new JButton("Toggle Theme");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 16));
        themeToggleButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        themeToggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, themeToggleButton.getPreferredSize().height));
        themeToggleButton.addActionListener(e -> toggleTheme());
        sidebar.add(themeToggleButton);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, logoutButton.getPreferredSize().height));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + readerName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 100, 100));

        // Quick action cards
        statsPanel.add(createQuickActionCard("Search Books", "Find books in library", new Color(100, 149, 237), () -> cardLayout.show(mainPanel, "Search Books")));
        statsPanel.add(createQuickActionCard("Borrowed Books", "View your borrowed books", new Color(60, 179, 113), () -> {
            cardLayout.show(mainPanel, "Borrowed Books");
            loadBorrowedBooks();
        }));
        statsPanel.add(createQuickActionCard("Borrow Book", "Borrow a new book", new Color(255, 165, 0), () -> cardLayout.show(mainPanel, "Borrow Book")));
        statsPanel.add(createQuickActionCard("Return Book", "Return a borrowed book", new Color(219, 112, 147), () -> cardLayout.show(mainPanel, "Return Book")));

        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuickActionCard(String title, String description, Color bgColor, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);

        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.WHITE);
        descLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private JPanel createSearchBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Search Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton openSearchButton = new JButton("Open Book Search");
        openSearchButton.setFont(new Font("Arial", Font.BOLD, 18));
        openSearchButton.addActionListener(e -> {
            ReaderSearch searchWindow = new ReaderSearch(email);
            searchWindow.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openSearchButton);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("My Borrowed Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBorrowedBooks());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);

        borrowedBooksContentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        JScrollPane scrollPane = new JScrollPane(borrowedBooksContentPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadBorrowedBooks() {
        borrowedBooksContentPanel.removeAll();
        try {
            String sql = "SELECT t.book_id, t.book_name, b.book_author, b.book_category " +
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
            
            boolean hasBooks = false;
            while (rs.next()) {
                hasBooks = true;
                int bookId = rs.getInt("book_id");
                String bookName = rs.getString("book_name");
                String author = rs.getString("book_author");
                String category = rs.getString("book_category");
                borrowedBooksContentPanel.add(createBorrowedBookCard(bookId, bookName, author, category));
            }
            
            if (!hasBooks) {
                JLabel noBooks = new JLabel("You have no borrowed books.", SwingConstants.CENTER);
                noBooks.setFont(new Font("Arial", Font.PLAIN, 18));
                borrowedBooksContentPanel.add(noBooks);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading borrowed books: " + e.getMessage());
        }
        borrowedBooksContentPanel.revalidate();
        borrowedBooksContentPanel.repaint();
    }

    private JPanel createBorrowedBookCard(int bookId, String bookName, String author, String category) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(new JLabel("Book ID: " + bookId));
        detailsPanel.add(new JLabel("Title: " + bookName));
        detailsPanel.add(new JLabel("Author: " + author));
        detailsPanel.add(new JLabel("Category: " + category));
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBorrowBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Borrow a Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton openBorrowButton = new JButton("Open Borrow Form");
        openBorrowButton.setFont(new Font("Arial", Font.BOLD, 18));
        openBorrowButton.addActionListener(e -> {
            BookBorrow borrowFrame = new BookBorrow(conn, accountId, email);
            borrowFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openBorrowButton);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReturnBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Return a Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton openReturnButton = new JButton("Open Return Form");
        openReturnButton.setFont(new Font("Arial", Font.BOLD, 18));
        openReturnButton.addActionListener(e -> {
            BookReturn returnFrame = new BookReturn(conn, accountId, email);
            returnFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openReturnButton);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }
    private void returnBook() {
        try {
            String bookIdStr = JOptionPane.showInputDialog(this,
                "Enter Book ID to return:",
                "Return Book",
                JOptionPane.QUESTION_MESSAGE);
            
            if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
                return;
            }
            
            int bookId = Integer.parseInt(bookIdStr);
            
            // Check if user has borrowed this book
            String checkSql = "SELECT book_name FROM transactions t " +
                    "WHERE account_id = ? AND book_id = ? AND transaction_type = 'borrow' " +
                    "AND NOT EXISTS ( " +
                    "  SELECT 1 FROM transactions t2 " +
                    "  WHERE t2.account_id = t.account_id " +
                    "  AND t2.book_id = t.book_id " +
                    "  AND t2.transaction_type = 'return' " +
                    "  AND t2.transaction_id > t.transaction_id " +
                    ")";
            PreparedStatement checkPst = conn.prepareStatement(checkSql);
            checkPst.setInt(1, accountId);
            checkPst.setInt(2, bookId);
            ResultSet rs = checkPst.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "You haven't borrowed this book or invalid Book ID!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                rs.close();
                checkPst.close();
                return;
            }
            
            String bookName = rs.getString("book_name");
            rs.close();
            checkPst.close();
            
            // Get contact number
            String contactSql = "SELECT contact_number FROM account WHERE account_id = ?";
            PreparedStatement contactPst = conn.prepareStatement(contactSql);
            contactPst.setInt(1, accountId);
            ResultSet contactRs = contactPst.executeQuery();
            contactRs.next();
            String contactNumber = contactRs.getString("contact_number");
            contactRs.close();
            contactPst.close();
            
            // Insert return transaction
            String sql = "INSERT INTO transactions (transaction_type, book_id, book_name, account_id, contact_number, email) " +
                         "VALUES ('return', ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setString(2, bookName);
            pst.setInt(3, accountId);
            pst.setString(4, contactNumber);
            pst.setString(5, email);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Book returned successfully!\n\nBook: " + bookName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            pst.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid Book ID! Please enter a number.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error returning book: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTransactionHistory());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(topPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        transactionHistoryContentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(transactionHistoryContentPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadTransactionHistory();

        return panel;
    }

    private void loadTransactionHistory() {
        transactionHistoryContentPanel.removeAll();
        try {
            String sql = "SELECT t.transaction_id, t.transaction_type, t.book_id, t.book_name, b.book_author " +
                         "FROM transactions t " +
                         "JOIN books b ON t.book_id = b.book_id " +
                         "WHERE t.account_id = ? " +
                         "ORDER BY t.transaction_id DESC";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, accountId);
            ResultSet rs = pst.executeQuery();
            
            boolean hasTransactions = false;
            while (rs.next()) {
                hasTransactions = true;
                int transactionId = rs.getInt("transaction_id");
                String type = rs.getString("transaction_type");
                int bookId = rs.getInt("book_id");
                String bookName = rs.getString("book_name");
                String author = rs.getString("book_author");
                transactionHistoryContentPanel.add(createTransactionCard(transactionId, type, bookId, bookName, author));
            }
            
            if (!hasTransactions) {
                JLabel noTransactions = new JLabel("No transaction history found.", SwingConstants.CENTER);
                noTransactions.setFont(new Font("Arial", Font.PLAIN, 18));
                transactionHistoryContentPanel.add(noTransactions);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transaction history: " + e.getMessage());
        }
        transactionHistoryContentPanel.revalidate();
        transactionHistoryContentPanel.repaint();
    }

    private JPanel createTransactionCard(int transactionId, String type, int bookId, String bookName, String author) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(new JLabel("Transaction ID: " + transactionId + " | Type: " + type.toUpperCase()));
        detailsPanel.add(new JLabel("Book ID: " + bookId + " | Title: " + bookName));
        detailsPanel.add(new JLabel("Author: " + author));
        card.add(detailsPanel, BorderLayout.CENTER);

        // Color code by transaction type
        if (type.equalsIgnoreCase("borrow")) {
            card.setBackground(new Color(255, 250, 205));
        } else if (type.equalsIgnoreCase("return")) {
            card.setBackground(new Color(240, 255, 240));
        }

        return card;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel profileTitle = new JLabel("My Profile", SwingConstants.CENTER);
        profileTitle.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(profileTitle, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 100, 100));

        try {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, accountId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                addProfileField(detailsPanel, "Account ID:", String.valueOf(rs.getInt("account_id")));
                addProfileField(detailsPanel, "Name:", rs.getString("first_name") + " " + rs.getString("last_name"));
                addProfileField(detailsPanel, "Age:", String.valueOf(rs.getInt("age")));
                addProfileField(detailsPanel, "Sex:", rs.getString("sex"));
                addProfileField(detailsPanel, "Contact:", rs.getString("contact_number"));
                addProfileField(detailsPanel, "Email:", rs.getString("email"));
                addProfileField(detailsPanel, "Address:", rs.getString("address"));
                addProfileField(detailsPanel, "Role:", rs.getString("role"));
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(detailsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addProfileField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 16));
        fieldPanel.add(labelComponent);
        fieldPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        fieldPanel.add(valueComponent);
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
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

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            this.dispose();
            Login loginWindow = new Login();
            loginWindow.setVisible(true);
        }
    }
}