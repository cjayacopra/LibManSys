package libManSys;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSeparator;
import java.awt.Color;


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
		setBounds(100, 100, 500, 300);
		contentPane = new JPanel();
		setContentPane(contentPane);
		
		ImageIcon logo = new ImageIcon("assets/LibManSys_Logo.png");
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{488, 0};
		gbl_contentPane.rowHeights = new int[]{288, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{248, 234, 0};
		gbl_panel.rowHeights = new int[]{80, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{216, 0};
		gbl_panel_1.rowHeights = new int[]{300, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		JLabel lblLogo = new JLabel(logo);
		GridBagConstraints gbc_lblLogo = new GridBagConstraints();
		gbc_lblLogo.fill = GridBagConstraints.BOTH;
		gbc_lblLogo.gridx = 0;
		gbc_lblLogo.gridy = 0;
		panel_1.add(lblLogo, gbc_lblLogo);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.VERTICAL;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 0;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{216, 0};
		gbl_panel_2.rowHeights = new int[]{1, 36, 1, 16, 1, 26, 1, 16, 26, 1, 26, 1, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.VERTICAL;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 0;
		panel_2.add(separator, gbc_separator);
		
		JLabel lblLibManSys = new JLabel("<html><center>LIBRARY MANAGEMENT<br>SYSTEM</center></html>");
		GridBagConstraints gbc_lblLibManSys = new GridBagConstraints();
		gbc_lblLibManSys.anchor = GridBagConstraints.SOUTH;
		gbc_lblLibManSys.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblLibManSys.insets = new Insets(0, 0, 5, 0);
		gbc_lblLibManSys.gridx = 0;
		gbc_lblLibManSys.gridy = 1;
		panel_2.add(lblLibManSys, gbc_lblLibManSys);
		lblLibManSys.setHorizontalAlignment(SwingConstants.CENTER);
		lblLibManSys.setFont(new Font("Dialog", Font.BOLD, 15));
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.VERTICAL;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 2;
		panel_2.add(separator_1, gbc_separator_1);
		
		
		JLabel lblEmail = new JLabel("Email");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.NORTH;
		gbc_lblEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 0);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 3;
		panel_2.add(lblEmail, gbc_lblEmail);
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		
		txtEmail = new JTextField();
		txtEmail.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_txtEmail = new GridBagConstraints();
		gbc_txtEmail.anchor = GridBagConstraints.NORTH;
		gbc_txtEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmail.insets = new Insets(0, 0, 5, 0);
		gbc_txtEmail.gridx = 0;
		gbc_txtEmail.gridy = 5;
		panel_2.add(txtEmail, gbc_txtEmail);
		txtEmail.setColumns(10);
		
		JSeparator separator_4 = new JSeparator();
		separator_4.setForeground(new Color(96, 98, 101));
		GridBagConstraints gbc_separator_4 = new GridBagConstraints();
		gbc_separator_4.fill = GridBagConstraints.VERTICAL;
		gbc_separator_4.insets = new Insets(0, 0, 5, 0);
		gbc_separator_4.gridx = 0;
		gbc_separator_4.gridy = 6;
		panel_2.add(separator_4, gbc_separator_4);
		
		JLabel lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.NORTH;
		gbc_lblPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 0);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 7;
		panel_2.add(lblPassword, gbc_lblPassword);
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_pwdPassword = new GridBagConstraints();
		gbc_pwdPassword.anchor = GridBagConstraints.NORTH;
		gbc_pwdPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_pwdPassword.insets = new Insets(0, 0, 5, 0);
		gbc_pwdPassword.gridx = 0;
		gbc_pwdPassword.gridy = 8;
		panel_2.add(pwdPassword, gbc_pwdPassword);
		
		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.fill = GridBagConstraints.VERTICAL;
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 9;
		panel_2.add(separator_3, gbc_separator_3);
		
		JButton btnLogin = new JButton("Login");
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.fill = GridBagConstraints.BOTH;
		gbc_btnLogin.insets = new Insets(0, 0, 5, 0);
		gbc_btnLogin.gridx = 0;
		gbc_btnLogin.gridy = 10;
		panel_2.add(btnLogin, gbc_btnLogin);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login(new LibraryAccount());
			}
		});
		
		JSeparator separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.VERTICAL;
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 11;
		panel_2.add(separator_2, gbc_separator_2);
		
		

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
