package controller;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

import exception.AppException;
import model.AppointmentView;
import model.Professional;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class DoctorBackendPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final Professional professional;

    private DefaultTableModel tableModel;
    private JTable appointmentTable;
    private JComboBox<String> patientCombo;
    private JTextArea mseTextArea;
    private JTextArea overallTextArea;
    private JLabel warningLabel;
    private MedicationChartPanel chartPanel;
    private JSlider ssriSlider;
    private JSlider bzdSlider;
    private JSlider phqSlider;
    private JSlider isiSlider;
    private final List<JCheckBox> tagBoxes = new ArrayList<>();
    private JLabel savedMedicationLabel;
    private int savedSsri = 1;
    private int savedBzd = 1;
    private int savedPhq = 8;
    private int savedIsi = 10;

    public DoctorBackendPanel(Professional professional) {
        this.professional = professional;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);
        initUI();
        refreshAppointments();
    }

    private void initUI() {
        JLabel title = new JLabel("醫師後台管理系統｜" + professional.getName() + "（" + professional.getRole() + "）");
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.NORMAL_FONT);
        tabs.addTab("預約名單與 MSE 行為觀察", buildMseTab());
        tabs.addTab("用藥歷程與症狀時間軸", buildMedicationTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildMseTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.BACKGROUND);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(UIStyle.BACKGROUND);
        left.setBorder(BorderFactory.createTitledBorder("今日 / 已預約患者名單"));

        tableModel = new DefaultTableModel();
        appointmentTable = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable.setFont(UIStyle.NORMAL_FONT);
        appointmentTable.setRowHeight(30);
        appointmentTable.getTableHeader().setBackground(UIStyle.TABLE_HEADER);
        appointmentTable.getTableHeader().setForeground(Color.WHITE);
        left.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        JPanel leftBottom = new JPanel(new BorderLayout(5, 5));
        leftBottom.setBackground(UIStyle.BACKGROUND);
        patientCombo = new JComboBox<>();
        patientCombo.setFont(UIStyle.NORMAL_FONT);
        JButton refreshButton = UIStyle.actionButton("重新整理名單");
        refreshButton.addActionListener(e -> refreshAppointments());
        leftBottom.add(new JLabel("目前撰寫個案："), BorderLayout.WEST);
        leftBottom.add(patientCombo, BorderLayout.CENTER);
        leftBottom.add(refreshButton, BorderLayout.EAST);
        left.add(leftBottom, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(UIStyle.BACKGROUND);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(UIStyle.BACKGROUND);
        top.add(new JLabel("勾選標籤後可自動產生 MSE 文字，醫師可再於下方欄位手動修改。"));
        right.add(top, BorderLayout.NORTH);

        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildTagMatrixPanel(), buildMseEditorPanel());
        vertical.setResizeWeight(0.42);
        right.add(vertical, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
     
        split.setResizeWeight(0.60);

        left.setPreferredSize(new Dimension(720, 600));
        right.setPreferredSize(new Dimension(480, 600));

        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.60));

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildTagMatrixPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 8, 8));
        panel.setBackground(UIStyle.BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("多維度快選標籤矩陣（Tag Matrix）"));

        panel.add(createTagRow("意識與定向感", new String[] {"清晰", "嗜睡", "混亂", "人時地定向感不佳"}));
        panel.add(createTagRow("外觀與儀態", new String[] {"整潔", "不修邊幅", "與年齡不符", "奇裝異服"}));
        panel.add(createTagRow("言談表現", new String[] {"語速極快 (Pressured Speech)", "語調平淡 (Flat)", "思考中斷", "喃喃自語"}));
        panel.add(createTagRow("肢體與動作", new String[] {"靜坐不能 (Akathisia)", "雙手顫抖", "動作遲緩 (Retardation)", "眼神迴避"}));

        return panel;
    }

    private JPanel createTagRow(String category, String[] tags) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row.setBackground(UIStyle.BACKGROUND);
        JLabel label = new JLabel(category + "：");
        label.setFont(UIStyle.NORMAL_FONT);
        row.add(label);

        for (String tag : tags) {
            JCheckBox box = new JCheckBox(tag);
            box.setFont(UIStyle.NORMAL_FONT);
            box.setBackground(UIStyle.BACKGROUND);
            box.addActionListener(e -> updateMseFromTags());
            tagBoxes.add(box);
            row.add(box);
        }
        return row;
    }

    private JPanel buildMseEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UIStyle.BACKGROUND);

        JPanel editorPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        editorPanel.setBackground(UIStyle.BACKGROUND);

        overallTextArea = new JTextArea();
        overallTextArea.setFont(UIStyle.NORMAL_FONT);
        overallTextArea.setLineWrap(true);
        overallTextArea.setWrapStyleWord(true);
        overallTextArea.setText("外觀和行為觀察的總體描述：\n");

        mseTextArea = new JTextArea();
        mseTextArea.setFont(UIStyle.NORMAL_FONT);
        mseTextArea.setLineWrap(true);
        mseTextArea.setWrapStyleWord(true);
        mseTextArea.setText("MSE 病歷文字會依勾選標籤自動產生，也可以手動修改。\n");

        editorPanel.add(wrapArea("外觀與行為總體描述", overallTextArea));
        editorPanel.add(wrapArea("MSE 結構化病歷文字", mseTextArea));
        panel.add(editorPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(UIStyle.BACKGROUND);

        JButton loadTemplateButton = UIStyle.actionButton("載入");
        JButton printMseButton = UIStyle.actionButton("列印");

        loadTemplateButton.addActionListener(e -> loadNormalMseTemplate());
        printMseButton.addActionListener(e -> printMseReport());

        buttonPanel.add(loadTemplateButton);
        buttonPanel.add(printMseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane wrapArea(String title, JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        return scroll;
    }

    private JPanel buildMedicationTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.BACKGROUND);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UIStyle.BACKGROUND);

        JButton saveMedicationButton = UIStyle.actionButton("確定修改");
        JButton printMedicationButton = UIStyle.actionButton("列印用藥時間軸");

        saveMedicationButton.addActionListener(e -> saveMedicationChanges());
        printMedicationButton.addActionListener(e -> printMedicationTimeline());

        topPanel.add(saveMedicationButton);
        topPanel.add(printMedicationButton);
        root.add(topPanel, BorderLayout.NORTH);

        JPanel control = new JPanel(new GridLayout(6, 1, 8, 8));
        control.setBackground(UIStyle.BACKGROUND);
        control.setBorder(BorderFactory.createTitledBorder("藥物與症狀雙軸並陳時間軸控制區"));

        ssriSlider = createSlider("SSRI / 抗憂鬱劑劑量", 0, 4, 1);
        bzdSlider = createSlider("BZD / 安眠藥劑量", 0, 4, 1);
        phqSlider = createSlider("PHQ-9 憂鬱分數", 0, 27, 8);
        isiSlider = createSlider("ISI 失眠指數", 0, 28, 10);

        control.add(wrapSlider("SSRI / 抗憂鬱劑劑量", ssriSlider));
        control.add(wrapSlider("BZD / 安眠藥劑量", bzdSlider));
        control.add(wrapSlider("PHQ-9 憂鬱分數", phqSlider));
        control.add(wrapSlider("ISI 失眠指數", isiSlider));

        warningLabel = new JLabel(" ");
        warningLabel.setFont(UIStyle.SUBTITLE_FONT);
        warningLabel.setForeground(new Color(150, 60, 50));
        control.add(warningLabel);

        savedMedicationLabel = new JLabel("尚未儲存本次修改");
        savedMedicationLabel.setFont(UIStyle.NORMAL_FONT);
        savedMedicationLabel.setForeground(UIStyle.TEXT);
        control.add(savedMedicationLabel);

        chartPanel = new MedicationChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Dual-Axis Medication Timeline 模擬圖"));
        chartPanel.setPreferredSize(new Dimension(850, 420));

        JTextArea note = new JTextArea();
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setFont(UIStyle.NORMAL_FONT);
        note.setText("設計概念：橫軸為週次，左軸呈現藥物劑量，右軸呈現 PHQ-9 / ISI 分數。此頁為 UI 模擬，用於展示精神科後台如何把用藥變化與症狀分數放在同一個時間軸比較。\n\n警示邏輯示範：若 BZD 劑量偏高且 ISI 仍高，提醒醫師評估失眠原因與藥物依賴風險；若症狀分數下降，可作為療效追蹤參考。");
        note.setBackground(UIStyle.BACKGROUND);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, control, chartPanel);
        split.setResizeWeight(0.34);
        root.add(split, BorderLayout.CENTER);
        root.add(new JScrollPane(note), BorderLayout.SOUTH);
        updateMedicationChart();
        return root;
    }

    private JSlider createSlider(String name, int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(max <= 4 ? 1 : 5);
        slider.setBackground(UIStyle.BACKGROUND);
        slider.addChangeListener((ChangeEvent e) -> updateMedicationChart());
        return slider;
    }

    private JPanel wrapSlider(String title, JSlider slider) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    private void loadNormalMseTemplate() {
        for (JCheckBox box : tagBoxes) {
            String text = box.getText();
            box.setSelected("清晰".equals(text) || "整潔".equals(text));
        }

        overallTextArea.setText("外觀和行為觀察的總體描述：\n"
                + "個案衣著整潔，衛生狀況可，外觀約略符合實際年齡。互動過程合作，眼神接觸可，未見明顯敵意、戒備或怪異行為。\n\n"
                + "文化考量：衣著、眼神接觸與個人空間需放在個案文化背景下理解，避免以單一文化標準做出判斷。\n");

        mseTextArea.setText("MSE 標準正常範本：\n"
                + "意識清晰，人物、時間、地點定向感良好。外觀整潔，儀容衛生可，衣著與情境相符。行為合作，眼神接觸可。言談速度、音量與語調大致正常，未見明顯思考中斷或喃喃自語。肢體動作未見明顯遲緩、顫抖或靜坐不能。\n");
    }

    private void updateMseFromTags() {
        List<String> selected = new ArrayList<>();
        for (JCheckBox box : tagBoxes) {
            if (box.isSelected()) {
                selected.add(box.getText());
            }
        }

        if (selected.isEmpty()) {
            mseTextArea.setText("尚未選擇行為觀察標籤。\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MSE 行為觀察自動轉換文字：\n");
        sb.append("本次觀察標籤包含：").append(String.join("、", selected)).append("。\n");
        sb.append("臨床描述：個案於會談中呈現上述外觀、言談與行為特徵，建議結合主訴、病史、文化背景及當日情境進一步解釋。\n");
        mseTextArea.setText(sb.toString());
    }

    private void refreshAppointments() {
        try {
            List<AppointmentView> list = appointmentService.getAppointmentsByProfessional(professional.getId());
            String[] columns = {"日期", "個案姓名", "手機", "預約項目", "時段", "序號", "狀態"};
            tableModel.setDataVector(new Object[0][0], columns);
            patientCombo.removeAllItems();

            for (AppointmentView v : list) {
                tableModel.addRow(new Object[] {
                        DateUtil.formatDate(v.getAppointmentDate()),
                        v.getPatientName(),
                        v.getPhone(),
                        v.getAppointmentType(),
                        v.getSession() + " " + DateUtil.formatTime(v.getStartTime()),
                        String.format("%03d", v.getAppointmentNo()),
                        v.getStatus()
                });
                patientCombo.addItem(v.getPatientName());
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "讀取醫師預約名單失敗：" + e.getMessage());
        }
    }

    private void updateMedicationChart() {
        if (chartPanel == null || warningLabel == null) {
            return;
        }
        chartPanel.setValues(ssriSlider.getValue(), bzdSlider.getValue(), phqSlider.getValue(), isiSlider.getValue());

        if (bzdSlider.getValue() >= 3 && isiSlider.getValue() >= 15) {
            warningLabel.setText("警示：BZD 劑量偏高且失眠分數仍高，建議評估依賴風險與失眠原因。");
        } else if (phqSlider.getValue() >= 20) {
            warningLabel.setText("警示：PHQ-9 分數偏高，建議評估自傷風險與治療計畫。");
        } else {
            warningLabel.setText("目前無高風險用藥警示。可持續追蹤劑量與量表變化。");
        }
    }



    private void saveMedicationChanges() {
        savedSsri = ssriSlider.getValue();
        savedBzd = bzdSlider.getValue();
        savedPhq = phqSlider.getValue();
        savedIsi = isiSlider.getValue();

        String patientName = patientCombo.getSelectedItem() == null
                ? "未選擇個案"
                : patientCombo.getSelectedItem().toString();

        String savedText = "已儲存：" + patientName
                + "｜SSRI=" + savedSsri
                + "，BZD=" + savedBzd
                + "，PHQ-9=" + savedPhq
                + "，ISI=" + savedIsi;

        savedMedicationLabel.setText(savedText);

        JOptionPane.showMessageDialog(this,
                "用藥歷程與症狀時間軸已完成修改。\n"
                        + "個案：" + patientName + "\n"
                        + "SSRI / 抗憂鬱劑劑量：" + savedSsri + "\n"
                        + "BZD / 安眠藥劑量：" + savedBzd + "\n"
                        + "PHQ-9 憂鬱分數：" + savedPhq + "\n"
                        + "ISI 失眠指數：" + savedIsi + "\n\n"
                        + "提醒：目前為後台畫面暫存示範，若要永久保存，需再新增 MySQL 用藥紀錄資料表。");
    }

    private void printMseReport() {
        String patientName = patientCombo.getSelectedItem() == null
                ? "未選擇個案"
                : patientCombo.getSelectedItem().toString();

        StringBuilder sb = new StringBuilder();

        sb.append("醫師後台管理系統 - MSE 精神狀態檢查\n");
        sb.append("====================================\n\n");

        sb.append("個案姓名：").append(patientName).append("\n");
        sb.append("醫師：").append(professional.getName())
                .append("（").append(professional.getRole()).append("）\n\n");

        sb.append("【已勾選行為觀察標籤】\n");
        sb.append(getSelectedTagsText()).append("\n\n");

        sb.append("【外觀與行為總體描述】\n");
        sb.append(overallTextArea.getText()).append("\n\n");

        sb.append("【MSE 結構化病歷文字】\n");
        sb.append(mseTextArea.getText()).append("\n\n");

        sb.append("====================================\n");
        sb.append("擁抱身心醫療預約系統\n");

        printTextReport("列印 MSE 行為觀察", sb.toString());
    }

    private String getSelectedTagsText() {
        List<String> selected = new ArrayList<>();

        for (JCheckBox box : tagBoxes) {
            if (box.isSelected()) {
                selected.add(box.getText());
            }
        }

        if (selected.isEmpty()) {
            return "尚未勾選行為觀察標籤。";
        }

        return String.join("、", selected);
    }

    private void printTextReport(String title, String text) {
        JTextArea printArea = new JTextArea();
        printArea.setText(text);
        printArea.setFont(UIStyle.NORMAL_FONT);
        printArea.setLineWrap(true);
        printArea.setWrapStyleWord(true);

        try {
            boolean complete = printArea.print();

            if (complete) {
                JOptionPane.showMessageDialog(this, title + "完成");
            } else {
                JOptionPane.showMessageDialog(this, "已取消列印");
            }

        } catch (PrinterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "列印失敗：" + e.getMessage());
        }
    }

    private void printMedicationTimeline() {
        if (chartPanel == null) {
            JOptionPane.showMessageDialog(this, "目前沒有可列印的用藥時間軸");
            return;
        }

        printComponent(chartPanel, "用藥歷程與症狀時間軸");
    }

    private void printComponent(JPanel panel, String jobName) {
        if (panel.getWidth() <= 0 || panel.getHeight() <= 0) {
            JOptionPane.showMessageDialog(this, "畫面尚未載入完成，請稍後再列印。");
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobName);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            double scaleX = pageFormat.getImageableWidth() / panel.getWidth();
            double scaleY = pageFormat.getImageableHeight() / panel.getHeight();
            double scale = Math.min(scaleX, scaleY);

            g2.scale(scale, scale);
            panel.printAll(g2);

            return Printable.PAGE_EXISTS;
        });

        try {
            boolean doPrint = job.printDialog();

            if (doPrint) {
                job.print();
                JOptionPane.showMessageDialog(this, jobName + "列印完成");
            } else {
                JOptionPane.showMessageDialog(this, "已取消列印");
            }

        } catch (PrinterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "列印失敗：" + e.getMessage());
        }
    }

    private static class MedicationChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private int ssri = 1;
        private int bzd = 1;
        private int phq = 8;
        private int isi = 10;

        public MedicationChartPanel() {
            setBackground(Color.WHITE);
        }

        public void setValues(int ssri, int bzd, int phq, int isi) {
            this.ssri = ssri;
            this.bzd = bzd;
            this.phq = phq;
            this.isi = isi;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int left = 70;
            int right = 50;
            int top = 40;
            int bottom = 60;
            int plotW = w - left - right;
            int plotH = h - top - bottom;

            g2.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= 4; i++) {
                int y = top + i * plotH / 4;
                g2.drawLine(left, y, left + plotW, y);
            }

            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(left, top, left, top + plotH);
            g2.drawLine(left, top + plotH, left + plotW, top + plotH);
            g2.drawLine(left + plotW, top, left + plotW, top + plotH);

            String[] weeks = {"W1", "W2", "W3", "W4"};
            int[] ssriValues = {1, Math.max(1, ssri - 1), ssri, ssri};
            int[] bzdValues = {1, bzd, Math.max(0, bzd - 1), bzd};
            int[] phqValues = {18, 15, Math.max(0, phq + 3), phq};
            int[] isiValues = {20, 17, Math.max(0, isi + 2), isi};

            for (int i = 0; i < 4; i++) {
                int x = left + (i + 1) * plotW / 5;
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(weeks[i], x - 8, top + plotH + 25);

                int barBase = top + plotH;
                int ssriH = ssriValues[i] * plotH / 5;
                int bzdH = bzdValues[i] * plotH / 5;

                g2.setColor(new Color(120, 160, 190));
                g2.fillRect(x - 18, barBase - ssriH, 14, ssriH);
                g2.setColor(new Color(190, 140, 120));
                g2.fillRect(x + 4, barBase - bzdH, 14, bzdH);
            }

            drawLine(g2, phqValues, 27, left, top, plotW, plotH, new Color(80, 120, 80), "PHQ-9");
            drawLine(g2, isiValues, 28, left, top, plotW, plotH, new Color(150, 90, 150), "ISI");

            g2.setColor(Color.DARK_GRAY);
            g2.drawString("左軸：藥物劑量 0-4", 15, 25);
            g2.drawString("右軸：量表分數", w - 145, 25);
            g2.setColor(new Color(120, 160, 190));
            g2.fillRect(left + 20, h - 25, 12, 12);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("SSRI", left + 38, h - 14);
            g2.setColor(new Color(190, 140, 120));
            g2.fillRect(left + 100, h - 25, 12, 12);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("BZD", left + 118, h - 14);
        }

        private void drawLine(Graphics2D g2, int[] values, int max, int left, int top, int plotW, int plotH, Color color, String label) {
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(color);
            int prevX = -1;
            int prevY = -1;
            for (int i = 0; i < values.length; i++) {
                int x = left + (i + 1) * plotW / 5;
                int y = top + plotH - values[i] * plotH / max;
                g2.fillOval(x - 5, y - 5, 10, 10);
                if (prevX >= 0) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                prevX = x;
                prevY = y;
            }
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, left + plotW - fm.stringWidth(label) - 10, top + 20 + ("PHQ-9".equals(label) ? 0 : 18));
            g2.setStroke(new BasicStroke(1f));
        }
    }
}
