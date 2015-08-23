package ch.sama.sql.dialect.tsql;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.IType;
import ch.sama.sql.dialect.tsql.query.*;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.IQueryRenderer;
import ch.sama.sql.query.base.InsertQueryIM;
import ch.sama.sql.query.base.QueryCreator;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

public class TSqlQueryCreator extends QueryCreator {
    private static final TSqlQueryRenderer renderer = new TSqlQueryRenderer();

    @Override
    public TSqlQueryRenderer renderer() {
        return renderer;
    }

    public TSqlCteQuery cteQuery(IQuery parent, String name) {
        return new TSqlCteQuery(this, parent, name);
    }

    public TSqlCteQueryFinal cteQueryFinal(TSqlCteQuery parent, IQuery query) {
        return new TSqlCteQueryFinal(this, parent, query);
    }

    public TSqlDeclareQuery declareQuery(IQuery parent, String variable, IType type, Value value) {
        return new TSqlDeclareQuery(this, parent, variable, type, value);
    }

    public TSqlElseQuery elseQuery(IQuery parent, IQuery query) {
        return new TSqlElseQuery(this, parent, query);
    }

    public TSqlIfQuery ifQuery(IQuery parent, ICondition condition, IQuery query) {
        return new TSqlIfQuery(this, parent, condition, query);
    }

    @Override
    public TSqlInsertQueryFinal insertQueryFinal(InsertQueryIM parent, Field[] fields) {
        return new TSqlInsertQueryFinal(this, parent, fields);
    }

    public TSqlInsertQueryOutput insertQueryOutput(TSqlInsertQueryFinal parent, Value[] values) {
        return new TSqlInsertQueryOutput(this, parent, values);
    }

    @Override
    public TSqlQuery query() {
        return new TSqlQuery(this);
    }

    @Override
    public TSqlQuery query(IQuery parent) {
        return new TSqlQuery(this, parent);
    }

    @Override
    public TSqlSelectQuery selectQuery(IQuery parent, Value[] values) {
        return new TSqlSelectQuery(this, parent, values);
    }
}
