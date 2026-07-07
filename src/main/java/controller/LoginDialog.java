package controller;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.Professional;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.UIStyle;

public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_PSYCHOLOGIST = "PSYCHOLOGIST";

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final String roleType;

    private JComboBox<Professional> professionalCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loginSuccess;
    private Professional selectedProfessional;

    public LoginDialog(Dialog owner, String roleType) {
        super(owner, getDialogTitle(roleType), true);
        this.roleType = roleType;
        setSize(560, 420);
        setLocationRelativeTo(owner);
        setResizable(false);
        initUI();
    }

    private static String getDialogTitle(String roleType) {
        if (ROLE_DOCTOR.equals(roleType)) {
            return "醫師身分登入";
        }
        return "心理師身分登入";
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(root, 15, 15, 15, 15);
        setContentPane(root);

        JLabel title = new JLabel(getDialogTitle(roleType), SwingConstants.CENTER);
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(UIStyle.BACKGROUND);
        center.setBorder(BorderFactory.createTitledBorder("請登入"));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);

        JLabel professionalLabel = new JLabel(getProfessionalLabel());
        JLabel usernameLabel = new JLabel("帳號：");
        JLabel passwordLabel = new JLabel("密碼：");
        professionalLabel.setFont(UIStyle.NORMAL_FONT);
        usernameLabel.setFont(UIStyle.NORMAL_FONT);
        passwordLabel.setFont(UIStyle.NORMAL_FONT);

        professionalCombo = new JComboBox<>();
        professionalCombo.setFont(UIStyle.NORMAL_FONT);
        loadProfessionals();

        usernameField = new JTextField(getDefaultUsername());
        passwordField = new JPasswordField("1234");
        usernameField.setFont(UIStyle.NORMAL_FONT);
        passwordField.setFont(UIStyle.NORMAL_FONT);

        form.add(professionalLabel);
        form.add(professionalCombo);
        form.add(usernameLabel);
        form.add(usernameField);
        form.add(passwordLabel);
        form.add(passwordField);
        center.add(form, BorderLayout.NORTH);

        JTextArea demo = new JTextArea();
        demo.setEditable(false);
        demo.setOpaque(false);
        demo.setFont(UIStyle.NORMAL_FONT);
        demo.setText(getDemoText());
        center.add(demo, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(UIStyle.BACKGROUND);
        JButton loginButton = UIStyle.actionButton("登入");
        JButton closeButton = UIStyle.actionButton("離開");
        bottom.add(loginButton);
        bottom.add(closeButton);
        root.add(bottom, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> doLogin());
        closeButton.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(loginButton);
    }

    private String getProfessionalLabel() {
        if (ROLE_DOCTOR.equals(roleType)) {
            return "醫師姓名：";
        }
        return "心理師姓名：";
    }

    private String getDefaultUsername() {
        if (ROLE_DOCTOR.equals(roleType)) {
            return "doctor";
        }
        return "psychologist";
    }

    private String getDemoText() {
        if (ROLE_DOCTOR.equals(roleType)) {
            return "測試帳號：\n醫師：doctor / 1234\n\n請先選擇醫師姓名，再輸入帳號與密碼登入。";
        }
        return "測試帳號：\n心理師：psychologist / 1234\n\n請先選擇臨床心理師或諮商心理師姓名，再輸入帳號與密碼登入。";
    }

    private void loadProfessionals() {
        try {
            List<Professional> list = appointmentService.getProfessionals();
            List<Professional> filtered;

            if (ROLE_DOCTOR.equals(roleType)) {
                filtered = list.stream()
                        .filter(p -> p.getRole() != null && p.getRole().contains("醫師"))
                        .collect(Collectors.toList());
            } else {
                filtered = list.stream()
                        .filter(p -> p.getRole() != null && p.getRole().contains("心理師"))
                        .collect(Collectors.toList());
            }

            for (Professional p : filtered) {
                professionalCombo.addItem(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "讀取專業人員名單失敗：" + e.getMessage());
        }
    }

    private void doLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (professionalCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "請選擇姓名");
            return;
        }

        if (ROLE_DOCTOR.equals(roleType)) {
            if (!"doctor".equals(username) || !"1234".equals(password)) {
                JOptionPane.showMessageDialog(this, "醫師帳號或密碼錯誤，請輸入 doctor / 1234");
                return;
            }
        } else {
            if (!"psychologist".equals(username) || !"1234".equals(password)) {
                JOptionPane.showMessageDialog(this, "心理師帳號或密碼錯誤，請輸入 psychologist / 1234");
                return;
            }
        }

        selectedProfessional = (Professional) professionalCombo.getSelectedItem();
        loginSuccess = true;
        dispose();
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public Professional getSelectedProfessional() {
        return selectedProfessional;
    }
}
