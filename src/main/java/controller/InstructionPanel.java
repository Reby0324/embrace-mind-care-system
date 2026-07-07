package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import model.CounselingFee;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.UIStyle;

public class InstructionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    public InstructionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);

        JLabel title = new JLabel("預約說明與門診時間表");
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.NORMAL_FONT);
        tabs.addTab("門診時間表", createTimeTablePanel());
        tabs.addTab("諮商費用說明", createFeePanel());
        tabs.addTab("預約注意事項", createNoticePanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createTimeTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.BACKGROUND);
        String[] columns = {"時段", "週一", "週二", "週三", "週四", "週五"};
        Object[][] rows = {
                {"上午","成人精神科","兒童青少年精神科", "高齡心智醫學","成人精神科", "心身醫學" },
                {"上午","壓力調適","兒童青少年諮商","情緒諮商","伴侶 / 家庭諮商","創傷諮商"},
                {"下午","注意力衡鑑","情緒衡鑑","智力衡鑑","兒童發展衡鑑","人格衡鑑"},
                {"下午","心理治療","", "成癮治療","腦刺激治療","藥癮追蹤"},
                {"下午","感覺統合訓練","","","兒童職能治療", ""}};

            JTable table = new JTable(rows, columns);
            table.setRowHeight(48);
            table.setFont(UIStyle.NORMAL_FONT);
            table.getTableHeader().setFont(UIStyle.NORMAL_FONT);
            table.getTableHeader().setBackground(UIStyle.PRIMARY);
            table.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

    private JPanel createFeePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIStyle.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel("諮商費用說明");
        label.setFont(UIStyle.SUBTITLE_FONT);
        panel.add(label, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[] {"服務項目", "時間（分鐘）", "費用（NTD）", "備註"}, 0);
        List<CounselingFee> fees = appointmentService.getCounselingFees();
        if (fees.isEmpty()) {
            model.addRow(new Object[] {"個別諮商（成人/青少年）", 50, "2200-3000", "依諮商心理師與服務內容調整"});
            model.addRow(new Object[] {"兒童個別諮商", 50, "2200-3000", "建議主要照顧者陪同初談"});
            model.addRow(new Object[] {"伴侶諮商", 80, "3500-5200", "適合伴侶溝通與關係議題"});
        } else {
            for (CounselingFee fee : fees) {
                model.addRow(new Object[] {fee.getServiceName(), fee.getDurationMinutes(), fee.getPriceRange(), fee.getNote()});
            }
        }
        JTable table = new JTable(model);
        table.setFont(UIStyle.NORMAL_FONT);
        table.setRowHeight(42);
        table.getTableHeader().setBackground(UIStyle.TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JTextArea note = new JTextArea("費用為範例資料，可依機構規定調整。實際費用、付款方式與取消規則請以現場公告為準。");
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setFont(UIStyle.NORMAL_FONT);
        note.setBackground(UIStyle.BACKGROUND);
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createNoticePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setBackground(UIStyle.BACKGROUND);
        JTextArea area = new JTextArea();
        area.setFont(UIStyle.NORMAL_FONT);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(UIStyle.BACKGROUND);
        area.setText("擁抱身心醫療預約系統說明\n\n"
                + "1. 本系統提供醫院精神科門診、心理諮商 / 心理治療、臨床心理衡鑑之示範預約流程。\n\n"
                + "2. 服務類別取代一般醫院科別，使用者可依日期、服務類別或專業人員預約。\n\n"
                + "3. 查詢 / 取消預約需輸入身分證字號、出生日期與手機號碼。\n\n"
                + "4. 心理諮商與臨床心理衡鑑為固定時段服務，請準時報到。\n\n"
                + "5. 本系統是作品範例，不保存真實敏感醫療內容，也不能取代醫師或心理師專業評估。");
        panel.add(new JScrollPane(area));
        return panel;
    }
}
