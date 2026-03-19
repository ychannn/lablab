package org.ychan.lablab.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 解析前端传来的日期时间字符串。
 * 支持 "yyyy-MM-dd HH:mm:ss" 或 "yyyy-MM-ddTHH:mm"（datetime-local 无秒时补 :00）
 */
public final class DateTimeParseUtil {

    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 解析为 LocalDateTime。前端 datetime-local 可能为 "yyyy-MM-ddTHH:mm"（16 位），需补秒。
     * @param value 如 "2024-01-15T08:00" 或 "2024-01-15 08:00:00"
     * @return 解析结果，失败返回 null
     */
    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) return null;
        String s = value.trim().replace(' ', 'T');
        if (s.length() == 16) {
            s = s + ":00";
        } else if (s.length() > 19) {
            s = s.substring(0, 19);
        }
        try {
            return LocalDateTime.parse(s, ISO_LOCAL);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
