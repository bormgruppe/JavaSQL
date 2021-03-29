package ch.sama.sql.query.helper.pattern;

import ch.sama.sql.query.exception.BadParameterException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dates {
    private static SimpleDateFormat createSDF(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);

        return sdf;
    }

    public static SimpleDateFormat[] getGerDateFormat() {
        return new SimpleDateFormat[] { createSDF("dd.MM.yyyy") };
    }
    public static SimpleDateFormat[] getGerDateTimeFormat() {
        return new SimpleDateFormat[] { createSDF("dd.MM.yyyy HH:mm:ss"), createSDF("dd.MM.yyyy HH:mm") };
    }
    public static SimpleDateFormat[] getIsoDateFormat() {
        return new SimpleDateFormat[] { createSDF("yyyy-MM-dd") };
    }
    public static SimpleDateFormat[] getIsoDateTimeFormat() {
        return new SimpleDateFormat[] { createSDF("yyyy-MM-dd HH:mm:ss"), createSDF("yyyy-MM-dd HH:mm") };
    }

    private static Date parse(String s, SimpleDateFormat[] accepted) {
        for (SimpleDateFormat sdf : accepted) {
            Date d;

            try {
                d = sdf.parse(s);
            } catch (ParseException e) {
                d = null;
            }

            if (d != null) {
                return d;
            }
        }

        return null;
    }

    private static boolean isDate(String s, SimpleDateFormat[] accepted) {
        return s != null && parse(s, accepted) != null;
    }

    public static boolean isGerDate(String s) {
        return isDate(s, getGerDateFormat());
    }

    public static boolean isGerDateTime(String s) {
        return isDate(s, getGerDateTimeFormat());
    }

    public static boolean isIsoDate(String s) {
        return isDate(s, getIsoDateFormat());
    }

    public static boolean isIsoDateTime(String s) {
        return isDate(s, getIsoDateTimeFormat());
    }

    public static boolean isKnownDate(String s) {
        return isGerDate(s) || isGerDateTime(s) || isIsoDate(s) || isIsoDateTime(s);
    }

    private static SimpleDateFormat[] getFormat(String s) {
        if (isGerDateTime(s)) {
            return getGerDateTimeFormat();
        } else if (isGerDate(s)) {
            return getGerDateFormat();
        } else if (isIsoDateTime(s)) {
            return getIsoDateTimeFormat();
        } else if (isIsoDate(s)) {
            return getIsoDateFormat();
        } else {
            throw new BadParameterException("Bad date format: " + s);
        }
    }

    public static String convertDate(String s, SimpleDateFormat dst) {
        return dst.format(parse(s, getFormat(s)));
    }

    public static String toIsoDate(String s) {
        return convertDate(s, getIsoDateFormat()[0]);
    }

    public static String toIsoDateTime(String s) {
        return convertDate(s, getIsoDateTimeFormat()[0]);
    }

    public static String toGerDate(String s) {
        return convertDate(s, getGerDateFormat()[0]);
    }

    public static String toGerDateTime(String s) {
        return convertDate(s, getGerDateTimeFormat()[0]);
    }
}
