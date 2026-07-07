package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Schedule;
import model.ServiceType;
import util.UIStyle;

public class ServiceCategoryPanel extends AbstractSchedulePanel {
    private static final long serialVersionUID = 1L;

    private JComboBox<ServiceType> serviceComboBox;

    public ServiceCategoryPanel() {
        super();
        initTopPanel();
        initTable("依服務類別預約：服務類別取代一般科別，包含精神科門診、心理諮商 / 心理治療、臨床心理衡鑑。");
        loadServiceTypes();
        refresh();
    }

    private void initTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(UIStyle.BACKGROUND);
        top.setBorder(BorderFactory.createTitledBorder("依服務類別預約"));

        JPanel form = new JPanel(new GridLayout(1, 3, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);
        JLabel label = new JLabel("服務類別：");
        label.setFont(UIStyle.SUBTITLE_FONT);
        serviceComboBox = new JComboBox<>();
        serviceComboBox.setFont(UIStyle.NORMAL_FONT);
        JButton searchButton = UIStyle.actionButton("查詢");
        form.add(label);
        form.add(serviceComboBox);
        form.add(searchButton);
        top.add(form, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        searchButton.addActionListener(e -> refresh());
        serviceComboBox.addActionListener(e -> refresh());
    }

    private void loadServiceTypes() {
        serviceComboBox.removeAllItems();
        for (ServiceType type : appointmentService.getServiceTypes()) {
            serviceComboBox.addItem(type);
        }
    }

    @Override
    protected void refresh() {
        ServiceType selected = (ServiceType) serviceComboBox.getSelectedItem();
        if (selected == null) {
            renderSchedules(java.util.Collections.emptyList());
            return;
        }
        List<Schedule> data = appointmentService.getSchedulesByServiceType(selected.getId());
        renderSchedules(data);
    }
}
