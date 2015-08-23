package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;

public class InsertQueryIM implements IQuery {
    private IQueryCreator creator;
	private InsertQuery parent;
    private Table table;

    public InsertQueryIM(IQueryCreator creator, InsertQuery parent, Table table) {
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
	
	public InsertQueryFinal columns(Field... fields) {
        return creator.insertQueryFinal(this, fields);
	}
    
    public InsertQueryFinal columns(String... fields) {
        Field[] fieldList = new Field[fields.length];
        for (int i = 0; i < fieldList.length; ++i) {
            fieldList[i] = new Field(fields[i]);
        }
        
        return columns(fieldList);
    }
}