package libManSys;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Reader_dashboard extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int accountId;
	private String readerName;
	private String email;
	private DbConnect dbConnect;
	private Connection conn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// For testing purposes - pass email only
					Reader_dashboard frame = new Reader_dashboard("otelo.nobleza@example.com");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame with reader information from database.
	 * Only requires email - fetches all other data from database.
	 */
	public Reader_dashboard(String email) {
		this.email = email;
		
		// Initialize database connection using DbConnect class
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
		
		// Fetch user data from database based on email
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
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Header Panel
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(70, 130, 180));
		headerPanel.setBounds(0, 0, 884, 80);
		contentPane.add(headerPanel);
		headerPanel.setLayout(null);
		
		// Title Label
		JLabel lblTitle = new JLabel("READER DASHBOARD");
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 28));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 864, 35);
		headerPanel.add(lblTitle);
		
		// Welcome Label
		JLabel lblWelcome = new JLabel("Welcome, " + email);
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
		
		// Search Books Button
		JButton btnSearchBooks = new JButton("Search Books");
		btnSearchBooks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchBooks();
			}
		});
		btnSearchBooks.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnSearchBooks.setBackground(new Color(100, 149, 237));
		btnSearchBooks.setForeground(Color.WHITE);
		btnSearchBooks.setBounds(50, 30, 350, 80);
		mainPanel.add(btnSearchBooks);
		
		// View Borrowed Books Button
		JButton btnBorrowedBooks = new JButton("My Borrowed Books");
		btnBorrowedBooks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewBorrowedBooks();
			}
		});
		btnBorrowedBooks.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnBorrowedBooks.setBackground(new Color(60, 179, 113));
		btnBorrowedBooks.setForeground(Color.WHITE);
		btnBorrowedBooks.setBounds(464, 30, 350, 80);
		mainPanel.add(btnBorrowedBooks);
		
		// Borrow Book Button
		JButton btnBorrowBook = new JButton("Borrow a Book");
		btnBorrowBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				borrowBook();
			}
		});
		btnBorrowBook.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnBorrowBook.setBackground(new Color(255, 165, 0));
		btnBorrowBook.setForeground(Color.WHITE);
		btnBorrowBook.setBounds(50, 140, 350, 80);
		mainPanel.add(btnBorrowBook);
		
		// Return Book Button
		JButton btnReturnBook = new JButton("Return a Book");
		btnReturnBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returnBook();
			}
		});
		btnReturnBook.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnReturnBook.setBackground(new Color(219, 112, 147));
		btnReturnBook.setForeground(Color.WHITE);
		btnReturnBook.setBounds(464, 140, 350, 80);
		mainPanel.add(btnReturnBook);
		
		// Transaction History Button
		JButton btnTransactionHistory = new JButton("Transaction History");
		btnTransactionHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewTransactionHistory();
			}
		});
		btnTransactionHistory.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnTransactionHistory.setBackground(new Color(147, 112, 219));
		btnTransactionHistory.setForeground(Color.WHITE);
		btnTransactionHistory.setBounds(50, 250, 350, 80);
		mainPanel.add(btnTransactionHistory);
		
		// View Profile Button
		JButton btnProfile = new JButton("My Profile");
		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewProfile();
			}
		});
		btnProfile.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnProfile.setBackground(new Color(72, 209, 204));
		btnProfile.setForeground(Color.WHITE);
		btnProfile.setBounds(464, 250, 350, 80);
		mainPanel.add(btnProfile);
		
		// Logout Button
		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		btnLogout.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnLogout.setBackground(new Color(220, 20, 60));
		btnLogout.setForeground(Color.WHITE);
		btnLogout.setBounds(350, 370, 164, 50);
		mainPanel.add(btnLogout);
		
		// Center the frame on screen
		setLocationRelativeTo(null);
	}
	
	// Method to search for books
	private void searchBooks() {
		try {
			String sql = "SELECT * FROM books";
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			StringBuilder bookList = new StringBuilder("Available Books:\n\n");
			int count = 0;
			
			while (rs.next()) {
				count++;
				bookList.append("ID: ").append(rs.getInt("book_id"))
					.append(" | ").append(rs.getString("book_name"))
					.append("\nAuthor: ").append(rs.getString("book_author"))
					.append(" | Category: ").append(rs.getString("book_category"))
					.append("\n\n");
			}
			
			if (count == 0) {
				JOptionPane.showMessageDialog(this,
					"No books available in the library.",
					"Search Books",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, bookList.toString(),
					"Search Books", JOptionPane.INFORMATION_MESSAGE);
			}
			
			rs.close();
			pst.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"Error searching books: " + e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	// Method to view borrowed books
	private void viewBorrowedBooks() {
		try {
			// Get all borrow transactions that haven't been returned yet
			String sql = "SELECT t.book_id, t.book_name, b.book_author " +
						 "FROM transactions t " +
						 "JOIN books b ON t.book_id = b.book_id " +
						 "WHERE t.account_id = ? AND t.transaction_type = 'borrow' " +
						 "AND t.book_id NOT IN (" +
						 "  SELECT book_id FROM transactions " +
						 "  WHERE account_id = ? AND transaction_type = 'return'" +
						 ")";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, accountId);
			pst.setInt(2, accountId);
			ResultSet rs = pst.executeQuery();
			
			StringBuilder borrowedList = new StringBuilder("Your Borrowed Books:\n\n");
			int count = 0;
			
			while (rs.next()) {
				count++;
				borrowedList.append("Book ID: ").append(rs.getInt("book_id"))
					.append(" | ").append(rs.getString("book_name"))
					.append("\nAuthor: ").append(rs.getString("book_author"))
					.append("\n\n");
			}
			
			if (count == 0) {
				JOptionPane.showMessageDialog(this,
					"You have no borrowed books.",
					"Borrowed Books",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, borrowedList.toString(),
					"Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
			}
			
			rs.close();
			pst.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"Error retrieving borrowed books: " + e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	// Method to borrow a book
	private void borrowBook() {
		try {
			String bookIdStr = JOptionPane.showInputDialog(this,
				"Enter Book ID to borrow:",
				"Borrow Book",
				JOptionPane.QUESTION_MESSAGE);
			
			if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
				return;
			}
			
			int bookId = Integer.parseInt(bookIdStr);
			
			// Check if book exists
			String checkSql = "SELECT book_name FROM books WHERE book_id = ?";
			PreparedStatement checkPst = conn.prepareStatement(checkSql);
			checkPst.setInt(1, bookId);
			ResultSet rs = checkPst.executeQuery();
			
			if (!rs.next()) {
				JOptionPane.showMessageDialog(this,
					"Book ID not found!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
				rs.close();
				checkPst.close();
				return;
			}
			
			String bookName = rs.getString("book_name");
			rs.close();
			checkPst.close();
			
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
			
			// Get contact number from account
			String contactSql = "SELECT contact_number FROM account WHERE account_id = ?";
			PreparedStatement contactPst = conn.prepareStatement(contactSql);
			contactPst.setInt(1, accountId);
			ResultSet contactRs = contactPst.executeQuery();
			contactRs.next();
			String contactNumber = contactRs.getString("contact_number");
			contactRs.close();
			contactPst.close();
			
			// Insert borrow transaction
			String sql = "INSERT INTO transactions (transaction_type, book_id, book_name, account_id, contact_number, email) " +
						 "VALUES ('borrow', ?, ?, ?, ?, ?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, bookId);
			pst.setString(2, bookName);
			pst.setInt(3, accountId);
			pst.setString(4, contactNumber);
			pst.setString(5, email);
			
			int result = pst.executeUpdate();
			
			if (result > 0) {
				JOptionPane.showMessageDialog(this,
					"Book borrowed successfully!\n\nBook: " + bookName,
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
				"Error borrowing book: " + e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	// Method to return a book
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
			
			// Check if user has borrowed this book and hasn't returned it yet
			String checkSql = "SELECT book_name FROM transactions " +
							  "WHERE account_id = ? AND book_id = ? AND transaction_type = 'borrow' " +
							  "AND book_id NOT IN (" +
							  "  SELECT book_id FROM transactions " +
							  "  WHERE account_id = ? AND transaction_type = 'return'" +
							  ")";
			PreparedStatement checkPst = conn.prepareStatement(checkSql);
			checkPst.setInt(1, accountId);
			checkPst.setInt(2, bookId);
			checkPst.setInt(3, accountId);
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
	
	// Method to view transaction history
	private void viewTransactionHistory() {
		try {
			String sql = "SELECT t.transaction_id, t.transaction_type, t.book_id, t.book_name, b.book_author " +
						 "FROM transactions t " +
						 "JOIN books b ON t.book_id = b.book_id " +
						 "WHERE t.account_id = ? " +
						 "ORDER BY t.transaction_id DESC";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, accountId);
			ResultSet rs = pst.executeQuery();
			
			StringBuilder history = new StringBuilder("Your Transaction History:\n\n");
			int count = 0;
			
			while (rs.next()) {
				count++;
				history.append("Transaction ID: ").append(rs.getInt("transaction_id"))
					.append(" | Type: ").append(rs.getString("transaction_type").toUpperCase())
					.append("\nBook: ").append(rs.getString("book_name"))
					.append(" (ID: ").append(rs.getInt("book_id")).append(")")
					.append("\nAuthor: ").append(rs.getString("book_author"))
					.append("\n\n");
			}
			
			if (count == 0) {
				JOptionPane.showMessageDialog(this,
					"No transaction history found.",
					"Transaction History",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, history.toString(),
					"Transaction History", JOptionPane.INFORMATION_MESSAGE);
			}
			
			rs.close();
			pst.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"Error retrieving transaction history: " + e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	// Method to view profile
	private void viewProfile() {
		try {
			String sql = "SELECT * FROM account WHERE account_id = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, accountId);
			ResultSet rs = pst.executeQuery();
			
			if (rs.next()) {
				String profile = "PROFILE INFORMATION\n\n" +
					"Account ID: " + rs.getInt("account_id") + "\n" +
					"Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + "\n" +
					"Age: " + rs.getInt("age") + "\n" +
					"Sex: " + rs.getString("sex") + "\n" +
					"Contact: " + rs.getString("contact_number") + "\n" +
					"Email: " + rs.getString("email") + "\n" +
					"Address: " + rs.getString("address") + "\n" +
					"Role: " + rs.getString("role");
				
				JOptionPane.showMessageDialog(this, profile,
					"My Profile", JOptionPane.INFORMATION_MESSAGE);
			}
			
			rs.close();
			pst.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"Error retrieving profile: " + e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	// Method to logout
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
			
			// Close current window
			this.dispose();
			
			// Open login window
			Login loginWindow = new Login();
			loginWindow.setVisible(true);
		}
	}
}