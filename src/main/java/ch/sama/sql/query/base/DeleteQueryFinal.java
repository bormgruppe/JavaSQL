package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.condition.ICondition;

public class DeleteQueryFinal implements IQuery {
    private IQueryCreator creator;
	private DeleteQueryIM parent;
    private ICondition condition;

    public DeleteQueryFinal(IQueryCreator creator, DeleteQueryIM parent, ICondition condition) {
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
    
    public Table getTable() {
        return parent.getTable();
    }
}