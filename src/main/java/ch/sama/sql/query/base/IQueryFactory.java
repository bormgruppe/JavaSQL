package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.condition.IConditionRenderer;
import ch.sama.sql.query.helper.order.IOrderRenderer;

public interface IQueryFactory {
    public IQueryRenderer renderer();
    public IQueryCreator creator();

    public IValueFactory value();
    public ISourceFactory source();

    public IConditionRenderer condition();
    public IOrderRenderer order();

    public Query query();
}
