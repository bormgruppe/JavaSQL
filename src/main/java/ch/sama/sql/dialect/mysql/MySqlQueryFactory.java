package ch.sama.sql.dialect.mysql;

import ch.sama.sql.query.base.IQueryFactory;
import ch.sama.sql.query.base.Query;

public class MySqlQueryFactory implements IQueryFactory {
    private static final MySqlQueryRenderer renderer = new MySqlQueryRenderer();
    private static final MySqlQueryCreator creator = new MySqlQueryCreator();

    private static final MySqlValueFactory value = new MySqlValueFactory();
    private static final MySqlSourceFactory source = new MySqlSourceFactory();

    private static final MySqlConditionRenderer condition = new MySqlConditionRenderer();
    private static final MySqlOrderRenderer order = new MySqlOrderRenderer();

    @Override
    public MySqlQueryRenderer renderer() {
        return renderer;
    }

    @Override
    public MySqlQueryCreator creator() {
        return creator;
    }

    @Override
    public MySqlValueFactory value() {
        return value;
    }

    @Override
    public MySqlSourceFactory source() {
        return source;
    }

    @Override
    public MySqlConditionRenderer condition() {
        return condition;
    }

    @Override
    public MySqlOrderRenderer order() {
        return order;
    }

    @Override
    public Query query() {
        return creator.query();
    }
}
