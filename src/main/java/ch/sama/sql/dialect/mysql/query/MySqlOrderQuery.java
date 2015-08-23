package ch.sama.sql.dialect.mysql.query;

import ch.sama.sql.dialect.mysql.MySqlQueryCreator;
import ch.sama.sql.dialect.mysql.MySqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.OrderQuery;
import ch.sama.sql.query.helper.order.IOrder;

public class MySqlOrderQuery extends OrderQuery {
    private MySqlQueryCreator creator;

    public MySqlOrderQuery(MySqlQueryCreator creator, IQuery parent, IOrder[] orders) {
        super(creator, parent, orders);

        this.creator = creator;
    }

    public MySqlLimitQuery limit(int limit) {
        return creator.limitQuery(this, limit);
    }

    public MySqlLimitQuery limit(int start, int stop) {
        return creator.limitQuery(this, start, stop);
    }
}