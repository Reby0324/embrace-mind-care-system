package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exception.AppException;
import model.Schedule;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class RegisterDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final Schedule schedule;
    private JTextField nameField;
    private JTextField idNoField;
    private JTextField birthDateField;
    private JTextField phoneField;
    private boolean success;

    public RegisterDialog(Schedule schedule) {
        this.schedule = schedule;
        setTitle("預約資料輸入");
        setModal(true);
        setSize(560, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(root, 15, 15, 15, 15);
        setContentPane(root);

        JLabel title = new JLabel("確認預約資料");
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(12, 2, 8, 8));
        center.setBackground(UIStyle.BACKGROUND);
        center.setBorder(BorderFactory.createTitledBorder("預約時段"));
        
        addRow(center, "服務地點：", schedule.getServiceTypeName());
        addRow(center, "服務科別：", schedule.getMainCategory());
        addRow(center, "預約項目：", schedule.getAppointmentType());
        addRow(center, "專業人員：", schedule.getProfessionalName() + "（" + schedule.getRole() + "）");
        addRow(center, "日期：", DateUtil.formatDate(schedule.getAppointmentDate()));
        addRow(center, "時段：", schedule.getSession() + " " + DateUtil.formatTime(schedule.getStartTime()) + " - " + DateUtil.formatTime(schedule.getEndTime()));
        addRow(center, "地點：", schedule.getLocation());
        addRow(center, "剩餘名額：", schedule.getRemainCount() + " 名");

        nameField = new JTextField();
        idNoField = new JTextField();
        birthDateField = new JTextField("1998-01-01");
        phoneField = new JTextField("0912345678");
        addInput(center, "姓名：", nameField);
        addInput(center, "身分證字號：", idNoField);
        addInput(center, "出生日期：", birthDateField);
        addInput(center, "手機號碼：", phoneField);

        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(UIStyle.BACKGROUND);
        JButton confirm = UIStyle.actionButton("確認預約");
        JButton cancel = UIStyle.actionButton("取消");
        bottom.add(confirm);
        bottom.add(cancel);
        root.add(bottom, BorderLayout.SOUTH);

        confirm.addActionListener(e -> doReserve());
        cancel.addActionListener(e -> dispose());
    }

    private void addRow(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        JLabel v = new JLabel(value);
        l.setFont(UIStyle.NORMAL_FONT);
        v.setFont(UIStyle.NORMAL_FONT);
        panel.add(l);
        panel.add(v);
    }

    private void addInput(JPanel panel, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(UIStyle.NORMAL_FONT);
        field.setFont(UIStyle.NORMAL_FONT);
        panel.add(l);
        panel.add(field);
    }

    private void doReserve() {
        try {
            LocalDate birthDate = DateUtil.parseDate(birthDateField.getText());
            int appointmentNo = appointmentService.reserve(
                    schedule.getId(),
                    nameField.getText(),
                    idNoField.getText(),
                    birthDate,
                    phoneField.getText());
            success = true;
            dispose();
            AppointmentSuccessDialog successDialog = new AppointmentSuccessDialog(schedule, appointmentNo);
            successDialog.setVisible(true);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "出生日期格式請輸入 yyyy-MM-dd");
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
