package ch.sama.sql.query.base;

import java.util.*;

import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Condition;
import ch.sama.sql.query.helper.Order;

public abstract class FromQuery implements IQuery {
	QueryFactory factory;
	private IQuery parent;
	private List<Table> tables;
	
	public IQuery getParent() {
		return parent;
	}
	
	public QueryFactory getFactory() {
		return factory;
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	public FromQuery(QueryFactory factory, IQuery parent, Table... t) {
		this.factory = factory;
		this.parent = parent;
		tables = new ArrayList<Table>();
		
		for (int i = 0; i < t.length; ++i) {
			tables.add(t[i]);
		}
	}
	
	public JoinQuery join(Table table) {
		return factory.joinQuery(factory, this, table);
	}
	
	public OrderQuery order(Order order) {
		return factory.orderQuery(factory, this, order);
	}
	
	public WhereQuery where(Condition condition) {
		return factory.whereQuery(factory, this, condition);
	}
}