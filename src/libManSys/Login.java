package libManSys;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.imageio.ImageIO;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.*;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtEmail;
    private JPasswordField pwdPassword;
    private DbConnect dbCon = new DbConnect();
    private BufferedImage logoImage;
    private boolean isDarkMode = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	UIManager.setLookAndFeel(new FlatLightLaf());
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Login() {
        try {
            logoImage = ImageIO.read(new File("assets/LibManSys_Logo.png"));
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            Image icon = new ImageIcon(getClass().getResource("/assets/LibManSys_Icon.png")).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
            e.printStackTrace();
        }
        
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (logoImage != null) {
                    g.drawImage(logoImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        logoPanel.setBounds(0, 0, 450, 600); 
        contentPane.add(logoPanel);

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(450, 0, 434, 600);
        contentPane.add(loginPanel);

        JLabel lblTitle = new JLabel("W E L C O M E");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(67, 136, 300, 35);
        loginPanel.add(lblTitle);

        txtEmail = new JTextField(20);
        txtEmail.putClientProperty("JTextField.placeholderText", "Email");
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        txtEmail.setBounds(67, 236, 300, 40);
        loginPanel.add(txtEmail);

        pwdPassword = new JPasswordField(20);
        pwdPassword.putClientProperty("JTextField.placeholderText", "Password");
        pwdPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        pwdPassword.setBounds(67, 296, 300, 40);
        loginPanel.add(pwdPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 20));
        btnLogin.setBounds(67, 376, 300, 50);
        loginPanel.add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login(new LibraryAccount());
            }
        });
        
        // Toggle Theme Button
        JButton btnToggleTheme = new JButton("Toggle Theme");
        btnToggleTheme.setFont(new Font("Arial", Font.PLAIN, 12));
        btnToggleTheme.setBounds(310, 10, 114, 30);
        loginPanel.add(btnToggleTheme);
        
        btnToggleTheme.addActionListener(e -> toggleTheme());
        
        // Initialize theme
        updateTheme();
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

    public void login(LibraryAccount account) {
        String email = txtEmail.getText();
        String password = new String(pwdPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        account.setEmail(email);

        try {
            dbCon.connect();
            String select = "SELECT * FROM account WHERE email = ?";
            PreparedStatement prep = dbCon.con.prepareStatement(select);
            prep.setString(1, email);

            ResultSet result = prep.executeQuery();

            if (result.next()) {
                account.setPassword(result.getString("password"));
                account.setRole(result.getString("role"));

                if (password.equals(account.getPassword())) {
                    if ("librarian".equals(account.getRole())) {
                        String firstName = result.getString("first_name");
                        String lastName = result.getString("last_name");
                        String fullName = firstName + " " + lastName;
                    	// Redirect to librarian dashboard
                        new LibDash(fullName);
                        this.dispose();
                    } else {
						// Redirect to reader dashboard
						Reader_dashboard readerDashboard = new Reader_dashboard(account.getEmail());
						readerDashboard.setVisible(true);
						dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred during login.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
	}