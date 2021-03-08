package com.hummer.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Range;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {
    private BigDecimalUtil() {

    }

    public static boolean between(double target, double start, double end) {
        return between(BigDecimal.valueOf(target)
                , Range.openClosed(BigDecimal.valueOf(start), BigDecimal.valueOf(end)));
    }

    public static BigDecimal subtractOf2HalfUp(BigDecimal left, BigDecimal right) {
        return left.subtract(right).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal subtractOf2HalfUp(BigDecimal left, BigDecimal right, BigDecimal right1) {
        return left.subtract(right).subtract(right1).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal addOf2HalfUp(BigDecimal left, BigDecimal right) {
        return left.add(right).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal max(BigDecimal left, BigDecimal right) {
        return greaterThanOrEqual(left, right) ? left : right;
    }

    public static BigDecimal min(BigDecimal left, BigDecimal right) {
        return lessThanOrEqual(left, right) ? left : right;
    }

    public static boolean between(BigDecimal val, Range<BigDecimal> range) {
        return range.contains(val);
    }

    public static boolean greaterThan(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) > 0;
    }

    public static boolean greaterThanOrEqual(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) >= 0;
    }

    public static boolean lessThan(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) < 0;
    }

    public static boolean lessThanOrEqual(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) <= 0;
    }

    public static boolean equal(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) == 0;
    }

    public static boolean equal0(BigDecimal left) {
        return left.compareTo(BigDecimal.valueOf(0.00)) == 0 || left.compareTo(BigDecimal.valueOf(0)) == 0;
    }

    public static BigDecimal valueOf(String val) {
        if (Strings.isNullOrEmpty(val)) {
            throw new IllegalArgumentException("value of BigDecimal failed,because val is null");
        }
        if (val.contains(".")) {
            return BigDecimal.valueOf(Double.parseDouble(val));
        } else {
            return BigDecimal.valueOf(Long.parseLong(val));
        }
    }

    public static boolean greaterThan(double left, double right) {
        return BigDecimal.valueOf(left).compareTo(BigDecimal.valueOf(right)) > 0;
    }

    public static boolean greaterThanOrEqual(double left, double right) {
        return BigDecimal.valueOf(left).compareTo(BigDecimal.valueOf(right)) >= 0;
    }

    public static boolean lessThan(double left, double right) {
        return BigDecimal.valueOf(left).compareTo(BigDecimal.valueOf(right)) < 0;
    }

    public static boolean lessThanOrEqual(double left, double right) {
        return BigDecimal.valueOf(left).compareTo(BigDecimal.valueOf(right)) <= 0;
    }

    public static boolean equal(double left, double right) {
        return BigDecimal.valueOf(left).compareTo(BigDecimal.valueOf(right)) == 0;
    }
}
