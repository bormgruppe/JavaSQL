package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.helper.Value;

import java.util.Arrays;
import java.util.List;

public class TSqlInsertQueryOutput implements IQuery {
    private TSqlQueryCreator creator;
    private TSqlInsertQueryFinal parent;
    private List<Value> values;

    public TSqlInsertQueryOutput(TSqlQueryCreator creator, TSqlInsertQueryFinal parent, Value[] values) {
        this.creator = creator;
        this.parent = parent;
        this.values = Arrays.asList(values);
    }

    public List<Value> getValues() {
        return values;
    }

    @Override
    public TSqlInsertQueryFinal getParent() {
        return parent;
    }

    @Override
    public String getSql() {
        return creator.renderer().render(this);
    }

    @Override
    public IQuery chainTo(IQuery query) {
        return this.parent.chainTo(query);
    }

    public TSqlSelectQuery select(Value... values) {
        return creator.selectQuery(this, values);
    }
}
