package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import exception.AppException;
import model.AppointmentView;
import model.User;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class ProfessionalAppointmentsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final User user;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<AppointmentView> results = new ArrayList<>();

    public ProfessionalAppointmentsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);
        initTop();
        initTable();
        refresh();
    }

    private void initTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIStyle.BACKGROUND);

        JLabel title = new JLabel("我的預約名單 - " + user.getDisplayName());
        title.setFont(UIStyle.SUBTITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        top.add(title, BorderLayout.WEST);

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
            results = appointmentService.getAppointmentsByProfessional(user.getProfessionalId() == null ? 0 : user.getProfessionalId());
            render();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void render() {
        String[] columns = {"日期", "個案姓名", "手機", "服務類別", "預約項目", "時段", "序號", "狀態", "地點"};
        tableModel.setDataVector(new Object[0][0], columns);
        for (AppointmentView v : results) {
            tableModel.addRow(new Object[] {
                    DateUtil.formatDate(v.getAppointmentDate()),
                    v.getPatientName(),
                    v.getPhone(),
                    v.getServiceTypeName(),
                    v.getAppointmentType(),
                    v.getSession() + " " + DateUtil.formatTime(v.getStartTime()) + " - " + DateUtil.formatTime(v.getEndTime()),
                    String.format("%03d", v.getAppointmentNo()),
                    v.getStatus(),
                    v.getLocation()
            });
        }
    }
}
