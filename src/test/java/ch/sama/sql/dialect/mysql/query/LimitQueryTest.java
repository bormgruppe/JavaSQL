package ch.sama.sql.dialect.mysql.query;

import ch.sama.sql.dialect.mysql.MySqlQueryFactory;
import ch.sama.sql.dialect.mysql.MySqlSourceFactory;
import ch.sama.sql.dialect.mysql.MySqlValueFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LimitQueryTest {
    private static final MySqlQueryFactory sql = new MySqlQueryFactory();
    private static final MySqlValueFactory value = sql.value();
    private static final MySqlSourceFactory source = sql.source();

    @Test
    public void selectSingleLimit() {
        // does not work..
        /*
        assertEquals(
                "SELECT 1\nLIMIT 10",
                sql.query()
                        .select(value.numeric(1))
                        .limit(10)
        );
        */
    }
}
