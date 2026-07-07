package util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

public class UIStyle {
    public static final Color PRIMARY = Color.decode("#ebb2a3");
    public static final Color BACKGROUND = Color.decode("#fcf7f6");
    public static final Color TEXT = new Color(70, 54, 50);
    public static final Color TABLE_HEADER = new Color(177, 126, 116);
    public static final Color CARD = Color.WHITE;

    public static final Font TITLE_FONT = new Font("微軟正黑體", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("微軟正黑體", Font.BOLD, 20);
    public static final Font NORMAL_FONT = new Font("微軟正黑體", Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font("微軟正黑體", Font.BOLD, 16);

    public static JButton menuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY.darker());
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    public static JButton actionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY.darker());
        button.setFocusPainted(false);
        return button;
    }

    public static void padding(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
    }
}
