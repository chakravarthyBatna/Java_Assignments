package com.wavemaker.employee.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static int calculateTotalDaysExcludingWeekends(Date fromDate, Date toDate) {
        LocalDate start = convertToLocalDate(fromDate);
        LocalDate end = convertToLocalDate(toDate);

        int totalDays = 0;
        LocalDate currentDate = start;

        while (!currentDate.isAfter(end)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                totalDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalDays;
    }

    private static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}