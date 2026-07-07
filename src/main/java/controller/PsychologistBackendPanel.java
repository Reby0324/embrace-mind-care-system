package controller;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import exception.AppException;
import model.AppointmentView;
import model.Professional;
import service.AppointmentService;
import service.impl.AppointmentServiceImpl;
import util.DateUtil;
import util.UIStyle;

public class PsychologistBackendPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final Professional professional;

    private DefaultTableModel tableModel;
    private JTable appointmentTable;
    private JComboBox<PatientComboItem> patientCombo;
    private List<AppointmentView> appointmentList = new ArrayList<>();
    private boolean syncingSelection = false;
    private JTextArea referralTextArea;
    private JTextArea reportTextArea;
    private JTextField vciField;
    private JTextField priField;
    private JTextField wmiField;
    private JTextField psiField;
    private JTextField iqField;
    private JTextField depressionTField;
    private JTextField paranoiaTField;
    private JTextField anxietyTField;
    private BellCurvePanel bellCurvePanel;
    private RadarChartPanel radarChartPanel;

    public PsychologistBackendPanel(Professional professional) {
        this.professional = professional;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.BACKGROUND);
        UIStyle.padding(this, 15, 15, 15, 15);
        initUI();
        refreshAppointments();
        updateCharts();
    }

    private void initUI() {
        JLabel title = new JLabel("心理師後台管理系統｜" + professional.getName() + "（" + professional.getRole() + "）");
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftWorkspace(), buildRightWorkspace());
        split.setResizeWeight(0.42);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildLeftWorkspace() {
        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.setBackground(UIStyle.BACKGROUND);
        left.setBorder(BorderFactory.createTitledBorder("左欄：資料與素材庫"));

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBackground(UIStyle.BACKGROUND);
        patientCombo = new JComboBox<>();
        patientCombo.setFont(UIStyle.NORMAL_FONT);
        patientCombo.addActionListener(e -> syncTableSelectionFromCombo());

        JButton refreshButton = UIStyle.actionButton("重新整理");
        refreshButton.addActionListener(e -> refreshAppointments());
        top.add(new JLabel("目前個案："), BorderLayout.WEST);
        top.add(patientCombo, BorderLayout.CENTER);
        top.add(refreshButton, BorderLayout.EAST);
        left.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.NORMAL_FONT);
        tabs.addTab("預約名單", buildAppointmentTablePanel());
        tabs.addTab("個案資料 / 轉介主訴", buildReferralPanel());
        tabs.addTab("測驗原始分數", buildScorePanel());
        tabs.addTab("結果視覺化", buildDashboardPanel());
        left.add(tabs, BorderLayout.CENTER);
        return left;
    }

    private JScrollPane buildAppointmentTablePanel() {
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

        // 點選表格某一位個案時，同步到「目前個案」下拉選單
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || syncingSelection) {
                return;
            }

            int row = appointmentTable.getSelectedRow();
            if (row >= 0 && row < patientCombo.getItemCount()) {
                syncingSelection = true;
                patientCombo.setSelectedIndex(row);
                syncingSelection = false;
            }
        });

        return new JScrollPane(appointmentTable);
    }


    private JPanel buildReferralPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UIStyle.BACKGROUND);

        referralTextArea = new JTextArea();
        referralTextArea.setFont(UIStyle.NORMAL_FONT);
        referralTextArea.setLineWrap(true);
        referralTextArea.setWrapStyleWord(true);
        referralTextArea.setText("個案基本資料與轉介主訴：\n"
                + "例如：本次衡鑑目的為確認是否有 ADHD、情緒困擾、失智症鑑別或人格特質評估需求。\n\n"
                + "病史摘要：\n"
                + "請輸入個案主訴、重要病史、用藥狀況、家庭 / 學校 / 職場功能。\n");
        panel.add(new JScrollPane(referralTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildScorePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UIStyle.BACKGROUND);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBackground(UIStyle.BACKGROUND);
        form.setBorder(BorderFactory.createTitledBorder("心理測驗原始數據面板"));

        vciField = createScoreField("105");
        priField = createScoreField("98");
        wmiField = createScoreField("92");
        psiField = createScoreField("88");
        iqField = createScoreField("96");
        depressionTField = createScoreField("68");
        paranoiaTField = createScoreField("55");
        anxietyTField = createScoreField("62");

        form.add(new JLabel("VCI 語文理解："));
        form.add(vciField);
        form.add(new JLabel("PRI 知覺推理："));
        form.add(priField);
        form.add(new JLabel("WMI 工作記憶："));
        form.add(wmiField);
        form.add(new JLabel("PSI 處理速度："));
        form.add(psiField);
        form.add(new JLabel("FSIQ 全量表智商："));
        form.add(iqField);
        form.add(new JLabel("MMPI / PAI 憂鬱 T 分："));
        form.add(depressionTField);
        form.add(new JLabel("偏執 T 分 / 焦慮 T 分："));

        JPanel pair = new JPanel(new GridLayout(1, 2, 5, 5));
        pair.setBackground(UIStyle.BACKGROUND);
        pair.add(paranoiaTField);
        pair.add(anxietyTField);
        form.add(pair);

        JTextArea hint = new JTextArea();
        hint.setEditable(false);
        hint.setOpaque(false);
        hint.setLineWrap(true);
        hint.setWrapStyleWord(true);
        hint.setFont(UIStyle.NORMAL_FONT);
        hint.setText("輸入分數後，常態分配曲線與雷達圖會即時更新。T 分大於 65 會被視為突破臨床切截點的示範指標。\n");

        panel.add(form, BorderLayout.NORTH);
        panel.add(hint, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createScoreField(String value) {
        JTextField field = new JTextField(value);
        field.setFont(UIStyle.NORMAL_FONT);
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateCharts(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateCharts(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateCharts(); }
        });
        return field;
    }

    private JPanel buildDashboardPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIStyle.BACKGROUND);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 18));
        panel.setBackground(UIStyle.BACKGROUND);

        bellCurvePanel = new BellCurvePanel();
        bellCurvePanel.setPreferredSize(new Dimension(520, 260));
        bellCurvePanel.setBorder(BorderFactory.createTitledBorder("常態分配曲線圖（Bell Curve）"));

        radarChartPanel = new RadarChartPanel();
        radarChartPanel.setPreferredSize(new Dimension(520, 300));
        radarChartPanel.setBorder(BorderFactory.createTitledBorder("多軸雷達圖（Radar Chart）"));

        panel.add(bellCurvePanel);
        panel.add(radarChartPanel);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        outer.add(scrollPane, BorderLayout.CENTER);
        return outer;
    }


    private JPanel buildRightWorkspace() {
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(UIStyle.BACKGROUND);
        right.setBorder(BorderFactory.createTitledBorder("右欄：結構化報告編輯器"));

        JPanel buttons = new JPanel();
        buttons.setBackground(UIStyle.BACKGROUND);
        JButton draftButton = UIStyle.actionButton("產生報告草稿");
        JButton exportButton = UIStyle.actionButton("匯出 TXT 報告");
        JButton printButton = UIStyle.actionButton("列印 / 另存 PDF");
        buttons.add(draftButton);
        buttons.add(exportButton);
        buttons.add(printButton);
        right.add(buttons, BorderLayout.NORTH);

        reportTextArea = new JTextArea();
        reportTextArea.setFont(UIStyle.NORMAL_FONT);
        reportTextArea.setLineWrap(true);
        reportTextArea.setWrapStyleWord(true);
        reportTextArea.setText("【心理衡鑑報告】\n\n"
                + "一、轉介目的\n\n"
                + "二、行為觀察\n\n"
                + "三、測驗結果\n\n"
                + "四、整合分析與臨床印象\n\n"
                + "五、建議\n");
        right.add(new JScrollPane(reportTextArea), BorderLayout.CENTER);

        draftButton.addActionListener(e -> generateDraftReport());
        exportButton.addActionListener(e -> exportReportTxt());
        printButton.addActionListener(e -> printReport());
        return right;
    }

    private void refreshAppointments() {
        try {
            List<AppointmentView> list = appointmentService.getAppointmentsByProfessional(professional.getId());
            appointmentList = list;

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

                // 將預約時填寫的姓名放進目前個案下拉選單
                patientCombo.addItem(new PatientComboItem(v));
            }

            if (patientCombo.getItemCount() > 0) {
                patientCombo.setSelectedIndex(0);
                appointmentTable.setRowSelectionInterval(0, 0);
            }

        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "讀取心理師預約名單失敗：" + e.getMessage());
        }
    }

    private void syncTableSelectionFromCombo() {
        if (syncingSelection || patientCombo == null || appointmentTable == null) {
            return;
        }

        int index = patientCombo.getSelectedIndex();

        if (index >= 0 && index < appointmentTable.getRowCount()) {
            syncingSelection = true;
            appointmentTable.setRowSelectionInterval(index, index);
            appointmentTable.scrollRectToVisible(appointmentTable.getCellRect(index, 0, true));
            syncingSelection = false;
        }
    }


    private void generateDraftReport() {
        PatientComboItem selectedPatient = (PatientComboItem) patientCombo.getSelectedItem();
        String patientName = selectedPatient == null ? "未選擇個案" : selectedPatient.getAppointment().getPatientName();
        int iq = readInt(iqField, 96);
        int vci = readInt(vciField, 105);
        int pri = readInt(priField, 98);
        int wmi = readInt(wmiField, 92);
        int psi = readInt(psiField, 88);
        int dep = readInt(depressionTField, 68);
        int par = readInt(paranoiaTField, 55);
        int anx = readInt(anxietyTField, 62);

        StringBuilder sb = new StringBuilder();
        sb.append("【心理衡鑑報告】\n\n");
        sb.append("個案姓名：").append(patientName).append("\n");
        sb.append("心理師：").append(professional.getName()).append("（").append(professional.getRole()).append("）\n\n");
        sb.append("一、轉介目的\n").append(referralTextArea.getText()).append("\n\n");
        sb.append("二、行為觀察\n");
        sb.append("個案於衡鑑過程中可配合指令，建議心理師依實際觀察補充注意力、挫折忍受度、眼神接觸與作答動機。\n\n");
        sb.append("三、測驗結果\n");
        sb.append("FSIQ = ").append(iq).append("，VCI = ").append(vci).append("，PRI = ").append(pri)
                .append("，WMI = ").append(wmi).append("，PSI = ").append(psi).append("。\n");
        sb.append("人格 / 症狀量表：憂鬱 T = ").append(dep).append("，偏執 T = ").append(par)
                .append("，焦慮 T = ").append(anx).append("。\n");
        sb.append("臨床切截點提示：").append(buildCutoffText(dep, par, anx)).append("\n\n");
        sb.append("四、整合分析與臨床印象\n");
        sb.append("請心理師整合轉介主訴、病史、行為觀察、測驗效度與各分測驗差異，撰寫個案的功能優勢與困難。\n\n");
        sb.append("五、建議\n");
        sb.append("建議依衡鑑目的提供後續治療、學習 / 職場調整、精神科追蹤或心理治療建議。\n");

        reportTextArea.setText(sb.toString());
    }

    private String buildCutoffText(int dep, int par, int anx) {
        StringBuilder sb = new StringBuilder();
        if (dep > 65) {
            sb.append("憂鬱量表高於臨床切截點；");
        }
        if (par > 65) {
            sb.append("偏執量表高於臨床切截點；");
        }
        if (anx > 65) {
            sb.append("焦慮量表高於臨床切截點；");
        }
        if (sb.length() == 0) {
            return "目前輸入分數未超過 T 分 65 的示範切截點。";
        }
        return sb.toString();
    }

    private void exportReportTxt() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("匯出心理衡鑑報告");
        chooser.setSelectedFile(new File("心理衡鑑報告.txt"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }

        try {
            Files.write(file.toPath(), reportTextArea.getText().getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(this, "匯出完成：\n" + file.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "匯出失敗：" + e.getMessage());
        }
    }

    private void printReport() {
        try {
            boolean complete = reportTextArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "列印完成");
            } else {
                JOptionPane.showMessageDialog(this, "已取消列印");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "列印失敗：" + e.getMessage());
        }
    }

    private void updateCharts() {
        if (bellCurvePanel != null) {
            bellCurvePanel.setScore(readInt(iqField, 96));
        }
        if (radarChartPanel != null) {
            radarChartPanel.setScores(
                    readInt(vciField, 105),
                    readInt(priField, 98),
                    readInt(wmiField, 92),
                    readInt(psiField, 88),
                    readInt(depressionTField, 68),
                    readInt(paranoiaTField, 55),
                    readInt(anxietyTField, 62));
        }
    }

    private int readInt(JTextField field, int defaultValue) {
        if (field == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static class BellCurvePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private int score = 96;

        public BellCurvePanel() {
            setBackground(Color.WHITE);
        }

        public void setScore(int score) {
            this.score = score;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets insets = getInsets();

            int w = getWidth();
            int h = getHeight();

            int left = insets.left + 55;
            int right = insets.right + 45;
            int top = insets.top + 45;
            int baseY = h - insets.bottom - 40;

            if (baseY <= top + 40) {
                return;
            }

            int plotW = w - left - right;
            int plotH = baseY - top;

            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(left, baseY, left + plotW, baseY);

            int prevX = left;
            int prevY = baseY;

            g2.setColor(new Color(110, 150, 180));
            g2.setStroke(new BasicStroke(3f));

            for (int x = 0; x <= plotW; x++) {
                double z = (x - plotW / 2.0) / (plotW / 6.0);
                double yValue = Math.exp(-0.5 * z * z);

                int y = baseY - (int) (yValue * plotH);
                int actualX = left + x;

                if (x > 0) {
                    g2.drawLine(prevX, prevY, actualX, y);
                }

                prevX = actualX;
                prevY = y;
            }

            int clamped = Math.max(55, Math.min(145, score));
            int scoreX = left + (clamped - 55) * plotW / 90;

            double scoreZ = ((double) (scoreX - left) - plotW / 2.0) / (plotW / 6.0);
            double scoreYValue = Math.exp(-0.5 * scoreZ * scoreZ);
            int scoreY = baseY - (int) (scoreYValue * plotH);

            g2.setColor(new Color(190, 90, 90));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(scoreX, scoreY, scoreX, baseY);
            g2.fillOval(scoreX - 5, scoreY - 5, 10, 10);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(UIStyle.NORMAL_FONT);
            g2.drawString("IQ / 標準分數：" + score, left, top - 15);

            g2.drawString("顯著落後", left, baseY + 25);
            g2.drawString("平均", left + plotW / 2 - 15, baseY + 25);
            g2.drawString("極優秀", left + plotW - 45, baseY + 25);
        }

    }

    private static class RadarChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final String[] labels = {"VCI", "PRI", "WMI", "PSI", "憂鬱", "偏執", "焦慮"};
        private int[] scores = {105, 98, 92, 88, 68, 55, 62};

        public RadarChartPanel() {
            setBackground(Color.WHITE);
        }

        public void setScores(int vci, int pri, int wmi, int psi, int dep, int par, int anx) {
            scores = new int[] {vci, pri, wmi, psi, dep, par, anx};
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets insets = getInsets();

            int w = getWidth();
            int h = getHeight();

            int left = insets.left + 55;
            int right = insets.right + 55;
            int top = insets.top + 55;
            int bottom = insets.bottom + 45;

            int chartW = w - left - right;
            int chartH = h - top - bottom;

            if (chartW <= 80 || chartH <= 80) {
                return;
            }

            int cx = left + chartW / 2;
            int cy = top + chartH / 2 + 10;
            int radius = Math.min(chartW, chartH) / 2 - 35;

            if (radius <= 30) {
                return;
            }

            g2.setFont(UIStyle.NORMAL_FONT);

            g2.setColor(new Color(190, 90, 90));
            g2.drawString("T 分 > 65：臨床切截點示範", left, top - 25);

            g2.setColor(Color.LIGHT_GRAY);
            for (int r = 1; r <= 4; r++) {
                drawPolygon(g2, cx, cy, radius * r / 4, null, false);
            }

            int n = labels.length;
            int[] xs = new int[n];
            int[] ys = new int[n];

            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2 + i * 2 * Math.PI / n;

                int axisX = cx + (int) (Math.cos(angle) * radius);
                int axisY = cy + (int) (Math.sin(angle) * radius);

                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(cx, cy, axisX, axisY);

                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(Color.DARK_GRAY);

                int labelX = cx + (int) (Math.cos(angle) * (radius + 32));
                int labelY = cy + (int) (Math.sin(angle) * (radius + 32));

                g2.drawString(
                        labels[i],
                        labelX - fm.stringWidth(labels[i]) / 2,
                        labelY + fm.getAscent() / 2
                );

                int normalized = Math.max(40, Math.min(140, scores[i]));
                int valueR = (normalized - 40) * radius / 100;

                xs[i] = cx + (int) (Math.cos(angle) * valueR);
                ys[i] = cy + (int) (Math.sin(angle) * valueR);
            }

            g2.setColor(new Color(120, 140, 190, 90));
            g2.fillPolygon(xs, ys, n);

            g2.setColor(new Color(80, 100, 160));
            g2.setStroke(new BasicStroke(2f));
            g2.drawPolygon(xs, ys, n);
        }


        private void drawPolygon(Graphics2D g2, int cx, int cy, int radius, Color color, boolean fill) {
            int n = labels.length;
            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2 + i * 2 * Math.PI / n;
                xs[i] = cx + (int) (Math.cos(angle) * radius);
                ys[i] = cy + (int) (Math.sin(angle) * radius);
            }
            if (color != null) {
                g2.setColor(color);
            }
            if (fill) {
                g2.fillPolygon(xs, ys, n);
            } else {
                g2.drawPolygon(xs, ys, n);
            }
        }
    }

    private static class PatientComboItem {
        private final AppointmentView appointment;

        public PatientComboItem(AppointmentView appointment) {
            this.appointment = appointment;
        }

        public AppointmentView getAppointment() {
            return appointment;
        }

        @Override
        public String toString() {
            return appointment.getPatientName()
                    + "｜"
                    + DateUtil.formatDate(appointment.getAppointmentDate())
                    + "｜"
                    + String.format("%03d", appointment.getAppointmentNo());
        }
    }

}
