package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

public class JoinQueryFinal implements IQuery {
    private IQueryCreator creator;
	private JoinQuery parent;
	private ICondition condition;

	public JoinQueryFinal(IQueryCreator creator, JoinQuery parent, ICondition condition) {
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
        return this.parent.chainTo(query);
    }

	public Source getJoinSource() {
		return parent.getSource();
	}

	public ICondition getCondition() {
		return condition;
	}
	
	public JoinQuery join(Source source) {
		return creator.joinQuery(this, source);
	}
	
	public OrderQuery order(IOrder... orders) {
		return creator.orderQuery(this, orders);
	}
	
	public WhereQuery where(ICondition condition) {
		return creator.whereQuery(this, condition);
	}
}