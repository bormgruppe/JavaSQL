package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Function;
import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;

public interface IQueryRenderer {
    public String render(Query query);
    public String render(CTEQuery query);
    public String render(CTEQueryFinal query);
    public String render(SelectQuery query);
    public String render(FromQuery query);
    public String render(JoinQuery query);
    public String render(JoinQueryFinal query);
    public String render(WhereQuery query);
    public String render(OrderQuery query);
    public String render(Field field);
    public String render(Table t);
    public String render(Value v);
    public String render(Source s);
    public String render(Function f);
}