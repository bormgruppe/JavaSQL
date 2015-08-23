package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.condition.ICondition;

public class UpdateQueryFinal implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;
    private ICondition condition;

    public UpdateQueryFinal(IQueryCreator creator, IQuery parent, ICondition condition) {
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
    
    public ICondition getCondition() {
        return condition;
    }
}