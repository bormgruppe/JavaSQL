package ch.sama.sql.query.base;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

public class UpdateQueryIM implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;
    private Field field;
    private Value value;

    public UpdateQueryIM(IQueryCreator creator, IQuery parent, Field field, Value value) {
        this.creator = creator;
        this.parent = parent;
        this.field = field;
        this.value = value;
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
    
    public Field getField() {
        return field;
    }
    
    public Value getValue() {
        return value;
    }
	
	public UpdateQueryIM set(Field field, Value value) {
        return creator.updateQueryIM(this, field, value);
	}
    
    public UpdateQueryIM set(String field, Value value) {
        return set(new Field(field), value);
    }
    
    public UpdateQueryFinal where(ICondition condition) {
        return creator.updateQueryFinal(this, condition);
    }
}