package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import exception.AppException;
import model.AppointmentView;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class QueryCancelPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private JTextField idNoField;
    private JTextField birthDateField;
    private JTextField phoneField;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<AppointmentView> results = new ArrayList<>();

    public QueryCancelPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);
        initTop();
        initTable();
    }

    private void initTop() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(UIStyle.BACKGROUND);
        top.setBorder(BorderFactory.createTitledBorder("查詢 / 取消預約"));

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);
        idNoField = new JTextField();
        birthDateField = new JTextField("1998-01-01");
        phoneField = new JTextField("0912345678");
        JButton queryButton = UIStyle.actionButton("查詢預約");

        form.add(new JLabel("身分證字號"));
        form.add(new JLabel("出生日期 yyyy-MM-dd"));
        form.add(new JLabel("手機號碼"));
        form.add(new JLabel("操作"));
        form.add(idNoField);
        form.add(birthDateField);
        form.add(phoneField);
        form.add(queryButton);
        top.add(form, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        queryButton.addActionListener(e -> query());
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
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UIStyle.BACKGROUND);
        JLabel hint = new JLabel("查詢結果包含：預約日期、服務類別、治療師、時段、預約序號、狀態。請選取資料列後按取消預約。");
        hint.setFont(UIStyle.NORMAL_FONT);
        JButton cancelButton = UIStyle.actionButton("取消選取預約");
        bottom.add(hint, BorderLayout.CENTER);
        bottom.add(cancelButton, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
        cancelButton.addActionListener(e -> cancelSelected());

        render();
    }

    private void query() {
        try {
            LocalDate birthDate = DateUtil.parseDate(birthDateField.getText());
            results = appointmentService.searchAppointments(idNoField.getText(), birthDate, phoneField.getText());
            render();
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "查無預約紀錄");
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "出生日期格式請輸入 yyyy-MM-dd");
        }
    }

    private void render() {
        String[] columns = {"預約日期", "服務類別", "專業人員", "時段", "預約序號", "狀態", "地點"};
        tableModel.setDataVector(new Object[0][0], columns);
        for (AppointmentView v : results) {
            tableModel.addRow(new Object[] {
                    DateUtil.formatDate(v.getAppointmentDate()),
                    v.getServiceTypeName(),
                    v.getProfessionalName() + "（" + v.getRole() + "）",
                    v.getSession() + " " + DateUtil.formatTime(v.getStartTime()),
                    String.format("%03d", v.getAppointmentNo()),
                    v.getStatus(),
                    v.getLocation()
            });
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "請先選擇一筆預約紀錄");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        AppointmentView selected = results.get(modelRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "確定要取消預約？\n" + selected.getServiceTypeName() + "\n" + DateUtil.formatDate(selected.getAppointmentDate()),
                "取消預約", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            appointmentService.cancelAppointment(selected.getAppointmentId());
            JOptionPane.showMessageDialog(this, "已取消預約");
            query();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
