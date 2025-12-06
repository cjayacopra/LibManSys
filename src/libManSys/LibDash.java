package libManSys;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
// Removed java.awt.Graphics;
// Removed java.awt.image.BufferedImage;
// Removed java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Removed javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

public class LibDash {

    private JFrame frame;
    private JTable booksTable;
    private JTable readersTable;
    private JTable librariansTable;
    private DefaultTableModel booksModel;
    private DefaultTableModel readersModel;
    private DefaultTableModel librariansModel;
    private DbConnect db;
    // Removed private BufferedImage logoImage;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LibDash window = new LibDash();
                    window.getFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LibDash() {
        db = new DbConnect();
        db.connect();
        initialize();
        loadBooks();
        loadAccounts("reader");
        loadAccounts("librarian");
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Removed logoImage = ImageIO.read(new File("assets/LogoWhite.png"));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF."); // Adjusted error message
        }

        frame = new JFrame("Librarian Dashboard");
        frame.setResizable(false);
        frame.setBounds(100, 100, 1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null); // Using null layout for absolute positioning

        // Side Panel (no logo, just title)
        JPanel sidePanel = new JPanel(null); // Changed to regular JPanel with null layout
        sidePanel.setBounds(0, 0, 250, 661); // Fixed size, left half
        frame.getContentPane().add(sidePanel);

        JLabel lblTitle = new JLabel("LibManSys");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBounds(10, 25, 230, 40); // Centered within the side panel
        sidePanel.add(lblTitle);

        // Main Content Panel
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBounds(250, 0, 934, 661); // Right half
        frame.getContentPane().add(mainContentPanel);
        mainContentPanel.setLayout(new BorderLayout(0, 0)); // Using BorderLayout for content within

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        mainContentPanel.add(tabbedPane, BorderLayout.CENTER);

        // Books Panel
        JPanel booksPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Books", null, booksPanel, null);
        booksModel = new DefaultTableModel(new Object[]{"ID", "Name", "Author", "Issue Date", "Category"}, 0);
        booksTable = new JTable(booksModel);
        booksPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);

        JPanel booksButtonPanel = new JPanel();
        booksPanel.add(booksButtonPanel, BorderLayout.SOUTH);

        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(e -> addBook());
        booksButtonPanel.add(addBookButton);

        JButton updateBookButton = new JButton("Update Book");
        updateBookButton.addActionListener(e -> updateBook());
        booksButtonPanel.add(updateBookButton);

        JButton deleteBookButton = new JButton("Delete Book");
        deleteBookButton.addActionListener(e -> deleteBook());
        booksButtonPanel.add(deleteBookButton);

        // Readers Panel
        JPanel readersPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Readers", null, readersPanel, null);
        readersModel = new DefaultTableModel(new Object[]{"ID", "First Name", "Last Name", "Email", "Phone"}, 0);
        readersTable = new JTable(readersModel);
        readersPanel.add(new JScrollPane(readersTable), BorderLayout.CENTER);

        JPanel readersButtonPanel = new JPanel();
        readersPanel.add(readersButtonPanel, BorderLayout.SOUTH);

        JButton addReaderButton = new JButton("Add Reader");
        addReaderButton.addActionListener(e -> addAccount("reader"));
        readersButtonPanel.add(addReaderButton);

        JButton updateReaderButton = new JButton("Update Reader");
        updateReaderButton.addActionListener(e -> updateAccount("reader"));
        readersButtonPanel.add(updateReaderButton);

        JButton deleteReaderButton = new JButton("Delete Reader");
        deleteReaderButton.addActionListener(e -> deleteAccount("reader"));
        readersButtonPanel.add(deleteReaderButton);

        // Librarians Panel
        JPanel librariansPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Librarians", null, librariansPanel, null);
        librariansModel = new DefaultTableModel(new Object[]{"ID", "First Name", "Last Name", "Email", "Phone"}, 0);
        librariansTable = new JTable(librariansModel);
        librariansPanel.add(new JScrollPane(librariansTable), BorderLayout.CENTER);

        JPanel librariansButtonPanel = new JPanel();
        librariansPanel.add(librariansButtonPanel, BorderLayout.SOUTH);

        JButton addLibrarianButton = new JButton("Add Librarian");
        addLibrarianButton.addActionListener(e -> addAccount("librarian"));
        librariansButtonPanel.add(addLibrarianButton);

        JButton updateLibrarianButton = new JButton("Update Librarian");
        updateLibrarianButton.addActionListener(e -> updateAccount("librarian"));
        librariansButtonPanel.add(updateLibrarianButton);

        JButton deleteLibrarianButton = new JButton("Delete Librarian");
        deleteLibrarianButton.addActionListener(e -> deleteAccount("librarian"));
        librariansButtonPanel.add(deleteLibrarianButton);
    }

    private void loadBooks() {
        try {
            String query = "SELECT * FROM books";
            PreparedStatement pst = db.con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            booksModel.setRowCount(0);
            while (rs.next()) {
                booksModel.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("book_author"),
                        rs.getDate("issue_date"),
                        rs.getString("book_category")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                loadBooks();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) booksModel.getValueAt(selectedRow, 0);
            JTextField nameField = new JTextField((String) booksModel.getValueAt(selectedRow, 1));
            JTextField authorField = new JTextField((String) booksModel.getValueAt(selectedRow, 2));
            JTextField categoryField = new JTextField((String) booksModel.getValueAt(selectedRow, 4));
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
                    loadBooks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to update.");
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) booksModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?", "Delete Book", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM books WHERE book_id = ?";
                    PreparedStatement pst = db.con.prepareStatement(query);
                    pst.setInt(1, bookId);
                    pst.executeUpdate();
                    loadBooks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to delete.");
        }
    }

    private void loadAccounts(String role) {
        DefaultTableModel model = role.equals("reader") ? readersModel : librariansModel;
        try {
            String query = "SELECT account_id, first_name, last_name, email, contact_number FROM account WHERE role = ?";
            PreparedStatement pst = db.con.prepareStatement(query);
            pst.setString(1, role);
            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("account_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("contact_number")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        int option = JOptionPane.showConfirmDialog(null, message, "Add New " + role, JOptionPane.OK_CANCEL_OPTION);
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
                loadAccounts(role);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAccount(String role) {
        JTable table = role.equals("reader") ? readersTable : librariansTable;
        DefaultTableModel model = role.equals("reader") ? readersModel : librariansModel;
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int accountId = (int) model.getValueAt(selectedRow, 0);
            JTextField firstNameField = new JTextField((String) model.getValueAt(selectedRow, 1));
            JTextField lastNameField = new JTextField((String) model.getValueAt(selectedRow, 2));
            JTextField emailField = new JTextField((String) model.getValueAt(selectedRow, 3));
            JTextField phoneField = new JTextField((String) model.getValueAt(selectedRow, 4));
            Object[] message = {
                    "First Name:", firstNameField,
                    "Last Name:", lastNameField,
                    "Email:", emailField,
                    "Phone:", phoneField
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Update " + role, JOptionPane.OK_CANCEL_OPTION);
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
                    loadAccounts(role);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an account to update.");
        }
    }

    private void deleteAccount(String role) {
        JTable table = role.equals("reader") ? readersTable : librariansTable;
        DefaultTableModel model = role.equals("reader") ? readersModel : librariansModel;
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int accountId = (int) model.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Delete Account", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM account WHERE account_id = ?";
                    PreparedStatement pst = db.con.prepareStatement(query);
                    pst.setInt(1, accountId);
                    pst.executeUpdate();
                    loadAccounts(role);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an account to delete.");
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}