package ch.sama.sql.dialect.mysql.query;

import ch.sama.sql.dialect.mysql.MySqlQueryCreator;
import ch.sama.sql.dialect.mysql.MySqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.WhereQuery;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public class MySqlWhereQuery extends WhereQuery {
    private MySqlQueryCreator creator;

    public MySqlWhereQuery(MySqlQueryCreator creator, IQuery parent, ICondition condition) {
        super(creator, parent, condition);

        this.creator = creator;
    }

    @Override
    public MySqlOrderQuery order(IOrder... orders) {
        return creator.orderQuery(this, orders);
    }

    public MySqlLimitQuery limit(int limit) {
        return creator.limitQuery(this, limit);
    }

    public MySqlLimitQuery limit(int start, int stop) {
        return creator.limitQuery(this, start, stop);
    }
}
