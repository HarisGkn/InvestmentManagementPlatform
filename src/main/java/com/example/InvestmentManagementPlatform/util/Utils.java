package com.example.InvestmentManagementPlatform.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static Optional<BigDecimal> calculatePercentageChange(BigDecimal initialValue, BigDecimal finalValue) {
        if (initialValue == null || finalValue == null || initialValue.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }
        BigDecimal change = finalValue.subtract(initialValue)
                .divide(initialValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return Optional.of(change);
    }

    public static String formatDate(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    public static LocalDate parseDate(String dateStr, String pattern) {
        if (isNullOrEmpty(dateStr) || pattern == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }
}
