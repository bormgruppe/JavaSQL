package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Table;

public class InsertQuery implements IQuery {
    private IQueryRenderer renderer;
	private IQuery parent;

    @Override
	public IQuery getParent() {
		return parent;
	}

	public InsertQuery(IQueryRenderer renderer, IQuery parent) {
        this.renderer = renderer;
		this.parent = parent;
	}
	
	public InsertQueryIM into(Table table) {
		return new InsertQueryIM(renderer, this, table);
	}
    
    public InsertQueryIM into(String table) {
        return new InsertQueryIM(renderer, this, new Table(table));
    }

    @Override
    public String getSql() {
        return renderer.render(this);
    }

	@Override
	public IQuery chainTo(IQuery query) {
		this.parent = query;
		return query;
	}
}