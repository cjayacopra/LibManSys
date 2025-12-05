package libManSys;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JSplitPane;
import javax.swing.JSeparator;

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
		
		try {
		    UIManager.setLookAndFeel( new FlatDarculaLaf() );
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize LaF" );
		}
		setTitle("L O G I N");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 306);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setBounds(329, 94, 96, 14);
		contentPane.add(lblEmail);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setBounds(329, 179, 96, 14);
		contentPane.add(lblPassword);
		
		txtEmail = new JTextField();
		txtEmail.setBounds(278, 120, 200, 25);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setBounds(278, 205, 200, 25);
		contentPane.add(pwdPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(329, 242, 100, 24);
		contentPane.add(btnLogin);
		
		ImageIcon logo = new ImageIcon("assets/LibManSys_Logo.png");
		
		JLabel lblLogo = new JLabel(logo);
		lblLogo.setBounds(0, 0, 260, 306);
		contentPane.add(lblLogo);
		
		JLabel lblLibManSys = new JLabel("<html><center>LIBRARY MANAGEMENT<br>SYSTEM</center></html>");
		lblLibManSys.setHorizontalAlignment(SwingConstants.CENTER);
		lblLibManSys.setFont(new Font("Dialog", Font.BOLD, 15));
		lblLibManSys.setBounds(272, 26, 216, 56);
		contentPane.add(lblLibManSys);
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
				account.setRole(result.getString("role")); // Set the role
				if(password.equals(account.getPassword())) {
					
					if ((account.getRole().equals("librarian"))) {
						// TODO: Replace this with the librarian dashboard
						JOptionPane.showMessageDialog(this, "Login successful! Your role is: librarian","Login successful", JOptionPane.INFORMATION_MESSAGE);
					} else {
						// TODO: Replace this with the reader dashboard
						JOptionPane.showMessageDialog(this, "Login successful! Your role is: reader" ,"Login successful", JOptionPane.INFORMATION_MESSAGE);
					}
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
