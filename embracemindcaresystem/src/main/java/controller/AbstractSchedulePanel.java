package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.Schedule;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public abstract class AbstractSchedulePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    protected final AppointmentService appointmentService = new AppointmentServiceImpl();
    protected JTable table;
    protected DefaultTableModel tableModel;
    protected List<Schedule> schedules = new ArrayList<>();
    protected Map<Integer, Schedule> rowScheduleMap = new HashMap<>();

    public AbstractSchedulePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);
    }

    protected void initTable(String hint) {
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(UIStyle.NORMAL_FONT);
        table.setRowHeight(38);
        table.setGridColor(new Color(235, 210, 205));
        table.setSelectionBackground(new Color(235, 178, 163, 120));
        table.getTableHeader().setFont(new Font("微軟正黑體", Font.BOLD, 15));
        table.getTableHeader().setBackground(UIStyle.TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UIStyle.BACKGROUND);
                }
                return c;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openRegisterDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UIStyle.BACKGROUND);
        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(UIStyle.NORMAL_FONT);
        hintLabel.setForeground(UIStyle.TEXT);
        JButton reserveButton = UIStyle.actionButton("立即預約");
        bottom.add(hintLabel, BorderLayout.CENTER);
        bottom.add(reserveButton, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
        reserveButton.addActionListener(e -> openRegisterDialog());
    }

    protected void renderSchedules(List<Schedule> data) {
        schedules = data;
        rowScheduleMap.clear();
        String[] columns = {"日期", "時段", "服務類別", "預約類型", "專業人員", "地點", "時間", "剩餘名額"};
        tableModel.setDataVector(new Object[0][0], columns);

        for (int i = 0; i < data.size(); i++) {
            Schedule s = data.get(i);
            tableModel.addRow(new Object[] {
                    DateUtil.formatDate(s.getAppointmentDate()),
                    s.getSession(),
                    s.getServiceTypeName(),
                    s.getAppointmentType(),
                    s.getProfessionalName() + "（" + s.getRole() + "）",
                    s.getLocation(),
                    DateUtil.formatTime(s.getStartTime()) + " - " + DateUtil.formatTime(s.getEndTime()),
                    s.getRemainCount() + " 名"
            });
            rowScheduleMap.put(i, s);
        }

        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(95);
            table.getColumnModel().getColumn(1).setPreferredWidth(70);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(130);
            table.getColumnModel().getColumn(4).setPreferredWidth(170);
            table.getColumnModel().getColumn(5).setPreferredWidth(90);
            table.getColumnModel().getColumn(6).setPreferredWidth(120);
            table.getColumnModel().getColumn(7).setPreferredWidth(80);
        }
    }

    protected void openRegisterDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "請先選擇一筆可預約時段");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        Schedule schedule = rowScheduleMap.get(modelRow);
        if (schedule == null) {
            JOptionPane.showMessageDialog(this, "查無選取的時段資料");
            return;
        }
        if (schedule.getRemainCount() <= 0) {
            JOptionPane.showMessageDialog(this, "此時段已額滿，請選擇其他時段");
            return;
        }

        RegisterDialog dialog = new RegisterDialog(schedule);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            refresh();
        }
    }

    protected abstract void refresh();
}
