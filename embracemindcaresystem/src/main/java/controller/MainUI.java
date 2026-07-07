package controller;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.UIStyle;

public class MainUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JPanel centerPanel;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainUI frame = new MainUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainUI() {
        setTitle("擁抱身心醫療預約系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1360, 820);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIStyle.BACKGROUND);
        setContentPane(contentPane);
        initHeader();
        initCenter();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.BACKGROUND);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIStyle.BACKGROUND);

        JLabel title = new JLabel("  擁抱身心醫療預約系統");
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        title.setOpaque(true);
        title.setBackground(UIStyle.BACKGROUND);
        titlePanel.add(title, BorderLayout.WEST);

        JLabel userLabel = new JLabel("一般大眾可直接預約  ");
        userLabel.setFont(UIStyle.NORMAL_FONT);
        userLabel.setForeground(UIStyle.TEXT);
        titlePanel.add(userLabel, BorderLayout.EAST);
        header.add(titlePanel, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("  醫院精神科門診｜心理諮商 / 心理治療｜臨床心理衡鑑");
        subtitle.setFont(UIStyle.SUBTITLE_FONT);
        subtitle.setForeground(UIStyle.TEXT);
        subtitle.setOpaque(true);
        subtitle.setBackground(new Color(255, 244, 241));
        header.add(subtitle, BorderLayout.CENTER);

        JPanel menu = buildMenu();
        header.add(menu, BorderLayout.SOUTH);
        contentPane.add(header, BorderLayout.NORTH);
    }

    private JPanel buildMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(UIStyle.PRIMARY);
        menu.setLayout(new GridLayout(1, 7, 2, 2));

        JButton dateButton = UIStyle.menuButton("依日期預約");
        JButton serviceButton = UIStyle.menuButton("依服務類別預約");
        JButton professionalButton = UIStyle.menuButton("依專業人員預約");
        JButton queryButton = UIStyle.menuButton("查詢 / 取消預約");
        JButton progressButton = UIStyle.menuButton("當日看診進度");
        JButton instructionButton = UIStyle.menuButton("預約說明");
        JButton managerButton = UIStyle.menuButton("管理員專區");

        menu.add(dateButton);
        menu.add(serviceButton);
        menu.add(professionalButton);
        menu.add(queryButton);
        menu.add(progressButton);
        menu.add(instructionButton);
        menu.add(managerButton);

        dateButton.addActionListener(e -> cardLayout.show(centerPanel, "DATE"));
        serviceButton.addActionListener(e -> cardLayout.show(centerPanel, "SERVICE"));
        professionalButton.addActionListener(e -> cardLayout.show(centerPanel, "PROFESSIONAL"));
        queryButton.addActionListener(e -> cardLayout.show(centerPanel, "QUERY"));
        progressButton.addActionListener(e -> cardLayout.show(centerPanel, "PROGRESS"));
        instructionButton.addActionListener(e -> cardLayout.show(centerPanel, "INSTRUCTION"));
        managerButton.addActionListener(e -> openManagerArea());

        return menu;
    }

    private void initCenter() {
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setBackground(UIStyle.BACKGROUND);

        centerPanel.add(new DateReservationPanel(), "DATE");
        centerPanel.add(new ServiceCategoryPanel(), "SERVICE");
        centerPanel.add(new ProfessionalReservationPanel(), "PROFESSIONAL");
        centerPanel.add(new QueryCancelPanel(), "QUERY");
        centerPanel.add(new ProgressPanel(), "PROGRESS");
        centerPanel.add(new InstructionPanel(), "INSTRUCTION");

        contentPane.add(centerPanel, BorderLayout.CENTER);
        cardLayout.show(centerPanel, "DATE");
    }

    private void openManagerArea() {
        ManagerAreaDialog dialog = new ManagerAreaDialog(this);
        dialog.setVisible(true);
    }
}
