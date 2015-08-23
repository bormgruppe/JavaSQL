package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

import java.util.Arrays;
import java.util.List;

public class SelectQuery implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;
	private List<Value> values;

    public SelectQuery(IQueryCreator creator, IQuery parent, Value[] values) {
        this.creator = creator;
        this.parent = parent;
        this.values = Arrays.asList(values);
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
	
	public List<Value> getValues() {
		return values;
	}
	
	public FromQuery from(Source... sources) {
        return creator.fromQuery(this, sources);
	}

    public WhereQuery where(ICondition condition) {
        return creator.whereQuery(this, condition);
    }
}