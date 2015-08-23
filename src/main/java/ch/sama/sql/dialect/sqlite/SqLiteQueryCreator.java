package ch.sama.sql.dialect.sqlite;

import ch.sama.sql.query.base.IQueryRenderer;
import ch.sama.sql.query.base.QueryCreator;

public class SqLiteQueryCreator extends QueryCreator {
    private static SqLiteQueryRenderer renderer = new SqLiteQueryRenderer();

    @Override
    public SqLiteQueryRenderer renderer() {
        return renderer;
    }
}
