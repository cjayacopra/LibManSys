package libManSys;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import java.awt.Component;

public class LibDash {

    private JFrame frame;
    private DbConnect db;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JPanel booksContentPanel;
    private JPanel readersContentPanel;
    private JPanel librariansContentPanel;
    private JPanel sidebar;
    private boolean isDarkMode = false;
    private String loggedInUserName; // Added for profile section

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LibDash window = new LibDash("Admin User"); // Pass a dummy username
                window.getFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LibDash(String userName) { // Constructor now accepts username
        this.loggedInUserName = userName;
        db = new DbConnect();
        db.connect();
        initialize();
        loadBooks("");
        loadAccounts("reader", "");
        loadAccounts("librarian", "");
    }

    private void initialize() {
        frame = new JFrame("Librarian Dashboard");
        frame.setBounds(100, 100, 1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout());

        // Sidebar
        sidebar = createSidebar();
        frame.getContentPane().add(sidebar, BorderLayout.WEST);

        // Main Panel
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        JPanel dashboardPanel = createDashboardPanel();
        mainPanel.add(dashboardPanel, "Dashboard");

        JPanel booksPanel = createBooksPanel();
        mainPanel.add(booksPanel, "Books");

        JPanel readersPanel = createAccountsPanel("reader");
        mainPanel.add(readersPanel, "Readers");
        
        JPanel librariansPanel = createAccountsPanel("librarian");
        mainPanel.add(librariansPanel, "Librarians");

        JPanel profilePanel = createProfilePanel(); // New profile panel
        mainPanel.add(profilePanel, "Profile"); // Add it as a card

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        updateTheme(); // Initialize theme
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("LibManSys");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createNavButton("Dashboard", "Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Books", "Books"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Readers", "Readers"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Librarians", "Librarians"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing for new button
        sidebar.add(createNavButton("Profile", "Profile")); // New Profile button
        
        sidebar.add(Box.createVerticalGlue());
        JButton themeToggleButton = new JButton("Toggle Theme");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 16));
        themeToggleButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        themeToggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, themeToggleButton.getPreferredSize().height));
        themeToggleButton.addActionListener(e -> toggleTheme());
        sidebar.add(themeToggleButton);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUserName + "!", SwingConstants.CENTER); // Personalized welcome
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.NORTH); // Changed to NORTH

        JLabel systemLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        systemLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(systemLabel, BorderLayout.CENTER); // Centered below welcome

        return panel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for search and add button
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.addActionListener(e -> loadBooks(searchField.getText()));
        topPanel.add(searchField, BorderLayout.CENTER);
        
        JButton searchButton = new JButton("Search Books");
        searchButton.addActionListener(e -> loadBooks(searchField.getText()));
        topPanel.add(searchButton, BorderLayout.EAST);
        
        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(e -> addBook());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addBookButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Content panel for books
        booksContentPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(booksContentPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createAccountsPanel(String role) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for search and add button
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.addActionListener(e -> loadAccounts(role, searchField.getText()));
        topPanel.add(searchField, BorderLayout.CENTER);
        
        JButton searchButton = new JButton("Search " + capitalize(role) + "s");
        searchButton.addActionListener(e -> loadAccounts(role, searchField.getText()));
        topPanel.add(searchButton, BorderLayout.EAST);

        JButton addAccountButton = new JButton("Add " + capitalize(role));
        addAccountButton.addActionListener(e -> addAccount(role));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addAccountButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Content panel for accounts
        if (role.equals("reader")) {
            readersContentPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            panel.add(new JScrollPane(readersContentPanel), BorderLayout.CENTER);
        } else {
            librariansContentPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            panel.add(new JScrollPane(librariansContentPanel), BorderLayout.CENTER);
        }

        return panel;
    }

    private void loadBooks(String searchTerm) {
        booksContentPanel.removeAll();
        try {
            String query = "SELECT * FROM books WHERE book_name LIKE ? OR book_author LIKE ?";
            PreparedStatement pst = db.con.prepareStatement(query);
            pst.setString(1, "%" + searchTerm + "%");
            pst.setString(2, "%" + searchTerm + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String name = rs.getString("book_name");
                String author = rs.getString("book_author");
                Date issueDate = rs.getDate("issue_date");
                String category = rs.getString("book_category");
                booksContentPanel.add(createBookCard(bookId, name, author, issueDate, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        booksContentPanel.revalidate();
        booksContentPanel.repaint();
    }
    
    private JPanel createBookCard(int bookId, String name, String author, Date issueDate, String category) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(new JLabel("ID: " + bookId));
        detailsPanel.add(new JLabel("Name: " + name));
        detailsPanel.add(new JLabel("Author: " + author));
        detailsPanel.add(new JLabel("Issue Date: " + (issueDate != null ? issueDate.toString() : "N/A")));
        detailsPanel.add(new JLabel("Category: " + category));
        card.add(detailsPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateBook(bookId, name, author, category));
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteBook(bookId));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addBook() {
        JTextField nameField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField categoryField = new JTextField();
        Object[] message = {
                "Name:", nameField,
                "Author:", authorField,
                "Category:", categoryField
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String query = "INSERT INTO books (book_name, book_author, issue_date, book_category) VALUES (?, ?, CURDATE(), ?)";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setString(1, nameField.getText());
                pst.setString(2, authorField.getText());
                pst.setString(3, categoryField.getText());
                pst.executeUpdate();
                loadBooks("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateBook(int bookId, String currentName, String currentAuthor, String currentCategory) {
        JTextField nameField = new JTextField(currentName);
        JTextField authorField = new JTextField(currentAuthor);
        JTextField categoryField = new JTextField(currentCategory);
        Object[] message = {
                "Name:", nameField,
                "Author:", authorField,
                "Category:", categoryField
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Update Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String query = "UPDATE books SET book_name = ?, book_author = ?, book_category = ? WHERE book_id = ?";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setString(1, nameField.getText());
                pst.setString(2, authorField.getText());
                pst.setString(3, categoryField.getText());
                pst.setInt(4, bookId);
                pst.executeUpdate();
                loadBooks("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteBook(int bookId) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?", "Delete Book", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setInt(1, bookId);
                pst.executeUpdate();
                loadBooks("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAccounts(String role, String searchTerm) {
        JPanel contentPanel = role.equals("reader") ? readersContentPanel : librariansContentPanel;
        contentPanel.removeAll();
        try {
            String query = "SELECT account_id, first_name, last_name, email, contact_number FROM account WHERE role = ? AND (first_name LIKE ? OR last_name LIKE ? OR email LIKE ?)";
            PreparedStatement pst = db.con.prepareStatement(query);
            pst.setString(1, role);
            pst.setString(2, "%" + searchTerm + "%");
            pst.setString(3, "%" + searchTerm + "%");
            pst.setString(4, "%" + searchTerm + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("contact_number");
                contentPanel.add(createAccountCard(accountId, firstName, lastName, email, phone, role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createAccountCard(int accountId, String firstName, String lastName, String email, String phone, String role) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(new JLabel("ID: " + accountId));
        detailsPanel.add(new JLabel("Name: " + firstName + " " + lastName));
        detailsPanel.add(new JLabel("Email: " + email));
        detailsPanel.add(new JLabel("Phone: " + phone));
        card.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateAccount(role, accountId, firstName, lastName, email, phone));
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteAccount(role, accountId));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addAccount(String role) {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField passwordField = new JTextField();
        Object[] message = {
                "First Name:", firstNameField,
                "Last Name:", lastNameField,
                "Email:", emailField,
                "Phone:", phoneField,
                "Password:", passwordField
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Add New " + capitalize(role), JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String query = "INSERT INTO account (first_name, last_name, email, contact_number, password, role, age, sex, address) VALUES (?, ?, ?, ?, ?, ?, 0, 'MALE', '')";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setString(1, firstNameField.getText());
                pst.setString(2, lastNameField.getText());
                pst.setString(3, emailField.getText());
                pst.setString(4, phoneField.getText());
                pst.setString(5, passwordField.getText());
                pst.setString(6, role);
                pst.executeUpdate();
                loadAccounts(role, "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createProfilePanel() { // New Profile Panel Method
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel profileTitle = new JLabel("User Profile", SwingConstants.CENTER);
        profileTitle.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(profileTitle, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Logged in as: " + loggedInUserName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        detailsPanel.add(nameLabel);
        
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            frame.dispose();
            Login loginFrame = new Login();
            loginFrame.setVisible(true);
        });
        detailsPanel.add(logoutButton);
        
        detailsPanel.add(Box.createVerticalGlue());

        panel.add(detailsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateAccount(String role, int accountId, String currentFirstName, String currentLastName, String currentEmail, String currentPhone) {
        JTextField firstNameField = new JTextField(currentFirstName);
        JTextField lastNameField = new JTextField(currentLastName);
        JTextField emailField = new JTextField(currentEmail);
        JTextField phoneField = new JTextField(currentPhone);
        Object[] message = {
                "First Name:", firstNameField,
                "Last Name:", lastNameField,
                "Email:", emailField,
                "Phone:", phoneField
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Update " + capitalize(role), JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String query = "UPDATE account SET first_name = ?, last_name = ?, email = ?, contact_number = ? WHERE account_id = ?";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setString(1, firstNameField.getText());
                pst.setString(2, lastNameField.getText());
                pst.setString(3, emailField.getText());
                pst.setString(4, phoneField.getText());
                pst.setInt(5, accountId);
                pst.executeUpdate();
                loadAccounts(role, "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteAccount(String role, int accountId) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Delete Account", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM account WHERE account_id = ?";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setInt(1, accountId);
                pst.executeUpdate();
                loadAccounts(role, "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            SwingUtilities.updateComponentTreeUI(frame);

            // Manually apply/reset custom sidebar colors
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
                // Reset to L&F defaults for light mode
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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}
