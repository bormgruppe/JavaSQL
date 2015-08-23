package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.helper.condition.ICondition;

public class TSqlIfQuery implements IQuery {
    private TSqlQueryCreator creator;
    private IQuery parent;
    private ICondition condition;
    private IQuery query;

    public TSqlIfQuery(TSqlQueryCreator creator, IQuery parent, ICondition condition, IQuery query) {
        this.creator = creator;
        this.parent = parent;
        this.condition = condition;
        this.query = query;
    }

    public ICondition getCondition() {
        return condition;
    }

    public IQuery getQuery() {
        return query;
    }

    @Override
    public IQuery getParent() {
        return parent;
    }

    @Override
    public String getSql() {
        return creator.renderer().render(this);
    }

    @Override
    public IQuery chainTo(IQuery query) {
        this.parent = query;
        return query;
    }

    public TSqlElseQuery otherwise(IQuery query) {
        return creator.elseQuery(this, query);
    }
}
