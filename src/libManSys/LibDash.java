package libManSys;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.*;

public class LibDash {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private JButton activeMenuButton;
    private Map<String, JButton> menuButtons = new HashMap<>();
    private Map<String, TableRowSorter<DefaultTableModel>> sorters = new HashMap<>();
    private String currentCard;
    private DbConnect dbConnect;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                LibDash window = new LibDash("Allison");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LibDash(String userName) {
        dbConnect = new DbConnect();
        dbConnect.connect();
        initialize(userName);
    }

    private void initialize(String userName) {
        frame = new JFrame("Library Management System");
        frame.setBounds(100, 100, 1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout());

        sidebar = createSidebar();
        frame.getContentPane().add(sidebar, BorderLayout.WEST);

        JPanel contentArea = new JPanel(new BorderLayout());
        frame.getContentPane().add(contentArea, BorderLayout.CENTER);

        JPanel topBar = createTopBar(userName);
        contentArea.add(topBar, BorderLayout.NORTH);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        contentArea.add(mainPanel, BorderLayout.CENTER);

        mainPanel.add(createDashboardScreen(), "Dashboard");
        mainPanel.add(createReadersScreen(), "Readers");
        mainPanel.add(createLibrariansScreen(), "Librarians");
        mainPanel.add(createBooksScreen(), "Books");
        mainPanel.add(createCheckOutBooksScreen(), "Check-out Books");

        
        cardLayout.show(mainPanel, "Dashboard");
        setActiveButton(menuButtons.get("Dashboard"));
        frame.setVisible(true);
    }

