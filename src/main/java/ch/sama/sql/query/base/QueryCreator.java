package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public abstract class QueryCreator implements IQueryCreator {
    @Override
    public Query query() {
        return new Query(this);
    }

    @Override
    public Query query(IQuery parent) {
        return new Query(this, parent);
    }

    @Override
    public UnionQuery unionQuery(IQuery parent, IQuery[] queries) {
        return new UnionQuery(this, parent, queries);
    }

    @Override
    public SelectQuery selectQuery(IQuery parent, Value[] values) {
        return new SelectQuery(this, parent, values);
    }

    @Override
    public FromQuery fromQuery(IQuery parent, Source[] sources) {
        return new FromQuery(this, parent, sources);
    }

    @Override
    public JoinQuery joinQuery(IQuery parent, Source source) {
        return new JoinQuery(this, parent, source);
    }

    @Override
    public JoinQueryFinal joinQueryFinal(JoinQuery parent, ICondition condition) {
        return new JoinQueryFinal(this, parent, condition);
    }

    @Override
    public WhereQuery whereQuery(IQuery parent, ICondition condition) {
        return new WhereQuery(this, parent, condition);
    }

    @Override
    public OrderQuery orderQuery(IQuery parent, IOrder[] orders) {
        return new OrderQuery(this, parent, orders);
    }

    @Override
    public DeleteQuery deleteQuery(IQuery parent) {
        return new DeleteQuery(this, parent);
    }

    @Override
    public DeleteQueryIM deleteQueryIM(DeleteQuery parent, Table table) {
        return new DeleteQueryIM(this, parent, table);
    }

    @Override
    public DeleteQueryFinal deleteQueryFinal(DeleteQueryIM parent, ICondition condition) {
        return new DeleteQueryFinal(this, parent, condition);
    }

    @Override
    public InsertQuery insertQuery(IQuery parent) {
        return new InsertQuery(this, parent);
    }

    @Override
    public InsertQueryIM insertQueryIM(InsertQuery parent, Table table) {
        return new InsertQueryIM(this, parent, table);
    }

    @Override
    public InsertQueryFinal insertQueryFinal(InsertQueryIM parent, Field[] fields) {
        return new InsertQueryFinal(this, parent, fields);
    }

    @Override
    public InsertQueryValues insertQueryValues(InsertQueryFinal parent, Value[] values) {
        return new InsertQueryValues(this, parent, values);
    }

    @Override
    public UpdateQuery updateQuery(IQuery parent, Table table) {
        return new UpdateQuery(this, parent, table);
    }

    @Override
    public UpdateQueryIM updateQueryIM(IQuery parent, Field field, Value value) {
        return new UpdateQueryIM(this, parent, field, value);
    }

    @Override
    public UpdateQueryFinal updateQueryFinal(IQuery parent, ICondition condition) {
        return new UpdateQueryFinal(this, parent, condition);
    }
}
