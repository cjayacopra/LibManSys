package libManSys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

public class BookReturn extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private Connection conn;
    private int accountId;
    private String email;
    private String readerName;
    
    private JPanel booksPanel;
    private JScrollPane scrollPane;
    private boolean isDarkMode = false;
    
    public BookReturn(Connection conn, int accountId, String email) {
        this.conn = conn;
        this.accountId = accountId;
        this.email = email;
        
        // Fetch reader name
        fetchReaderName();
        
        // Initialize UI
        initialize();
        
        // Load unreturned books
        loadUnreturnedBooks();
    }
    
    private void fetchReaderName() {
        try {
            String sql = "SELECT first_name, last_name FROM account WHERE account_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, accountId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                this.readerName = firstName + " " + lastName;
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.readerName = "Reader";
        }
    }
    
    private void initialize() {
        setTitle("Return a Book - LibManSys");
        setBounds(100, 100, 1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            Image icon = new ImageIcon(getClass().getResource("/assets/LibManSys_Icon.png")).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        
        getContentPane().setLayout(new BorderLayout(10, 10));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        
        // Books Panel (main content)
        booksPanel = new JPanel();
        booksPanel.setLayout(new BoxLayout(booksPanel, BoxLayout.Y_AXIS));
        booksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(booksPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        
        updateTheme();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Return a Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Right side with refresh and theme toggle
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadUnreturnedBooks());
        rightPanel.add(refreshButton);
        
        JButton themeToggleButton = new JButton("Toggle Theme");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(e -> toggleTheme());
        rightPanel.add(themeToggleButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Subtitle
        JPanel subtitlePanel = new JPanel(new BorderLayout());
        JLabel subtitleLabel = new JLabel("Select a book to return from your borrowed books");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        subtitlePanel.add(subtitleLabel, BorderLayout.WEST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(subtitlePanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        
        return bottomPanel;
    }
    
    private void loadUnreturnedBooks() {
        booksPanel.removeAll();
        
        try {
            String sql = "SELECT t.book_id, t.book_name, b.book_author, b.book_category, b.issue_date " +
                        "FROM transactions t " +
                        "JOIN books b ON t.book_id = b.book_id " +
                        "WHERE t.account_id = ? AND t.transaction_type = 'borrow' " +
                        "AND NOT EXISTS ( " +
                        "    SELECT 1 FROM transactions t2 " +
                        "    WHERE t2.account_id = t.account_id " +
                        "    AND t2.book_id = t.book_id " +
                        "    AND t2.transaction_type = 'return' " +
                        "    AND t2.transaction_id > t.transaction_id" +
                        ") " +
                        "ORDER BY t.transaction_id DESC";
            
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
                String issueDate = rs.getString("issue_date");
                
                booksPanel.add(createBookCard(bookId, bookName, author, category, issueDate));
                booksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            if (!hasBooks) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
                
                JLabel emptyIcon = new JLabel("📚");
                emptyIcon.setFont(new Font("Arial", Font.PLAIN, 72));
                emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyPanel.add(emptyIcon);
                
                emptyPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                JLabel emptyLabel = new JLabel("No borrowed books to return");
                emptyLabel.setFont(new Font("Arial", Font.BOLD, 20));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyPanel.add(emptyLabel);
                
                emptyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                JLabel emptySubLabel = new JLabel("You currently have no unreturned books");
                emptySubLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyPanel.add(emptySubLabel);
                
                booksPanel.add(emptyPanel);
            }
            
            rs.close();
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading books: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        booksPanel.revalidate();
        booksPanel.repaint();
    }
    
    private JPanel createBookCard(int bookId, String bookName, String author, String category, String issueDate) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        
        // Left side - Book details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        // Book Title
        JLabel titleLabel = new JLabel(bookName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(titleLabel);
        
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Book ID
        JLabel idLabel = new JLabel("Book ID: " + bookId);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(idLabel);
        
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Author
        JLabel authorLabel = new JLabel("Author: " + author);
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(authorLabel);
        
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Category
        JLabel categoryLabel = new JLabel("Category: " + category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(categoryLabel);
        
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Issue Date
        JLabel issueDateLabel = new JLabel("Issue Date: " + issueDate);
        issueDateLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        issueDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(issueDateLabel);
        
        card.add(detailsPanel, BorderLayout.CENTER);
        
        // Right side - Return button
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JButton returnButton = new JButton("Return Book");
        returnButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnButton.setPreferredSize(new Dimension(150, 45));
        returnButton.setFocusPainted(false);
        returnButton.setBackground(new Color(46, 125, 50));
        returnButton.setForeground(Color.WHITE);
        returnButton.addActionListener(e -> returnBook(bookId, bookName));
        
        // Center the button vertically
        JPanel buttonWrapper = new JPanel(new GridLayout(1, 1));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(returnButton);
        actionPanel.add(buttonWrapper, BorderLayout.CENTER);
        
        card.add(actionPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void returnBook(int bookId, String bookName) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to return this book?\n\n" +
            "Book: " + bookName + "\n" +
            "Book ID: " + bookId,
            "Confirm Return",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            // Get contact number
            String contactSql = "SELECT contact_number FROM account WHERE account_id = ?";
            PreparedStatement contactPst = conn.prepareStatement(contactSql);
            contactPst.setInt(1, accountId);
            ResultSet contactRs = contactPst.executeQuery();
            
            if (!contactRs.next()) {
                JOptionPane.showMessageDialog(this,
                    "Error: Could not retrieve account information.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                contactRs.close();
                contactPst.close();
                return;
            }
            
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
                    "Book returned successfully!\n\n" +
                    "Book: " + bookName + "\n" +
                    "Book ID: " + bookId + "\n\n" +
                    "Thank you for using LibManSys!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reload the books list
                loadUnreturnedBooks();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to return book. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
            pst.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}