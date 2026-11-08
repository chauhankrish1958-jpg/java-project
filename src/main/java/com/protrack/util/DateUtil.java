package com.protrack.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Date utility helper for parsing, formatting and calculating overdue intervals.
 */
public class DateUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_PATTERN = "MMM dd, yyyy";
    private static final String DISPLAY_DATETIME_PATTERN = "MMM dd, yyyy HH:mm";

    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setLenient(false);
            java.util.Date parsed = sdf.parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            System.err.println("[DateUtil] Could not parse date: " + dateStr);
            return null;
        }
    }

    public static String formatDisplayDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_PATTERN);
        return sdf.format(date);
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATETIME_PATTERN);
        return sdf.format(timestamp);
    }

    public static String formatInputDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }

    public static boolean isOverdue(Date dueDate, String status) {
        if (dueDate == null || "COMPLETED".equalsIgnoreCase(status)) {
            return false;
        }
        LocalDate due = dueDate.toLocalDate();
        LocalDate today = LocalDate.now();
        return due.isBefore(today);
    }

    public static boolean isDueToday(Date dueDate, String status) {
        if (dueDate == null || "COMPLETED".equalsIgnoreCase(status)) {
            return false;
        }
        LocalDate due = dueDate.toLocalDate();
        LocalDate today = LocalDate.now();
        return due.isEqual(today);
    }

    public static long getDaysRemaining(Date dueDate) {
        if (dueDate == null) return 0;
        LocalDate due = dueDate.toLocalDate();
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, due);
    }

    public static String getRelativeDueStatus(Date dueDate, String status) {
        if ("COMPLETED".equalsIgnoreCase(status)) {
            return "Completed";
        }
        if (dueDate == null) {
            return "No Due Date";
        }
        LocalDate due = dueDate.toLocalDate();
        LocalDate today = LocalDate.now();
        long diff = ChronoUnit.DAYS.between(today, due);

        if (diff < 0) {
            return "Overdue by " + Math.abs(diff) + (Math.abs(diff) == 1 ? " day" : " days");
        } else if (diff == 0) {
            return "Due today";
        } else if (diff == 1) {
            return "Due tomorrow";
        } else {
            return diff + " days left";
        }
    }
}