    private JPanel createSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(248, 249, 250));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel logoLabel = new JLabel("Library", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        logoLabel.setForeground(new Color(25, 25, 112));
        sidebar.add(logoLabel);
        sidebar.add(new javax.swing.JSeparator());
        sidebar.add(javax.swing.Box.createRigidArea(new Dimension(0, 20)));

        addMenuButton("Dashboard", "Dashboard");
        addMenuButton("Readers", "Readers");
        addMenuButton("Librarians", "Librarians");
        addMenuButton("Books", "Books");
        addMenuButton("Check-out Books", "Check-out Books");

        sidebar.add(javax.swing.Box.createVerticalGlue());

        JButton logoutButton = new JButton("Logout");
        styleMenuButton(logoutButton);
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new Login().setVisible(true);
        });
        sidebar.add(logoutButton);

        return sidebar;
    }

    private void addMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        styleMenuButton(button);
        button.addActionListener(e -> {
            currentCard = cardName;
            cardLayout.show(mainPanel, cardName);
            setActiveButton((JButton) e.getSource());
        });
        sidebar.add(button);
        sidebar.add(javax.swing.Box.createRigidArea(new Dimension(0, 5)));
        menuButtons.put(text, button);
    }
    
    private void setActiveButton(JButton button) {
        if (activeMenuButton != null) {
            styleMenuButton(activeMenuButton);
        }
        activeMenuButton = button;
        activeMenuButton.setBackground(new Color(46, 139, 87));
        activeMenuButton.setForeground(Color.WHITE);
    }

    private void styleMenuButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(248, 249, 250));
        button.setForeground(new Color(50, 50, 50));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
    }

    private JPanel createTopBar(String userName) {
        JPanel topBar = new JPanel(new GridBagLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Constraints for the search field
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(5, 10, 5, 10);
        searchGbc.weightx = 1.0;
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchGbc.gridx = 0;
        JTextField searchField = new JTextField("Ex. Title, Author, Member, etc.");
        searchField.setPreferredSize(new Dimension(300, 30));
        topBar.add(searchField, searchGbc);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCurrentTable(searchField.getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCurrentTable(searchField.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCurrentTable(searchField.getText());
            }
        });

        // Constraints for the user label
        GridBagConstraints userGbc = new GridBagConstraints();
        userGbc.insets = new Insets(5, 10, 5, 10);
        userGbc.gridx = 1;
        JLabel userLabel = new JLabel(userName);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topBar.add(userLabel, userGbc);

        return topBar;
    }

    private void filterCurrentTable(String text) {
        if (currentCard != null) {
            TableRowSorter<DefaultTableModel> sorter = sorters.get(currentCard);
            if (sorter != null) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }

    private JPanel createDashboardScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topCardsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        
        int borrowedBooks = getMetricValue("SELECT COUNT(*) FROM transactions WHERE transaction_type = 'borrow'");
        topCardsPanel.add(createMetricCard("Borrowed Books", String.valueOf(borrowedBooks), "+23%"));

        int returnedBooks = getMetricValue("SELECT COUNT(*) FROM transactions WHERE transaction_type = 'return'");
        topCardsPanel.add(createMetricCard("Returned Books", String.valueOf(returnedBooks), "+10%"));
        
        topCardsPanel.add(createMetricCard("Overdue Books", "12", "-5%"));
        topCardsPanel.add(createMetricCard("Missing Books", "3", "+1%"));
        
        int totalBooks = getMetricValue("SELECT COUNT(*) FROM books");
        topCardsPanel.add(createMetricCard("Total Books", String.valueOf(totalBooks), ""));

        topCardsPanel.add(createMetricCard("Visitors", "2,345", ""));
        
        int totalMembers = getMetricValue("SELECT COUNT(*) FROM account WHERE role = 'reader'");
        topCardsPanel.add(createMetricCard("New Members", String.valueOf(totalMembers), ""));
        
        topCardsPanel.add(createMetricCard("Pending Fees", "$540", ""));
        panel.add(topCardsPanel, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new GridLayout(2,1,10,10));
        centerContent.setOpaque(false);
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Check-out Statistics"));
        chartPanel.add(new JLabel("Chart will be here (requires a library like JFreeChart).", SwingConstants.CENTER));
        centerContent.add(chartPanel);
        
        String[] overdueColumns = {"Member ID", "Title", "ISBN", "Due Date", "Fine"};
        JTable overdueTable = new JTable(new DefaultTableModel(overdueColumns, 0));
        overdueTable.setFillsViewportHeight(true);
        JScrollPane overdueScrollPane = new JScrollPane(overdueTable);
        overdueScrollPane.setBorder(BorderFactory.createTitledBorder("Overdue Books History"));
        centerContent.add(overdueScrollPane);

        JPanel topBooksPanel = new JPanel(new BorderLayout());
        topBooksPanel.setBorder(BorderFactory.createTitledBorder("Top Books"));
        
        String[] topBooksColumns = {"Title", "Author", "Times Borrowed"};
        DefaultTableModel topBooksModel = new DefaultTableModel(topBooksColumns, 0);
        topBooksTable = new JTable(topBooksModel);
        topBooksTable.setFillsViewportHeight(true);
        topBooksPanel.add(new JScrollPane(topBooksTable), BorderLayout.CENTER);
        loadTopBooks();
        topBooksPanel.setPreferredSize(new Dimension(250, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerContent, topBooksPanel);
        splitPane.setResizeWeight(0.8);
        panel.add(splitPane, BorderLayout.CENTER);

        String[] recentColumns = {"ID", "ISBN", "Title", "Author", "Member", "Issued Date", "Return Date", "Status"};
        DefaultTableModel recentModel = new DefaultTableModel(recentColumns, 0);
        recentCheckOutsTable = new JTable(recentModel);
        recentCheckOutsTable.setFillsViewportHeight(true);
        JScrollPane recentScrollPane = new JScrollPane(recentCheckOutsTable);
        recentScrollPane.setBorder(BorderFactory.createTitledBorder("Recent Check-out's"));
        recentScrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(recentScrollPane, BorderLayout.SOUTH);
        loadRecentCheckOuts();

        return panel;
    }

    private int getMetricValue(String query) {
        int count = 0;
        try {
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            if (result.next()) {
                count = result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private void loadTopBooks() {
        DefaultTableModel model = (DefaultTableModel) topBooksTable.getModel();
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT b.book_name, b.book_author, COUNT(t.book_id) AS times_borrowed FROM transactions t JOIN books b ON t.book_id = b.book_id GROUP BY b.book_name, b.book_author ORDER BY times_borrowed DESC LIMIT 5";
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                String title = result.getString("book_name");
                String author = result.getString("book_author");
                int timesBorrowed = result.getInt("times_borrowed");
                model.addRow(new Object[]{title, author, timesBorrowed});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecentCheckOuts() {
        DefaultTableModel model = (DefaultTableModel) recentCheckOutsTable.getModel();
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT t.transaction_id, t.book_name, b.book_author, a.first_name, a.last_name, t.transaction_type FROM transactions t JOIN books b ON t.book_id = b.book_id JOIN account a ON t.account_id = a.account_id ORDER BY t.transaction_id DESC LIMIT 10";
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                String id = "T" + String.format("%03d", result.getInt("transaction_id"));
                String title = result.getString("book_name");
                String author = result.getString("book_author");
                String member = result.getString("first_name") + " " + result.getString("last_name");
                String status = result.getString("transaction_type");
                
                model.addRow(new Object[]{id, "", title, author, member, "", "", status}); // ISBN, Issued Date, Return Date are blank as per schema limitations
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JTable readersTable;
    private JTable topBooksTable;
    private JTable recentCheckOutsTable;
    private JTable librariansTable;
    private JTable booksTable;

    private JPanel createMetricCard(String title, String value, String change) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        if (change.startsWith("+")) {
            changeLabel.setForeground(new Color(0, 128, 0));
        } else if (change.startsWith("-")) {
            changeLabel.setForeground(Color.RED);
        }
        card.add(changeLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createReadersScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Readers");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel subtitle = new JLabel("To create a reader and view the reader report.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        titlePanel.add(title);
        titlePanel.add(subtitle);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField("Reader Search", 20);
        buttonsPanel.add(searchField);
        JButton addButton = new JButton("Add Readers");
        addButton.setBackground(new Color(46, 139, 87));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            AddUserDialog dialog = new AddUserDialog(frame, "Add Reader", true, true);
            dialog.setVisible(true);
        });
        buttonsPanel.add(addButton);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Reader ID", "Reader", "Email ID", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        readersTable = new JTable(model);
        readersTable.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        readersTable.setRowSorter(sorter);
        sorters.put("Readers", sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
            private void filterTable() {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        readersTable.getColumnModel().getColumn(3).setCellRenderer(new ActionPanel());
        readersTable.getColumnModel().getColumn(3).setCellEditor(new ActionCellEditor(readersTable));
        
        panel.add(new JScrollPane(readersTable), BorderLayout.CENTER);
        
        loadReaders();

        return panel;
    }

    private void loadReaders() {
        DefaultTableModel model = (DefaultTableModel) readersTable.getModel();
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT account_id, first_name, last_name, email FROM account WHERE role = 'reader'";
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                String memberId = "M" + String.format("%03d", result.getInt("account_id"));
                String name = result.getString("first_name") + " " + result.getString("last_name");
                String email = result.getString("email");
                model.addRow(new Object[]{memberId, name, email, "Edit / Cancel"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createLibrariansScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Librarians");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel subtitle = new JLabel("To create a librarian and view the librarian report.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        titlePanel.add(title);
        titlePanel.add(subtitle);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField("Librarian Search", 20);
        buttonsPanel.add(searchField);
        JButton addButton = new JButton("Add Librarians");
        addButton.setBackground(new Color(46, 139, 87));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            AddUserDialog dialog = new AddUserDialog(frame, "Add Librarian", true, false);
            dialog.setVisible(true);
        });
        buttonsPanel.add(addButton);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Librarian ID", "Librarian", "Email ID", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        librariansTable = new JTable(model);
        librariansTable.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        librariansTable.setRowSorter(sorter);
        sorters.put("Librarians", sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
            private void filterTable() {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        librariansTable.getColumnModel().getColumn(3).setCellRenderer(new ActionPanel());
        librariansTable.getColumnModel().getColumn(3).setCellEditor(new ActionCellEditor(librariansTable));
        
        panel.add(new JScrollPane(librariansTable), BorderLayout.CENTER);
        
        loadLibrarians();

        return panel;
    }

    private void loadLibrarians() {
        DefaultTableModel model = (DefaultTableModel) librariansTable.getModel();
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT account_id, first_name, last_name, email FROM account WHERE role = 'librarian'";
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                String memberId = "L" + String.format("%03d", result.getInt("account_id"));
                String name = result.getString("first_name") + " " + result.getString("last_name");
                String email = result.getString("email");
                model.addRow(new Object[]{memberId, name, email, "Edit / Cancel"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JTable checkOutTable;

    private JPanel createBooksScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Books");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel subtitle = new JLabel("To add, edit, delete, and view book reports.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        titlePanel.add(title);
        titlePanel.add(subtitle);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField("Book Search", 20);
        buttonsPanel.add(searchField);
        JButton addButton = new JButton("Add Book");
        addButton.setBackground(new Color(46, 139, 87));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            AddBookDialog dialog = new AddBookDialog(frame, "Add Book", true);
            dialog.setVisible(true);
        });
        buttonsPanel.add(addButton);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Book ID", "Name", "Author", "Category", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        booksTable = new JTable(model);
        booksTable.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        booksTable.setRowSorter(sorter);
        sorters.put("Books", sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
            private void filterTable() {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        booksTable.getColumnModel().getColumn(4).setCellRenderer(new ActionPanel());
        booksTable.getColumnModel().getColumn(4).setCellEditor(new ActionCellEditor(booksTable));
        
        panel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        
        loadBooks();

        return panel;
    }

    private void loadBooks() {
        DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT * FROM books";
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                int bookId = result.getInt("book_id");
                String name = result.getString("book_name");
                String author = result.getString("book_author");
                String category = result.getString("book_category");
                model.addRow(new Object[]{bookId, name, author, category, "Edit / Delete"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createCheckOutBooksScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField("Search...", 30);
        String[] filters = {"ISBN", "Title", "Author", "Member"};
        JComboBox<String> filterComboBox = new JComboBox<>(filters);
        headerPanel.add(searchField);
        headerPanel.add(filterComboBox);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Member ID", "ISBN", "Title", "Author", "Borrowed Date", "Returned Date", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        checkOutTable = new JTable(model);
        checkOutTable.setFillsViewportHeight(true);
        
        panel.add(new JScrollPane(checkOutTable), BorderLayout.CENTER);
        
        loadCheckOuts();
        
        return panel;
    }

    private void loadCheckOuts() {
        DefaultTableModel model = (DefaultTableModel) checkOutTable.getModel();
        model.setRowCount(0);
        try {
            String query = "SELECT t.account_id, t.book_name, b.book_author, t.transaction_type FROM transactions t JOIN books b ON t.book_id = b.book_id";
            PreparedStatement prep = dbConnect.con.prepareStatement(query);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                String memberId = "M" + String.format("%03d", result.getInt("account_id"));
                String title = result.getString("book_name");
                String author = result.getString("book_author");
                String status = result.getString("transaction_type");
                
                model.addRow(new Object[]{memberId, "", title, author, "", "", status, "Return / Renew"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inner class for the action panel
    @SuppressWarnings("serial")
	class ActionPanel extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");

            editButton.setOpaque(true);
            deleteButton.setOpaque(true);

            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            return this;
        }
    }

    // Inner class for the cell editor
    @SuppressWarnings("serial")
	class ActionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private JTable table;
        private int row;

        public ActionCellEditor(JTable table) {
            this.table = table;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");

            editButton.setOpaque(true);
            deleteButton.setOpaque(true);

            editButton.setActionCommand("edit");
            deleteButton.setActionCommand("delete");

            editButton.addActionListener(this);
            deleteButton.addActionListener(this);

            panel.add(editButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();

            if (table == readersTable || table == librariansTable) {
                String idStr = (String) table.getModel().getValueAt(row, 0);
                int id = Integer.parseInt(idStr.substring(1)); // Remove the "M" or "L" prefix

                if ("delete".equals(e.getActionCommand())) {
                    int response = JOptionPane.showConfirmDialog(
                        table,
                        "Are you sure you want to delete this user?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (response == JOptionPane.YES_OPTION) {
                        try {
                            String query = "DELETE FROM account WHERE account_id = ?";
                            PreparedStatement prep = dbConnect.con.prepareStatement(query);
                            prep.setInt(1, id);
                            prep.executeUpdate();

                            // Refresh the table
                            if (table == readersTable) {
                                loadReaders();
                            } else if (table == librariansTable) {
                                loadLibrarians();
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(table, "Error deleting user.", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else { // Edit button
                    String name = (String) table.getModel().getValueAt(row, 1);
                    String email = (String) table.getModel().getValueAt(row, 2);
                    String[] nameParts = name.split(" ");
                    String firstName = nameParts[0];
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";

                    EditUserDialog dialog = new EditUserDialog(frame, "Edit User", true, id, firstName, lastName, email, table == readersTable);
                    dialog.setVisible(true);
                }
            } else if (table == booksTable) {
                int bookId = (int) table.getModel().getValueAt(row, 0);

                if ("delete".equals(e.getActionCommand())) {
                    int response = JOptionPane.showConfirmDialog(
                        table,
                        "Are you sure you want to delete this book?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (response == JOptionPane.YES_OPTION) {
                        try {
                            String query = "DELETE FROM books WHERE book_id = ?";
                            PreparedStatement prep = dbConnect.con.prepareStatement(query);
                            prep.setInt(1, bookId);
                            prep.executeUpdate();
                            loadBooks();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(table, "Error deleting book.", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else { // Edit button
                    String name = (String) table.getModel().getValueAt(row, 1);
                    String author = (String) table.getModel().getValueAt(row, 2);
                    String category = (String) table.getModel().getValueAt(row, 3);
                    EditBookDialog dialog = new EditBookDialog(frame, "Edit Book", true, bookId, name, author, category);
                    dialog.setVisible(true);
                }
            }
        }
    }

    // Inner class for the edit user dialog
    @SuppressWarnings("serial")
	class EditUserDialog extends JDialog {
        private JTextField firstNameField;
        private JTextField lastNameField;
        private JTextField emailField;
        private JButton saveButton;
        private JButton cancelButton;
        private int userId;
        private boolean isReader;

        public EditUserDialog(JFrame parent, String title, boolean modal, int userId, String firstName, String lastName, String email, boolean isReader) {
            super(parent, title, modal);
            this.userId = userId;
            this.isReader = isReader;
            
            firstNameField = new JTextField(firstName, 20);
            lastNameField = new JTextField(lastName, 20);
            emailField = new JTextField(email, 20);
            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");

            setLayout(new GridLayout(4, 2, 10, 10));
            add(new JLabel("First Name:"));
            add(firstNameField);
            add(new JLabel("Last Name:"));
            add(lastNameField);
            add(new JLabel("Email:"));
            add(emailField);
            add(saveButton);
            add(cancelButton);

            saveButton.addActionListener(e -> onSave());
            cancelButton.addActionListener(e -> dispose());

            pack();
            setLocationRelativeTo(parent);
        }

        private void onSave() {
            try {
                String query = "UPDATE account SET first_name = ?, last_name = ?, email = ? WHERE account_id = ?";
                PreparedStatement prep = dbConnect.con.prepareStatement(query);
                prep.setString(1, firstNameField.getText());
                prep.setString(2, lastNameField.getText());
                prep.setString(3, emailField.getText());
                prep.setInt(4, userId);
                prep.executeUpdate();

                if (isReader) {
                    loadReaders();
                } else {
                    loadLibrarians();
                }
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating user.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for the add user dialog
    class AddUserDialog extends JDialog {
        private JTextField firstNameField, lastNameField, ageField, contactField, emailField, addressField;
        private JPasswordField passwordField;
        private JComboBox<String> sexComboBox;
        private JButton addButton, cancelButton;
        private boolean isReader;

        public AddUserDialog(JFrame parent, String title, boolean modal, boolean isReader) {
            super(parent, title, modal);
            this.isReader = isReader;

            firstNameField = new JTextField(20);
            lastNameField = new JTextField(20);
            ageField = new JTextField(5);
            contactField = new JTextField(15);
            emailField = new JTextField(20);
            addressField = new JTextField(20);
            passwordField = new JPasswordField(20);
            sexComboBox = new JComboBox<>(new String[]{"MALE", "FEMALE"});
            addButton = new JButton("Add");
            cancelButton = new JButton("Cancel");

            setLayout(new GridLayout(9, 2, 10, 10));
            add(new JLabel("First Name:"));
            add(firstNameField);
            add(new JLabel("Last Name:"));
            add(lastNameField);
            add(new JLabel("Age:"));
            add(ageField);
            add(new JLabel("Sex:"));
            add(sexComboBox);
            add(new JLabel("Contact Number:"));
            add(contactField);
            add(new JLabel("Email:"));
            add(emailField);
            add(new JLabel("Address:"));
            add(addressField);
            add(new JLabel("Password:"));
            add(passwordField);
            add(addButton);
            add(cancelButton);

            addButton.addActionListener(e -> onAdd());
            cancelButton.addActionListener(e -> dispose());

            pack();
            setLocationRelativeTo(parent);
        }

        private void onAdd() {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String ageStr = ageField.getText();
            String contact = contactField.getText();
            String email = emailField.getText();
            String address = addressField.getText();
            String password = new String(passwordField.getPassword());
            String sex = (String) sexComboBox.getSelectedItem();
            String role = isReader ? "reader" : "librarian";

            if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int age = Integer.parseInt(ageStr);
                String query = "INSERT INTO account (first_name, last_name, age, sex, contact_number, email, address, role, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement prep = dbConnect.con.prepareStatement(query);
                prep.setString(1, firstName);
                prep.setString(2, lastName);
                prep.setInt(3, age);
                prep.setString(4, sex);
                prep.setString(5, contact);
                prep.setString(6, email);
                prep.setString(7, address);
                prep.setString(8, role);
                prep.setString(9, password);
                prep.executeUpdate();

                if (isReader) {
                    loadReaders();
                } else {
                    loadLibrarians();
                }
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding user.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for the add book dialog
    @SuppressWarnings("serial")
	class AddBookDialog extends JDialog {
        private JTextField nameField;
        private JTextField authorField;
        private JTextField categoryField;
        private JButton addButton;
        private JButton cancelButton;

        public AddBookDialog(JFrame parent, String title, boolean modal) {
            super(parent, title, modal);

            nameField = new JTextField(30);
            authorField = new JTextField(30);
            categoryField = new JTextField(30);
            addButton = new JButton("Add");
            cancelButton = new JButton("Cancel");

            setLayout(new GridLayout(4, 2, 10, 10));
            add(new JLabel("Name:"));
            add(nameField);
            add(new JLabel("Author:"));
            add(authorField);
            add(new JLabel("Category:"));
            add(categoryField);
            add(addButton);
            add(cancelButton);

            addButton.addActionListener(e -> onAdd());
            cancelButton.addActionListener(e -> dispose());

            pack();
            setLocationRelativeTo(parent);
        }

        private void onAdd() {
            String bookName = nameField.getText();
            String bookAuthor = authorField.getText();
            String bookCategory = categoryField.getText();

            if (bookName.isEmpty() || bookAuthor.isEmpty() || bookCategory.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "INSERT INTO books (book_name, book_author, issue_date, book_category) VALUES (?, ?, ?, ?)";
                PreparedStatement prep = dbConnect.con.prepareStatement(query);
                prep.setString(1, bookName);
                prep.setString(2, bookAuthor);
                prep.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Use current date for issue_date
                prep.setString(4, bookCategory);
                prep.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
                dispose();
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding book.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for the edit book dialog
    class EditBookDialog extends JDialog {
        private JTextField nameField;
        private JTextField authorField;
        private JTextField categoryField;
        private JButton saveButton;
        private JButton cancelButton;
        private int bookId;

        public EditBookDialog(JFrame parent, String title, boolean modal, int bookId, String name, String author, String category) {
            super(parent, title, modal);
            this.bookId = bookId;

            nameField = new JTextField(name, 30);
            authorField = new JTextField(author, 30);
            categoryField = new JTextField(category, 30);
            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");

            setLayout(new GridLayout(4, 2, 10, 10));
            add(new JLabel("Name:"));
            add(nameField);
            add(new JLabel("Author:"));
            add(authorField);
            add(new JLabel("Category:"));
            add(categoryField);
            add(saveButton);
            add(cancelButton);

            saveButton.addActionListener(e -> onSave());
            cancelButton.addActionListener(e -> dispose());

            pack();
            setLocationRelativeTo(parent);
        }

        private void onSave() {
            try {
                String query = "UPDATE books SET book_name = ?, book_author = ?, book_category = ? WHERE book_id = ?";
                PreparedStatement prep = dbConnect.con.prepareStatement(query);
                prep.setString(1, nameField.getText());
                prep.setString(2, authorField.getText());
                prep.setString(3, categoryField.getText());
                prep.setInt(4, bookId);
                prep.executeUpdate();

                loadBooks();
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating book.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}