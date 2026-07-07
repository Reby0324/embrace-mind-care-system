package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Professional;
import util.UIStyle;

public class ManagerAreaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public ManagerAreaDialog(Frame owner) {
        super(owner, "管理員專區", true);
        initUI();

        setSize(520, 300);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(root, 15, 15, 15, 15);
        setContentPane(root);

        JLabel title = new JLabel("管理員專區", SwingConstants.CENTER);
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 20));
        centerPanel.setBackground(UIStyle.BACKGROUND);
        centerPanel.setBorder(BorderFactory.createTitledBorder("身分選擇"));

        JLabel hint = new JLabel("請選擇您的身分", SwingConstants.CENTER);
        hint.setFont(UIStyle.SUBTITLE_FONT);
        hint.setForeground(UIStyle.TEXT);
        centerPanel.add(hint, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 25));
        buttonPanel.setBackground(UIStyle.BACKGROUND);

        JButton doctorButton = UIStyle.actionButton("醫師");
        JButton psychologistButton = UIStyle.actionButton("心理師");

        doctorButton.setPreferredSize(new Dimension(150, 35));
        psychologistButton.setPreferredSize(new Dimension(150, 35));

        buttonPanel.add(doctorButton);
        buttonPanel.add(psychologistButton);

        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        root.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UIStyle.BACKGROUND);

        JButton closeButton = UIStyle.actionButton("關閉");
        closeButton.setPreferredSize(new Dimension(90, 35));
        bottomPanel.add(closeButton);

        root.add(bottomPanel, BorderLayout.SOUTH);

        doctorButton.addActionListener(e -> openLogin(LoginDialog.ROLE_DOCTOR));
        psychologistButton.addActionListener(e -> openLogin(LoginDialog.ROLE_PSYCHOLOGIST));
        closeButton.addActionListener(e -> dispose());
    }

    private void openLogin(String roleType) {
        LoginDialog loginDialog = new LoginDialog(this, roleType);
        loginDialog.setVisible(true);

        if (!loginDialog.isLoginSuccess()) {
            return;
        }

        Professional professional = loginDialog.getSelectedProfessional();

        if (LoginDialog.ROLE_DOCTOR.equals(roleType)) {
            openDoctorBackend(professional);
        } else if (LoginDialog.ROLE_PSYCHOLOGIST.equals(roleType)) {
            openPsychologistBackend(professional);
        }
    }

    private void openDoctorBackend(Professional professional) {
        JDialog dialog = new JDialog(this, "醫師後台管理系統", true);
        dialog.setSize(1260, 760);
        dialog.setLocationRelativeTo(this);
        dialog.setContentPane(new DoctorBackendPanel(professional));
        dialog.setVisible(true);
    }

    private void openPsychologistBackend(Professional professional) {
        JDialog dialog = new JDialog(this, "心理師後台管理系統", true);
        dialog.setSize(1260, 760);
        dialog.setLocationRelativeTo(this);
        dialog.setContentPane(new PsychologistBackendPanel(professional));
        dialog.setVisible(true);
    }
}
