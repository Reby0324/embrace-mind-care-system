package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.Schedule;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class ProgressPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private JTextField dateField;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProgressPanel() {
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
        top.setBorder(BorderFactory.createTitledBorder("當日看診 / 預約進度"));
        JPanel form = new JPanel(new GridLayout(1, 3, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);
        dateField = new JTextField("2026-07-08");
        JButton button = UIStyle.actionButton("更新進度");
        form.add(new JLabel("日期 yyyy-MM-dd"));
        form.add(dateField);
        form.add(button);
        top.add(form, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        button.addActionListener(e -> refresh());
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
        JLabel hint = new JLabel("精神科門診顯示目前序號；心理諮商與臨床心理衡鑑則以固定預約時段為主。實際順序仍以現場為準。表示用示範資料估算。 ");
        hint.setFont(UIStyle.NORMAL_FONT);
        add(hint, BorderLayout.SOUTH);
    }

    private void refresh() {
        String[] columns = {"服務地點", "專業人員", "時段", "目前序號", "下一位", "未服務人數", "預約方式"};
        tableModel.setDataVector(new Object[0][0], columns);
        try {
            LocalDate date = DateUtil.parseDate(dateField.getText());
            List<Schedule> schedules = appointmentService.getSchedulesByDate(date);
            for (Schedule s : schedules) {
                int bookedCount=s.getBookedCount();
               
                int current;
                int next;
                
                if(bookedCount <=0){
                	current=0;
                	next=1;
                }else {
                	current=Math.min(5, bookedCount);
                	next=current+1;
                	if(next>s.getQuota()) {
                		next=s.getQuota();
                	}
                }
                
                int waiting = Math.max(s.getBookedCount() - current, 0);
                
                String mode = isTimedService(s) ? "固定時段" : "叫號看診";
                tableModel.addRow(new Object[] {
                        s.getServiceTypeName(),
                        s.getProfessionalName() + "（" + s.getRole() + "）",
                        s.getSession() + " " + DateUtil.formatTime(s.getStartTime()),
                        String.format("%03d", current),
                        String.format("%03d", next),
                        waiting + " 人",
                        mode
                });
            }
        } catch (Exception e) {
            tableModel.addRow(new Object[] {"日期格式錯誤", "請輸入 yyyy-MM-dd", "", "", "", "", ""});
        }
    }

    private boolean isTimedService(Schedule s) {
        return s.getServiceTypeName().contains("諮商") || s.getServiceTypeName().contains("治療") || s.getServiceTypeName().contains("衡鑑");
    }
}
