package ch.sama.sql.dialect.mysql.query;

import ch.sama.sql.dialect.mysql.MySqlQueryCreator;
import ch.sama.sql.dialect.mysql.MySqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.SelectQuery;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

public class MySqlSelectQuery extends SelectQuery {
    private MySqlQueryCreator creator;

    public MySqlSelectQuery(MySqlQueryCreator creator, IQuery parent, Value[] values) {
        super(creator, parent, values);

        this.creator = creator;
    }

    @Override
    public MySqlFromQuery from(Source... sources) {
        return creator.fromQuery(this, sources);
    }

    @Override
    public MySqlWhereQuery where(ICondition condition) {
        return creator.whereQuery(this, condition);
    }
}
