package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Professional;
import model.Schedule;
import util.UIStyle;

public class ProfessionalReservationPanel extends AbstractSchedulePanel {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> roleComboBox;
    private JComboBox<Professional> professionalComboBox;

    public ProfessionalReservationPanel() {
        super();
        initTopPanel();
        initTable("依治療師預約：可依精神科醫師、臨床心理師、諮商心理師、職能治療師篩選。雙擊資料列也可以預約。");
        loadProfessionals();
        refresh();
    }

    private void initTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(UIStyle.BACKGROUND);
        top.setBorder(BorderFactory.createTitledBorder("依治療師預約"));

        JPanel form = new JPanel(new GridLayout(1, 5, 10, 10));
        form.setBackground(UIStyle.BACKGROUND);
        roleComboBox = new JComboBox<>(new String[] {"全部", "精神科醫師", "臨床心理師", "諮商心理師", "職能治療師"});
        roleComboBox.setFont(UIStyle.NORMAL_FONT);
        professionalComboBox = new JComboBox<>();
        professionalComboBox.setFont(UIStyle.NORMAL_FONT);
        JButton searchButton = UIStyle.actionButton("查詢");

        form.add(new JLabel("指定治療師："));
        form.add(roleComboBox);
        form.add(new JLabel("治療師："));
        form.add(professionalComboBox);
        form.add(searchButton);
        top.add(form, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        roleComboBox.addActionListener(e -> loadProfessionals());
        searchButton.addActionListener(e -> refresh());
        professionalComboBox.addActionListener(e -> refresh());
    }

    private void loadProfessionals() {
        if (professionalComboBox == null || roleComboBox == null) {
            return;
        }
        Object selectedRoleObject = roleComboBox.getSelectedItem();
        String role = selectedRoleObject == null ? "全部" : selectedRoleObject.toString();
        professionalComboBox.removeAllItems();
        professionalComboBox.addItem(new Professional().setId(0).setName("全部治療師").setRole(role));
        for (Professional p : appointmentService.getProfessionalsByRole(role)) {
            professionalComboBox.addItem(p);
        }
    }

    @Override
    protected void refresh() {
        Professional selected = (Professional) professionalComboBox.getSelectedItem();
        if (selected == null) {
            renderSchedules(java.util.Collections.emptyList());
            return;
        }
        List<Schedule> data;
        if (selected.getId() == 0) {
            data = new java.util.ArrayList<>();
            for (Professional p : appointmentService.getProfessionalsByRole(String.valueOf(roleComboBox.getSelectedItem()))) {
                data.addAll(appointmentService.getSchedulesByProfessional(p.getId()));
            }
        } else {
            data = appointmentService.getSchedulesByProfessional(selected.getId());
        }
        renderSchedules(data);
    }
}
