package com.ebookmanagerclient.ui;

import com.ebookmanagerclient.controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lớp Giao diện (View) cho màn hình Đăng nhập.
 * Nó sở hữu một LoginController để xử lý logic.
 */
public class LoginGUI extends JFrame {

    // Controller mà View này sẽ nói chuyện
    private final LoginController controller;

    // Các thành phần UI
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;

    /**
     * Constructor
     */
    public LoginGUI() {
        // 1. Khởi tạo Controller
        this.controller = new LoginController();

        // 2. Thiết lập cửa sổ
        setTitle("Ebook Manager - Sign In");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Thiết lập Layout chính
        setLayout(new GridLayout(1, 2));

        // Panel bên trái (màu xanh)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(40, 167, 69));
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.insets = new Insets(10, 30, 10, 30);
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.anchor = GridBagConstraints.CENTER;

        // Welcome text
        JLabel welcomeLabel = new JLabel("WELCOME BACK!", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.insets = new Insets(0, 30, 20, 30);
        leftPanel.add(welcomeLabel, leftGbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>To keep connected with us please<br>login with your personal info</div></html>", SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(0, 30, 30, 30);
        leftPanel.add(subtitleLabel, leftGbc);

        // Panel bên phải (màu trắng)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        
        // Title cho form đăng nhập
        JLabel signInLabel = new JLabel("Sign In", SwingConstants.CENTER);
        signInLabel.setFont(new Font("Arial", Font.BOLD, 24));
        signInLabel.setForeground(new Color(33, 37, 41));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(signInLabel, gbc);

        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 30, 5, 30);
        rightPanel.add(userLabel, gbc);

        userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(200, 35));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 30, 20, 30);
        rightPanel.add(userField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 5, 30);
        rightPanel.add(passLabel, gbc);

        passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(200, 35));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 20, 30);
        rightPanel.add(passField, gbc);

        // Sign In button
        loginButton = new JButton("SIGN IN");
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setBackground(new Color(40, 167, 69));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 20, 30);
        rightPanel.add(loginButton, gbc);

        // "Don't have an account?" text
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Don't have an account? ");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        noAccountLabel.setForeground(new Color(108, 117, 125));
        registerPanel.add(noAccountLabel);

        // Create account link
        registerButton = new JButton("<html><u>Create new account</u></html>");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(new Color(40, 167, 69));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setFocusPainted(false);
        registerPanel.add(registerButton);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 30, 10, 30);
        rightPanel.add(registerPanel, gbc);

        // Thêm panels vào frame
        add(leftPanel);
        add(rightPanel);

        // 5. Gắn (attach) các trình xử lý sự kiện
        addListeners();
    }

    /**
     * Hàm nội bộ (private) để gắn các ActionListener
     */
    private void addListeners() {
        
        // Xử lý sự kiện cho nút "Đăng nhập"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Xử lý sự kiện cho nút "Đăng ký"
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        
        // Cho phép nhấn Enter để đăng nhập
        passField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    /**
     * Logic được gọi khi nhấn nút "Đăng nhập"
     */
    private void handleLogin() {
        // 1. Lấy dữ liệu từ UI
        String username = userField.getText();
        String password = new String(passField.getPassword());

        // 2. Kiểm tra UI (không gửi yêu cầu rỗng)
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.",
                    "Lỗi Đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Pass request for controller
        boolean success = controller.handleLogin(username, password);

        // 4. Xử lý kết quả trả về từ Controller
        if (success) {
            // Đăng nhập thành công
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công! Chào mừng " + controller.getCurrentUser().getUsername() + ".",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Mở màn hình chính (MainGUI)
            // (Chúng ta sẽ code MainGUI ở bước tiếp theo)
            MainGUI mainGUI = new MainGUI();
            mainGUI.setVisible(true);

            // Đóng cửa sổ đăng nhập này
            this.dispose(); 
        } else {
            // Đăng nhập thất bại
            JOptionPane.showMessageDialog(this,
                    "Sai tên đăng nhập hoặc mật khẩu.",
                    "Lỗi Đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Logic được gọi khi nhấn nút "Đăng ký"
     */
    private void handleRegister() {
        // Mở giao diện đăng ký
        RegisterGUI registerGUI = new RegisterGUI();
        registerGUI.setVisible(true);
        // Đóng giao diện đăng nhập
        this.dispose();
    }
}