package libManSys;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mysql.cj.result.IntegerValueFactory;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.SwingConstants;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmail;
	private JPasswordField pwdPassword;
	DbConnect dbCon = new DbConnect();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setTitle("L O G I N");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblLibManSys = new JLabel("Library Management System");
		lblLibManSys.setHorizontalAlignment(SwingConstants.CENTER);
		lblLibManSys.setFont(new Font("Dialog", Font.BOLD, 18));
		lblLibManSys.setBounds(12, 26, 276, 38);
		contentPane.add(lblLibManSys);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setBounds(105, 91, 96, 14);
		contentPane.add(lblEmail);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setBounds(105, 167, 96, 14);
		contentPane.add(lblPassword);
		
		txtEmail = new JTextField();
		txtEmail.setBounds(105, 117, 96, 18);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setBounds(105, 193, 96, 18);
		contentPane.add(pwdPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(101, 264, 100, 24);
		contentPane.add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login(new LibraryAccount());
			}
		});
		

	}
	
	public void login(LibraryAccount account) {
		
		String email = String.valueOf(txtEmail.getText());
		String password = String.valueOf(pwdPassword.getPassword());
		
		account.setEmail(email);
		
		try {
			
			dbCon.connect();
			String select = "SELECT * FROM account WHERE email = ?";
			PreparedStatement prep = dbCon.con.prepareStatement(select);
			prep.setString(1, email);
			
			ResultSet result = prep.executeQuery();
			
			if(result.next()){
				account.setEmail(result.getString("email"));
				account.setPassword(result.getString("password"));
				if(password.equals(account.getPassword())) {
					JOptionPane.showMessageDialog(this, "Login succesful!","Login succesful", JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(this, "Login Failed! Incorrect Password","Login Failed. Incorrect Password", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Login Failed! Email not found.","Login Failed. Incorrect Email.", JOptionPane.ERROR_MESSAGE);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
