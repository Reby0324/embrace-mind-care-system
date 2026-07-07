package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import model.Schedule;
import util.UIStyle;

public class DateReservationPanel extends AbstractSchedulePanel {

    private static final long serialVersionUID = 1L;

    private JDateChooser dateChooser;
    private JLabel summaryLabel;

    public DateReservationPanel() {
        super();
        initTopPanel();
        initTable("依日期預約：先選擇日期，再查看當天上午、下午、夜診可預約的服務。雙擊資料列也可以預約。");
        refresh();
    }

    private void initTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(UIStyle.BACKGROUND);
        top.setBorder(BorderFactory.createTitledBorder("依日期預約"));

        JPanel form = new JPanel(new GridLayout(1, 4, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);

        JLabel label = new JLabel("日期：");
        label.setFont(UIStyle.SUBTITLE_FONT);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(UIStyle.NORMAL_FONT);

        // 預設日期：2026-07-08
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.of(2026, 7, 8)));

        JButton searchButton = UIStyle.actionButton("查詢");

        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(UIStyle.NORMAL_FONT);

        form.add(label);
        form.add(dateChooser);
        form.add(searchButton);
        form.add(summaryLabel);

        top.add(form, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        searchButton.addActionListener(e -> refresh());
    }

    @Override
    protected void refresh() {
        try {
            Date selectedDate = dateChooser.getDate();

            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "請選擇日期");
                renderSchedules(java.util.Collections.emptyList());
                summaryLabel.setText("尚未選擇日期");
                return;
            }

            LocalDate date = selectedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            List<Schedule> data = appointmentService.getSchedulesByDate(date);

            renderSchedules(data);

            long morning = data.stream()
                    .filter(s -> "上午".equals(s.getSession()))
                    .count();

            long afternoon = data.stream()
                    .filter(s -> "下午".equals(s.getSession()))
                    .count();

            long night = data.stream()
                    .filter(s -> "夜診".equals(s.getSession()))
                    .count();

            summaryLabel.setText(
                    "上午 " + morning + " 筆，下午 " + afternoon + " 筆，夜診 " + night + " 筆，共 " + data.size() + " 筆"
            );

        } catch (Exception e) {
            e.printStackTrace();
            summaryLabel.setText("查詢日期失敗");
            renderSchedules(java.util.Collections.emptyList());
        }
    }
}