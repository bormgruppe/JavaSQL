package ch.sama.sql.dialect.mysql;

import ch.sama.sql.dialect.mysql.query.*;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.IQueryRenderer;
import ch.sama.sql.query.base.JoinQuery;
import ch.sama.sql.query.base.QueryCreator;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public class MySqlQueryCreator extends QueryCreator {
    private static final MySqlQueryRenderer renderer = new MySqlQueryRenderer();

    @Override
    public MySqlQueryRenderer renderer() {
        return renderer;
    }

    @Override
    public MySqlFromQuery fromQuery(IQuery parent, Source[] sources) {
        return new MySqlFromQuery(this, parent, sources);
    }

    @Override
    public MySqlJoinQueryFinal joinQueryFinal(JoinQuery parent, ICondition condition) {
        return new MySqlJoinQueryFinal(this, parent, condition);
    }

    public MySqlLimitQuery limitQuery(IQuery parent, int limit) {
        return new MySqlLimitQuery(this, parent, limit);
    }

    public MySqlLimitQuery limitQuery(IQuery parent, int start, int stop) {
        return new MySqlLimitQuery(this, parent, start, stop);
    }

    @Override
    public MySqlOrderQuery orderQuery(IQuery parent, IOrder[] orders) {
        return new MySqlOrderQuery(this, parent, orders);
    }

    @Override
    public MySqlSelectQuery selectQuery(IQuery parent, Value[] values) {
        return new MySqlSelectQuery(this, parent, values);
    }

    @Override
    public MySqlWhereQuery whereQuery(IQuery parent, ICondition condition) {
        return new MySqlWhereQuery(this, parent, condition);
    }
}
