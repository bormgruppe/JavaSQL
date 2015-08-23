package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Function;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public interface IQueryCreator {
    public Query query();
    public Query query(IQuery parent);
    public UnionQuery unionQuery(IQuery parent, IQuery[] queries);
    public SelectQuery selectQuery(IQuery parent, Value[] values);
    public FromQuery fromQuery(IQuery parent, Source[] sources);
    public JoinQuery joinQuery(IQuery parent, Source source);
    public JoinQueryFinal joinQueryFinal(JoinQuery parent, ICondition condition);
    public WhereQuery whereQuery(IQuery parent, ICondition condition);
    public OrderQuery orderQuery(IQuery parent, IOrder[] orders);
    public DeleteQuery deleteQuery(IQuery parent);
    public DeleteQueryIM deleteQueryIM(DeleteQuery parent, Table table);
    public DeleteQueryFinal deleteQueryFinal(DeleteQueryIM parent, ICondition condition);
    public InsertQuery insertQuery(IQuery parent);
    public InsertQueryIM insertQueryIM(InsertQuery parent, Table table);
    public InsertQueryFinal insertQueryFinal(InsertQueryIM parent, Field[] fields);
    public InsertQueryValues insertQueryValues(InsertQueryFinal parent, Value[] values);
    public UpdateQuery updateQuery(IQuery parent, Table table);
    public UpdateQueryIM updateQueryIM(IQuery parent, Field field, Value value);
    public UpdateQueryFinal updateQueryFinal(IQuery parent, ICondition condition);

    public IQueryRenderer renderer();
}
