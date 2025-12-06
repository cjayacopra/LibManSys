package libManSys;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarculaLaf;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtEmail;
    private JPasswordField pwdPassword;
    DbConnect dbCon = new DbConnect();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Login frame = new Login();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Login() {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the frame

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Logo Panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        ImageIcon logo = new ImageIcon("assets/LibManSys_Logo.png");
        JLabel lblLogo = new JLabel(logo);
        logoPanel.add(lblLogo, BorderLayout.CENTER);
        mainPanel.add(logoPanel, BorderLayout.WEST);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Library Management System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(lblTitle, gbc);
        
        gbc.gridy = 1;
        loginPanel.add(Box.createVerticalStrut(20), gbc);


        txtEmail = new JTextField(20);
        txtEmail.putClientProperty("JTextField.placeholderText", "Email");
        gbc.gridy = 2;
        loginPanel.add(txtEmail, gbc);

        pwdPassword = new JPasswordField(20);
        pwdPassword.putClientProperty("JTextField.placeholderText", "Password");
        gbc.gridy = 3;
        loginPanel.add(pwdPassword, gbc);
        
        gbc.gridy = 4;
        loginPanel.add(Box.createVerticalStrut(10), gbc);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 5;
        loginPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login(new LibraryAccount());
            }
        });
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
                        new LibDash().getFrame().setVisible(true);
                        this.dispose();
                    } else {
                        // TODO: Replace this with the reader dashboard
                        JOptionPane.showMessageDialog(this, "Login successful! Your role is: reader", "Login successful", JOptionPane.INFORMATION_MESSAGE);
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

