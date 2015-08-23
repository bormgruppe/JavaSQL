package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dbo.IType;
import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.Query;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

public class TSqlQuery extends Query {
    private TSqlQueryCreator creator;

    public TSqlQuery(TSqlQueryCreator creator) {
        super(creator);

        this.creator = creator;
    }

    public TSqlQuery(TSqlQueryCreator creator, IQuery parent) {
        super(creator, parent);

        this.creator = creator;
    }

    @Override
    public TSqlSelectQuery select(Value... values) {
        return creator.selectQuery(this, values);
    }

    public TSqlCteQuery with(String alias) {
        return creator.cteQuery(this, alias);
    }

    public TSqlIfQuery doif(ICondition condition, IQuery query) {
        return creator.ifQuery(this, condition, query);
    }

    public TSqlDeclareQuery declare(String variable, IType type, Value value) {
        return creator.declareQuery(this, variable, type, value);
    }
}
