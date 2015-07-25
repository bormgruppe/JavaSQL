package ch.sama.sql.query.helper;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dialect.tsql.TSqlMerger;
import ch.sama.sql.dialect.tsql.TSqlQueryFactory;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.dialect.tsql.TSqlValueFactory;
import ch.sama.sql.dialect.tsql.type.*;
import ch.sama.sql.query.exception.BadParameterException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ObjectValueTest {
    private static final TSqlQueryFactory sql = new TSqlQueryFactory();
    private static final TSqlValueFactory value = sql.value();

    @Test
    public void guessNull() {
        assertEquals("NULL", value.object(null).getValue());
    }

    @Test
    public void guessBit() {
        assertEquals("1", value.object(true).getValue());
        assertEquals("0", value.object(false).getValue());
    }

    @Test
    public void guessShort() {
        assertEquals("1", value.object((short) 1).getValue());
    }

    @Test
    public void guessInt() {
        assertEquals("1", value.object(1).getValue());
    }

    @Test
    public void guessLong() {
        assertEquals("1", value.object((long) 1).getValue());
    }

    @Test
    public void guessFloat() {
        assertEquals("1.23", value.object(1.23f).getValue());
    }

    @Test
    public void guessDouble() {
        assertEquals("1.23", value.object(1.23).getValue());
    }

    @Test
    public void guessDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();

        assertEquals("CONVERT(datetime, '" + sdf.format(d) + "', 21)", value.object(d).getValue());
    }

    @Test
    public void guessNullString() {
        assertEquals("NULL", value.object("NuLl").getValue());
    }

    @Test
    public void guessIntString() {
        assertEquals("1", value.object("1").getValue());
    }

    @Test
    public void guessFloatString() {
        assertEquals("1.23", value.object("1.23").getValue());
    }

    @Test
    public void guessNormDateString() {
        assertEquals("CONVERT(datetime, '2015-07-25 00:00:00', 21)", value.object("25.07.2015").getValue());
    }

    @Test
    public void guessNormDateStringIncomplete() {
        assertEquals("CONVERT(datetime, '2015-07-01 00:00:00', 21)", value.object("1.7.2015").getValue());
    }

    @Test
    public void guessNormDateTimeString() {
        assertEquals("CONVERT(datetime, '2015-07-25 16:29:00', 21)", value.object("25.07.2015 16:29:00").getValue());
    }

    @Test
    public void guessNormDateTimeStringIncomplete() {
        assertEquals("CONVERT(datetime, '2015-07-01 16:30:00', 21)", value.object("1.7.2015 16:30:00").getValue());
    }

    @Test
    public void guessIsoDateString() {
        assertEquals("CONVERT(datetime, '2015-07-25 00:00:00', 21)", value.object("2015-07-25").getValue());
    }

    @Test
    public void guessIsoDateTimeString() {
        assertEquals("CONVERT(datetime, '2015-07-25 16:31:00', 21)", value.object("2015-07-25 16:31:00").getValue());
    }

    @Test
    public void guessString() {
        assertEquals("'Hello'", value.object("Hello").getValue());
    }

    @Test (expected = BadParameterException.class)
    public void wrongGuess() {
        value.object(new Object());
    }
}
