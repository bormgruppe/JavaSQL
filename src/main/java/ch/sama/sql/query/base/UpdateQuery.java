package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Value;

public class UpdateQuery implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;
    private Table table;

    public UpdateQuery(IQueryCreator creator, IQuery parent, Table table) {
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
        this.parent = query;
        return query;
    }
    
    public Table getTable() {
        return table;
    }
	
	public UpdateQueryIM set(Field field, Value value) {
        return creator.updateQueryIM(this, field, value);
	}
    
    public UpdateQueryIM set(String field, Value value) {
        return set(new Field(field), value);
    }
}