package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Value;

public class Query implements IQuery {
    private IQueryCreator creator;
    private IQuery parent;

	public Query(IQueryCreator creator) {
        this.creator = creator;
    }

    public Query(IQueryCreator creator, IQuery parent) {
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
		
	public SelectQuery select(Value... values) {
        return creator.selectQuery(this, values);
	}

    public InsertQuery insert() {
        return creator.insertQuery(this);
    }
    
    public DeleteQuery delete() {
        return creator.deleteQuery(this);
    }
    
    public UpdateQuery update(Table table) {
        return creator.updateQuery(this, table);
    }

    public UpdateQuery update(String table) {
        return update(new Table(table));
    }

    public UnionQuery union(IQuery... queries) {
        return creator.unionQuery(this, queries);
    }
}