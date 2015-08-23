package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dbo.Table;
import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.*;
import ch.sama.sql.query.helper.Value;

public class TSqlCteQueryFinal implements IQuery {
    private TSqlQueryCreator creator;
    private TSqlCteQuery parent;
    private IQuery query;

    public TSqlCteQueryFinal(TSqlQueryCreator creator, TSqlCteQuery parent, IQuery query) {
        this.creator = creator;
        this.parent = parent;
        this.query = query;
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
        return this.parent.chainTo(query);
    }

    public String getCteName() {
        return parent.getName();
    }

    public IQuery getQuery() {
        return query;
    }

    public TSqlCteQuery with(String name) {
        return creator.cteQuery(this, name);
    }

    public TSqlSelectQuery select(Value... values) {
        return creator.selectQuery(this, values);
    }

    public InsertQuery insert() {
        return creator.insertQuery(this);
    }

    public DeleteQuery delete() {
        return creator.deleteQuery(this);
    }

    public UpdateQuery update(Table table) {
        return creator.updateQuery(this, table);
    }

    public UpdateQuery update(String table) {
        return update(new Table(table));
    }

    public UnionQuery union(IQuery... queries) {
        return creator.unionQuery(this, queries);
    }
}
