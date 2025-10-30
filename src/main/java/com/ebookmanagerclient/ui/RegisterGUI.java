package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lớp Giao diện (View) cho màn hình Đăng ký.
 */
public class RegisterGUI extends JFrame {

    private final LoginController controller;
    private JTextField userField;
    private JPasswordField passField;
    private JButton registerButton;
    private JButton backToLoginButton;

    public RegisterGUI() {
        this.controller = new LoginController();

        setTitle("Ebook Manager - Create Account");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        setLayout(new GridLayout(1, 2));

        // Panel bên trái (màu xanh)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(40, 167, 69));
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.insets = new Insets(10, 30, 10, 30);
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.anchor = GridBagConstraints.CENTER;
        
        JLabel welcomeLabel = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.insets = new Insets(0, 30, 20, 30);
        leftPanel.add(welcomeLabel, leftGbc);
        
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Enter your personal details<br>and start journey with us</div></html>", SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(0, 30, 30, 30);
        leftPanel.add(subtitleLabel, leftGbc);

        // Panel bên phải (màu trắng)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        
        JLabel createAccountLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        createAccountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        createAccountLabel.setForeground(new Color(33, 37, 41));
        
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(10, 30, 10, 30);
        rightGbc.fill = GridBagConstraints.HORIZONTAL;
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.gridwidth = 2;
        rightPanel.add(createAccountLabel, rightGbc);

        add(leftPanel);
        add(rightPanel);

        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightGbc.gridy = 1;
        rightGbc.gridwidth = 1;
        rightGbc.insets = new Insets(20, 30, 5, 30);
        rightPanel.add(userLabel, rightGbc);

        userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(200, 35));
        rightGbc.gridy = 2;
        rightGbc.insets = new Insets(0, 30, 20, 30);
        rightPanel.add(userField, rightGbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightGbc.gridy = 3;
        rightGbc.insets = new Insets(0, 30, 5, 30);
        rightPanel.add(passLabel, rightGbc);

        passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(200, 35));
        rightGbc.gridy = 4;
        rightGbc.insets = new Insets(0, 30, 20, 30);
        rightPanel.add(passField, rightGbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        registerButton = new JButton("SIGN UP");
        registerButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        buttonPanel.add(registerButton);

        backToLoginButton = new JButton("BACK TO LOGIN");
        backToLoginButton.setPreferredSize(new Dimension(120, 40));
        backToLoginButton.setBackground(new Color(40, 167, 69));
        backToLoginButton.setForeground(Color.WHITE);
        backToLoginButton.setFont(new Font("Arial", Font.BOLD, 14));
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setFocusPainted(false);
        buttonPanel.add(backToLoginButton);

        rightGbc.gridy = 5;
        rightGbc.gridwidth = 2;
        rightGbc.insets = new Insets(20, 30, 10, 30);
        rightPanel.add(buttonPanel, rightGbc);

        addListeners();
    }

    private void addListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mở lại màn hình đăng nhập
                new LoginGUI().setVisible(true);
                // Đóng màn hình đăng ký
                dispose();
            }
        });
    }

    private void handleRegister() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password to register.",
                    "Registration Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = controller.handleRegister(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            // Mở lại màn hình đăng nhập
            new LoginGUI().setVisible(true);
            // Đóng màn hình đăng ký
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Username may already exist.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}