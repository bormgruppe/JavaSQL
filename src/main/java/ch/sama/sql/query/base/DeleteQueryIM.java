package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.condition.ICondition;

public class DeleteQueryIM implements IQuery {
    private IQueryCreator creator;
	private DeleteQuery parent;
    private Table table;

    public DeleteQueryIM(IQueryCreator creator, DeleteQuery parent, Table table) {
        this.creator = creator;
        this.parent = parent;
        this.table = table;
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
    
    public Table getTable() {
        return table;
    }
	
	public DeleteQueryFinal where(ICondition condition) {
        return creator.deleteQueryFinal(this, condition);
	}
}