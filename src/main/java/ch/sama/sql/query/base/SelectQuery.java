package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

import java.util.Arrays;
import java.util.List;

public class SelectQuery implements IQuery {
    private IQueryFactory factory;
	private IQuery parent;
	private List<Value> values;

    public SelectQuery(IQueryFactory factory, IQuery parent, Value[] v) {
        this.factory = factory;
        this.parent = parent;
        this.values = Arrays.asList(v);
    }

    @Override
	public IQuery getParent() {
		return parent;
	}

    @Override
    public String getSql() {
        return factory.renderer().render(this);
    }

    @Override
    public IQuery chainTo(IQuery query) {
        this.parent = query;
        return query;
    }
	
	public List<Value> getValues() {
		return values;
	}
	
	public FromQuery from(Source... s) {
        return factory.from(this, s);
	}

    public Query union() {
        return factory.query(this);
    }

    public WhereQuery where(ICondition c) {
        return factory.where(this, c);
    }
}