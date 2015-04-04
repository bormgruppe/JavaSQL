package ch.sama.sql.dialect.tsql;

import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.standard.ValueFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TSqlValueFactory extends ValueFactory {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final TSqlQueryRenderer renderer = new TSqlQueryRenderer();

    public TSqlValueFactory() {
        super(renderer);
    }

    public Value table(String table) {
        return table(new Table(table));
    }

    public Value table(Table table) {
        return new Value(table, renderer.render(table) + ".*");
    }

    public Value date(Date date) {
        return new Value(date, "CONVERT(datetime, '" + DATE_FORMAT.format(date) + "', 21)");
    }

    @Override
    public Value string(String s) {
        return new Value(s, "'" + s.replace("'", "''") + "'");
    }
}
