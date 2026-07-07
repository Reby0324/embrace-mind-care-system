package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import exception.AppException;
import model.AppointmentView;
import model.Schedule;
import model.ServiceType;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class AdminPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;
    private JTextField mainCategoryField;
    private JTextField serviceNameField;
    private JTextField descriptionField;

    private JTable scheduleTable;
    private DefaultTableModel scheduleTableModel;
    private JTextField scheduleIdField;
    private JTextField scheduleDateField;
    private JComboBox<String> sessionComboBox;
    private JTextField appointmentTypeField;
    private JTextField locationField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField quotaField;
    private JCheckBox activeCheckBox;
    private List<Schedule> schedules = new ArrayList<>();

    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;

    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.NORMAL_FONT);
        tabs.addTab("新增服務", buildServicePanel());
        tabs.addTab("修改時段", buildSchedulePanel());
        tabs.addTab("所有預約", buildAppointmentPanel());
        add(tabs, BorderLayout.CENTER);

        refreshServices();
        refreshSchedules();
        refreshAppointments();
    }

    private JPanel buildServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIStyle.BACKGROUND);

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);
        form.setBorder(BorderFactory.createTitledBorder("新增服務類別"));

        mainCategoryField = new JTextField("醫院精神科門診");
        serviceNameField = new JTextField();
        descriptionField = new JTextField();
        JButton addButton = UIStyle.actionButton("新增服務");

        form.add(new JLabel("服務主軸"));
        form.add(new JLabel("服務名稱"));
        form.add(new JLabel("說明"));
        form.add(new JLabel("操作"));
        form.add(mainCategoryField);
        form.add(serviceNameField);
        form.add(descriptionField);
        form.add(addButton);

        panel.add(form, BorderLayout.NORTH);

        serviceTableModel = new DefaultTableModel();
        serviceTable = createTable(serviceTableModel);
        panel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addService());
        return panel;
    }

    private JPanel buildSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIStyle.BACKGROUND);

        JPanel form = new JPanel(new GridLayout(4, 6, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);
        form.setBorder(BorderFactory.createTitledBorder("選取表格資料後修改時段"));

        scheduleIdField = new JTextField();
        scheduleIdField.setEditable(false);
        scheduleDateField = new JTextField("2026-07-08");
        sessionComboBox = new JComboBox<>(new String[] {"上午", "下午", "夜診"});
        appointmentTypeField = new JTextField();
        locationField = new JTextField();
        startTimeField = new JTextField("09:00");
        endTimeField = new JTextField("12:00");
        quotaField = new JTextField("10");
        activeCheckBox = new JCheckBox("啟用");
        activeCheckBox.setBackground(UIStyle.BACKGROUND);
        activeCheckBox.setSelected(true);
        JButton updateButton = UIStyle.actionButton("修改時段");
        JButton refreshButton = UIStyle.actionButton("重新整理");

        form.add(new JLabel("時段ID"));
        form.add(new JLabel("日期 yyyy-MM-dd"));
        form.add(new JLabel("時段"));
        form.add(new JLabel("預約項目"));
        form.add(new JLabel("地點"));
        form.add(new JLabel("狀態"));
        form.add(scheduleIdField);
        form.add(scheduleDateField);
        form.add(sessionComboBox);
        form.add(appointmentTypeField);
        form.add(locationField);
        form.add(activeCheckBox);
        form.add(new JLabel("開始 HH:mm"));
        form.add(new JLabel("結束 HH:mm"));
        form.add(new JLabel("名額"));
        form.add(updateButton);
        form.add(refreshButton);
        form.add(new JLabel(""));
        form.add(startTimeField);
        form.add(endTimeField);
        form.add(quotaField);
        form.add(new JLabel(""));
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        panel.add(form, BorderLayout.NORTH);

        scheduleTableModel = new DefaultTableModel();
        scheduleTable = createTable(scheduleTableModel);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillScheduleForm();
            }
        });
        panel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        updateButton.addActionListener(e -> updateSchedule());
        refreshButton.addActionListener(e -> refreshSchedules());
        return panel;
    }

    private JPanel buildAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIStyle.BACKGROUND);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIStyle.BACKGROUND);
        JLabel title = new JLabel("所有預約紀錄");
        title.setFont(UIStyle.SUBTITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        JButton refreshButton = UIStyle.actionButton("重新整理");
        top.add(title, BorderLayout.WEST);
        top.add(refreshButton, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        appointmentTableModel = new DefaultTableModel();
        appointmentTable = createTable(appointmentTableModel);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refreshAppointments());
        return panel;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(UIStyle.NORMAL_FONT);
        table.setRowHeight(34);
        table.getTableHeader().setBackground(UIStyle.TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }

    private void addService() {
        try {
            appointmentService.addServiceType(
                    mainCategoryField.getText(),
                    serviceNameField.getText(),
                    descriptionField.getText());
            JOptionPane.showMessageDialog(this, "新增服務完成");
            serviceNameField.setText("");
            descriptionField.setText("");
            refreshServices();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void refreshServices() {
        String[] columns = {"ID", "服務主軸", "服務名稱", "說明"};
        serviceTableModel.setDataVector(new Object[0][0], columns);
        for (ServiceType st : appointmentService.getServiceTypes()) {
            serviceTableModel.addRow(new Object[] {
                    st.getId(), st.getMainCategory(), st.getName(), st.getDescription()
            });
        }
    }

    private void refreshSchedules() {
        schedules = appointmentService.getAllSchedulesForAdmin();
        String[] columns = {"ID", "日期", "時段", "服務類別", "預約項目", "專業人員", "地點", "時間", "名額", "已約", "狀態"};
        scheduleTableModel.setDataVector(new Object[0][0], columns);
        for (Schedule s : schedules) {
            scheduleTableModel.addRow(new Object[] {
                    s.getId(),
                    DateUtil.formatDate(s.getAppointmentDate()),
                    s.getSession(),
                    s.getServiceTypeName(),
                    s.getAppointmentType(),
                    s.getProfessionalName() + "（" + s.getRole() + "）",
                    s.getLocation(),
                    DateUtil.formatTime(s.getStartTime()) + " - " + DateUtil.formatTime(s.getEndTime()),
                    s.getQuota(),
                    s.getBookedCount(),
                    s.isActive() ? "啟用" : "停用"
            });
        }
    }

    private void fillScheduleForm() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int modelRow = scheduleTable.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= schedules.size()) {
            return;
        }
        Schedule s = schedules.get(modelRow);
        scheduleIdField.setText(String.valueOf(s.getId()));
        scheduleDateField.setText(DateUtil.formatDate(s.getAppointmentDate()));
        sessionComboBox.setSelectedItem(s.getSession());
        appointmentTypeField.setText(s.getAppointmentType());
        locationField.setText(s.getLocation());
        startTimeField.setText(DateUtil.formatTime(s.getStartTime()));
        endTimeField.setText(DateUtil.formatTime(s.getEndTime()));
        quotaField.setText(String.valueOf(s.getQuota()));
        activeCheckBox.setSelected(s.isActive());
    }

    private void updateSchedule() {
        try {
            if (scheduleIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "請先在表格選擇一筆時段");
                return;
            }

            int id = Integer.parseInt(scheduleIdField.getText().trim());
            Schedule original = null;
            for (Schedule s : schedules) {
                if (s.getId() == id) {
                    original = s;
                    break;
                }
            }
            if (original == null) {
                JOptionPane.showMessageDialog(this, "查無原始時段資料");
                return;
            }

            Schedule updated = new Schedule()
                    .setId(id)
                    .setAppointmentDate(LocalDate.parse(scheduleDateField.getText().trim(), DateUtil.DATE_FORMAT))
                    .setSession(String.valueOf(sessionComboBox.getSelectedItem()))
                    .setAppointmentType(appointmentTypeField.getText().trim())
                    .setLocation(locationField.getText().trim())
                    .setStartTime(LocalTime.parse(startTimeField.getText().trim(), DateUtil.TIME_FORMAT))
                    .setEndTime(LocalTime.parse(endTimeField.getText().trim(), DateUtil.TIME_FORMAT))
                    .setQuota(Integer.parseInt(quotaField.getText().trim()))
                    .setBookedCount(original.getBookedCount())
                    .setActive(activeCheckBox.isSelected());

            appointmentService.updateSchedule(updated);
            JOptionPane.showMessageDialog(this, "修改時段完成");
            refreshSchedules();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "資料格式錯誤，請確認日期、時間與名額格式");
        }
    }

    private void refreshAppointments() {
        List<AppointmentView> appointments = appointmentService.getAllAppointments();
        String[] columns = {"日期", "個案姓名", "身分證", "手機", "服務類別", "預約項目", "專業人員", "序號", "狀態", "地點"};
        appointmentTableModel.setDataVector(new Object[0][0], columns);
        for (AppointmentView v : appointments) {
            appointmentTableModel.addRow(new Object[] {
                    DateUtil.formatDate(v.getAppointmentDate()),
                    v.getPatientName(),
                    v.getIdNo(),
                    v.getPhone(),
                    v.getServiceTypeName(),
                    v.getAppointmentType(),
                    v.getProfessionalName() + "（" + v.getRole() + "）",
                    String.format("%03d", v.getAppointmentNo()),
                    v.getStatus(),
                    v.getLocation()
            });
        }
    }
}
