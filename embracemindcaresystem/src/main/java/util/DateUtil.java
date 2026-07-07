package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text.trim(), DATE_FORMAT);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMAT);
    }

    public static String getChineseWeek(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return "一";
            case TUESDAY:
                return "二";
            case WEDNESDAY:
                return "三";
            case THURSDAY:
                return "四";
            case FRIDAY:
                return "五";
            case SATURDAY:
                return "六";
            case SUNDAY:
                return "日";
            default:
                return "";
        }
    }

    public static String formatRocDate(LocalDate date) {
        int rocYear = date.getYear() - 1911;
        return rocYear + "年 "
                + String.format("%02d", date.getMonthValue()) + "月 "
                + String.format("%02d", date.getDayOfMonth()) + "日"
                + "(星期" + getChineseWeek(date.getDayOfWeek()) + ")";
    }
}
