package ch.sama.sql.dialect.mysql.query;

import ch.sama.sql.dialect.mysql.MySqlQueryCreator;
import ch.sama.sql.dialect.mysql.MySqlQueryRenderer;
import ch.sama.sql.query.base.JoinQuery;
import ch.sama.sql.query.base.JoinQueryFinal;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public class MySqlJoinQueryFinal extends JoinQueryFinal {
    private MySqlQueryCreator creator;

    public MySqlJoinQueryFinal(MySqlQueryCreator creator, JoinQuery parent, ICondition condition) {
        super(creator, parent, condition);

        this.creator = creator;
    }

    @Override
    public MySqlOrderQuery order(IOrder... orders) {
        return creator.orderQuery(this, orders);
    }

    @Override
    public MySqlWhereQuery where(ICondition condition) {
        return creator.whereQuery(this, condition);
    }

    public MySqlLimitQuery limit(int limit) {
        return creator.limitQuery(this, limit);
    }

    public MySqlLimitQuery limit(int start, int stop) {
        return creator.limitQuery(this, start, stop);
    }
}
