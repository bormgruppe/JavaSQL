package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Table;

public class InsertQuery implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;

    public InsertQuery(IQueryCreator creator, IQuery parent) {
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
	
	public InsertQueryIM into(Table table) {
        return creator.insertQueryIM(this, table);
	}
    
    public InsertQueryIM into(String table) {
        return into(new Table(table));
    }
}