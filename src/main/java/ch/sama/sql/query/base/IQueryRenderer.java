package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Function;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public interface IQueryRenderer {
    public String render(Query query);

    public String render(UnionAllQuery query);

    public String render(UnionQuery query);
    public String render(SelectQuery query);
    public String render(FromQuery query);
    public String render(JoinQuery query);
    public String render(JoinQueryFinal query);
    public String render(WhereQuery query);
    public String render(OrderQuery query);
    public String render(DeleteQuery query);
    public String render(DeleteQueryIM query);
    public String render(DeleteQueryFinal query);
    public String render(InsertQuery query);
    public String render(InsertQueryIM query);
    public String render(InsertQueryFinal query);
    public String render(InsertQueryValues query);
    public String render(UpdateQuery query);
    public String render(UpdateQueryIM query);
    public String render(UpdateQueryFinal query);

    public String render(ICondition condition);
    public String render(IOrder order);

    public String render(Field field);
    public String render(Table t);
    public String render(Value v);
    public String render(Source s);
    public String render(Function f);

    public String renderObjectName(String s);
}
