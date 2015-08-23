package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Table;

public class DeleteQuery implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;

    public DeleteQuery(IQueryCreator creator, IQuery parent) {
        this.creator = creator;
        this.parent = parent;
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

	public DeleteQueryIM from(Table table) {
        return creator.deleteQueryIM(this, table);
	}
    
    public DeleteQueryIM from(String table) {
        return from(new Table(table));
    }
}