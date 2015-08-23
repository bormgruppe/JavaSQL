package ch.sama.sql.query.base;

import ch.sama.sql.query.helper.Source;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.helper.order.IOrder;

import java.util.Arrays;
import java.util.List;

public class FromQuery implements IQuery {
    private IQueryCreator creator;
	private IQuery parent;
	private List<Source> sources;

    public FromQuery(IQueryCreator creator, IQuery parent, Source[] sources) {
        this.creator = creator;
        this.parent = parent;
        this.sources = Arrays.asList(sources);
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
	
	public List<Source> getSources() {
		return sources;
	}
	
	public JoinQuery join(Source source) {
        return creator.joinQuery(this, source);
	}
	
	public OrderQuery order(IOrder... orders) {
        return creator.orderQuery(this, orders);
	}
	
	public WhereQuery where(ICondition condition) {
        return creator.whereQuery(this, condition);
	}
}