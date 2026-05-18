package cn.nyc1.myapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class FormatUtils {
    private FormatUtils() {
    }

    public static String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    public static String weekDay(Integer weekDay) {
        if (weekDay == null) {
            return "周-";
        }
        switch (weekDay) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "周-";
        }
    }

    public static String courseTime(Integer weekDay, String section, String startTime, String endTime) {
        return weekDay(weekDay) + " " + safe(section) + " · " + safe(startTime) + "-" + safe(endTime);
    }

    public static String statusText(String status) {
        if ("ACTIVE".equals(status)) {
            return "可签到";
        }
        if ("NOT_STARTED".equals(status)) {
            return "未开始";
        }
        if ("ENDED".equals(status)) {
            return "已结束";
        }
        if ("SIGNED".equals(status)) {
            return "已签到";
        }
        if ("LATE".equals(status)) {
            return "迟到";
        }
        if ("ABSENT".equals(status)) {
            return "缺勤";
        }
        if ("EXCEPTION".equals(status)) {
            return "异常";
        }
        return safe(status);
    }

    public static String nowIsoMinutes() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)
                .format(Calendar.getInstance().getTime());
    }

    public static String plusMinutesIso(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)
                .format(calendar.getTime());
    }
}
