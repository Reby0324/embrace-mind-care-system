package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Schedule;
import util.DateUtil;
import util.UIStyle;

public class AppointmentSuccessDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public AppointmentSuccessDialog(Schedule schedule, int appointmentNo) {
        setTitle("預約完成");
        setModal(true);
        setSize(720, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI(schedule, appointmentNo);
    }

    private void initUI(Schedule schedule, int appointmentNo) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(root, 15, 15, 15, 15);
        setContentPane(root);

        JLabel title = new JLabel("預約完成", SwingConstants.CENTER);
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(248, 242, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.PRIMARY.darker(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel subTitle = new JLabel("擁抱身心醫療預約資料");
        subTitle.setFont(UIStyle.SUBTITLE_FONT);
        subTitle.setForeground(UIStyle.TEXT);
        card.add(subTitle, BorderLayout.NORTH);

        JPanel infoWrapper = new JPanel(new BorderLayout(10, 10));
        infoWrapper.setOpaque(false);

        JPanel info = new JPanel(new GridLayout(6, 2, 8, 8));
        info.setOpaque(false);

        addInfo(info, "預約日期：", DateUtil.formatRocDate(schedule.getAppointmentDate()));
        addInfo(info, "服務類別：", schedule.getMainCategory());
        addInfo(info, "服務地點：", schedule.getServiceTypeName());
        addInfo(info, "預約項目：", schedule.getAppointmentType());
        addInfo(info, "專業人員：", schedule.getProfessionalName() + "（" + schedule.getRole() + "）");
        addInfo(info, "地點：", schedule.getLocation());

        infoWrapper.add(info, BorderLayout.CENTER);
        infoWrapper.add(new QrPanel(), BorderLayout.EAST);
        card.add(infoWrapper, BorderLayout.CENTER);

        JPanel numberPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        numberPanel.setOpaque(false);

        JLabel numberTitle = new JLabel("預約序號：", SwingConstants.RIGHT);
        numberTitle.setFont(UIStyle.SUBTITLE_FONT);

        JLabel number = new JLabel(String.format("%03d", appointmentNo), SwingConstants.LEFT);
        number.setFont(new java.awt.Font("微軟正黑體", java.awt.Font.BOLD, 48));
        number.setForeground(Color.RED.darker());

        JLabel timeTitle = new JLabel("預約時間：", SwingConstants.RIGHT);
        timeTitle.setFont(UIStyle.SUBTITLE_FONT);

        JLabel time = new JLabel(DateUtil.formatTime(schedule.getStartTime()), SwingConstants.LEFT);
        time.setFont(UIStyle.SUBTITLE_FONT);

        numberPanel.add(numberTitle);
        numberPanel.add(number);
        numberPanel.add(timeTitle);
        numberPanel.add(time);

        JTextArea note = new JTextArea();
        note.setEditable(false);
        note.setOpaque(false);
        note.setFont(UIStyle.NORMAL_FONT);
        note.setText("提醒您：\n"
                + "• 請於預約時間前 10 至 15 分鐘完成報到。\n"
                + "• 若需取消預約，請至【查詢 / 取消預約】功能操作。\n"
                + "• 心理諮商與臨床心理衡鑑需準時開始，遲到可能影響當次服務時間。\n"
                + "• 本系統僅供預約使用，不能取代醫師或心理師專業評估。");

        JPanel lower = new JPanel(new BorderLayout());
        lower.setOpaque(false);
        lower.add(numberPanel, BorderLayout.NORTH);
        lower.add(note, BorderLayout.CENTER);

        card.add(lower, BorderLayout.SOUTH);
        root.add(card, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIStyle.BACKGROUND);

        JButton print = UIStyle.actionButton("列印");
        JButton download = UIStyle.actionButton("下載");
        JButton close = UIStyle.actionButton("關閉");

        buttonPanel.add(print);
        buttonPanel.add(download);
        buttonPanel.add(close);

        root.add(buttonPanel, BorderLayout.SOUTH);

        close.addActionListener(e -> dispose());

        print.addActionListener(e -> printTextReport(schedule, appointmentNo));

        download.addActionListener(e -> downloadTextReport(schedule, appointmentNo));
    }

    private void addInfo(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        JLabel v = new JLabel(value);

        l.setFont(UIStyle.NORMAL_FONT);
        v.setFont(UIStyle.NORMAL_FONT);

        panel.add(l);
        panel.add(v);
    }

    private String createReportText(Schedule schedule, int appointmentNo) {
        StringBuilder sb = new StringBuilder();

        sb.append("擁抱身心醫療預約資料\n");
        sb.append("====================================\n\n");

        sb.append("預約日期：")
          .append(DateUtil.formatRocDate(schedule.getAppointmentDate()))
          .append("\n");

        sb.append("服務主軸：")
          .append(schedule.getMainCategory())
          .append("\n");

        sb.append("服務類別：")
          .append(schedule.getServiceTypeName())
          .append("\n");

        sb.append("預約項目：")
          .append(schedule.getAppointmentType())
          .append("\n");

        sb.append("治療師：")
          .append(schedule.getProfessionalName())
          .append("（")
          .append(schedule.getRole())
          .append("）\n");

        sb.append("地點：")
          .append(schedule.getLocation())
          .append("\n\n");

        sb.append("------------------------------------\n\n");

        sb.append("預約序號：")
          .append(String.format("%03d", appointmentNo))
          .append("\n");

        sb.append("預約時間：")
          .append(DateUtil.formatTime(schedule.getStartTime()))
          .append("\n\n");

        sb.append("提醒您：\n");
        sb.append("1. 請於預約時間前 10 至 15 分鐘完成報到。\n");
        sb.append("2. 若需取消預約，請至【查詢 / 取消預約】功能操作。\n");
        sb.append("3. 心理諮商與臨床心理衡鑑需準時開始，遲到可能影響當次服務時間。\n");
        sb.append("4. 本系統僅供預約使用，不能取代醫師或心理師專業評估。\n\n");

        sb.append("====================================\n");
        sb.append("擁抱身心醫療預約系統\n");

        return sb.toString();
    }

    private void printTextReport(Schedule schedule, int appointmentNo) {
        JTextArea reportArea = new JTextArea();
        reportArea.setText(createReportText(schedule, appointmentNo));
        reportArea.setFont(new java.awt.Font("微軟正黑體", java.awt.Font.PLAIN, 14));
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);

        try {
            boolean complete = reportArea.print();

            if (complete) {
                JOptionPane.showMessageDialog(this, "列印完成");
            } else {
                JOptionPane.showMessageDialog(this, "已取消列印");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "列印失敗：" + e.getMessage());
        }
    }

    private void downloadTextReport(Schedule schedule, int appointmentNo) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("下載預約資料");
        chooser.setSelectedFile(new File("預約完成資料.txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("文字檔案 (*.txt)", "txt"));

        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try {
                Files.write(file.toPath(),
                        createReportText(schedule, appointmentNo).getBytes(StandardCharsets.UTF_8));

                JOptionPane.showMessageDialog(this, "下載完成：\n" + file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "下載失敗：" + e.getMessage());
            }
        }
    }

    private static class QrPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        QrPanel() {
            setPreferredSize(new Dimension(130, 130));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLACK);

            int size = 8;

            for (int y = 2; y < 14; y++) {
                for (int x = 2; x < 14; x++) {
                    if ((x * y + x + y) % 3 == 0 || (x + y) % 5 == 0) {
                        g.fillRect(x * size, y * size, size, size);
                    }
                }
            }

            g.drawRect(15, 15, 24, 24);
            g.drawRect(91, 15, 24, 24);
            g.drawRect(15, 91, 24, 24);
        }
    }
}