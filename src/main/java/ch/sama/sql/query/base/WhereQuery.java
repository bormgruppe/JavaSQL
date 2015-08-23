package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public class WhereQuery implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;
	private ICondition condition;

    public WhereQuery(IQueryCreator creator, IQuery parent, ICondition condition) {
        this.creator = creator;
        this.parent = parent;
        this.condition = condition;
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
    
	public ICondition getCondition() {
		return condition;
	}
	
	public OrderQuery order(IOrder... orders) {
        return creator.orderQuery(this, orders);
	}
}