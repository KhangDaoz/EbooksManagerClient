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
        // Mỗi LoginGUI sẽ tạo ra một LoginController mới
        this.controller = new LoginController();

        // 2. Thiết lập cửa sổ (JFrame)
        setTitle("Ebook Manager - Đăng nhập");
        setSize(400, 250); // Kích thước cửa sổ
        // Khi nhấn nút X (Close), toàn bộ ứng dụng sẽ thoát
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Hiển thị cửa sổ ở giữa màn hình
        setLocationRelativeTo(null);
        setResizable(false); // Không cho phép thay đổi kích thước

        // 3. Thiết lập Layout (Bố cục)
        // Chúng ta dùng GridBagLayout để căn chỉnh đẹp hơn
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 4. Thêm các thành phần UI

        // Hàng 0: Nhãn "Tên đăng nhập"
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);

        // Hàng 0: Ô nhập "Tên đăng nhập"
        userField = new JTextField(20); // Rộng 20 ký tự
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(userField, gbc);

        // Hàng 1: Nhãn "Mật khẩu"
        JLabel passLabel = new JLabel("Mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);

        // Hàng 1: Ô nhập "Mật khẩu"
        passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passField, gbc);

        // Hàng 2: Panel chứa 2 nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loginButton = new JButton("Đăng nhập");
        registerButton = new JButton("Đăng ký");
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END; // Căn phải
        add(buttonPanel, gbc);

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
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập và mật khẩu để đăng ký.",
                    "Lỗi Đăng ký",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ủy quyền (Delegate) cho Controller
        boolean success = controller.handleRegister(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thành công! Vui lòng đăng nhập.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thất bại. Tên người dùng có thể đã tồn tại.",
                    "Lỗi Đăng ký",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}