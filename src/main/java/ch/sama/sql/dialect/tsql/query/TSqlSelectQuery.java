package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.SelectQuery;
import ch.sama.sql.query.exception.BadParameterException;
import ch.sama.sql.query.helper.Value;

public class TSqlSelectQuery extends SelectQuery {
    private TSqlQueryCreator creator;
    private int top;

    public TSqlSelectQuery(TSqlQueryCreator creator, IQuery parent, Value[] values) {
        super(creator, parent, values);

        this.top = -1;
        this.creator = creator;
    }

    public TSqlSelectQuery top(int n) {
        if (n <= 0) {
            throw new BadParameterException("Illegal top value: " + n);
        }

        this.top = n;
        return this;
    }

    @Override
    public String getSql() {
        return creator.renderer().render(this);
    }

    public int getTop() {
        return top;
    }

    public boolean hasTop() {
        return top > 0;
    }
}
