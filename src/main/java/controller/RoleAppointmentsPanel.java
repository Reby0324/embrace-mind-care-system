package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.AppointmentView;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class RoleAppointmentsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final String titleText;
    private final String roleKeyword;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel countLabel;
    private List<AppointmentView> results = new ArrayList<>();

    public RoleAppointmentsPanel(String titleText, String roleKeyword) {
        this.titleText = titleText;
        this.roleKeyword = roleKeyword;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);
        initTop();
        initTable();
        refresh();
    }

    private void initTop() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(UIStyle.BACKGROUND);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIStyle.BACKGROUND);

        JLabel title = new JLabel(titleText);
        title.setFont(UIStyle.SUBTITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        titlePanel.add(title, BorderLayout.WEST);

        countLabel = new JLabel(" ");
        countLabel.setFont(UIStyle.NORMAL_FONT);
        countLabel.setForeground(UIStyle.TEXT);
        titlePanel.add(countLabel, BorderLayout.EAST);
        top.add(titlePanel, BorderLayout.CENTER);

        JButton refreshButton = UIStyle.actionButton("重新整理");
        refreshButton.addActionListener(e -> refresh());
        top.add(refreshButton, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(UIStyle.NORMAL_FONT);
        table.setRowHeight(36);
        table.getTableHeader().setBackground(UIStyle.TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        try {
            List<AppointmentView> all = appointmentService.getAllAppointments();
            results = all.stream()
                    .filter(v -> v.getRole() != null && v.getRole().contains(roleKeyword))
                    .collect(Collectors.toList());
            render();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "讀取預約名單失敗：" + e.getMessage());
        }
    }

    private void render() {
        String[] columns = {"日期", "個案姓名", "手機", "專業人員", "服務類別", "預約項目", "時段", "序號", "狀態", "地點"};
        tableModel.setDataVector(new Object[0][0], columns);

        for (AppointmentView v : results) {
            tableModel.addRow(new Object[] {
                    DateUtil.formatDate(v.getAppointmentDate()),
                    v.getPatientName(),
                    v.getPhone(),
                    v.getProfessionalName() + "（" + v.getRole() + "）",
                    v.getServiceTypeName(),
                    v.getAppointmentType(),
                    v.getSession() + " " + DateUtil.formatTime(v.getStartTime()) + " - " + DateUtil.formatTime(v.getEndTime()),
                    String.format("%03d", v.getAppointmentNo()),
                    v.getStatus(),
                    v.getLocation()
            });
        }

        countLabel.setText("共 " + results.size() + " 筆");
    }
}
