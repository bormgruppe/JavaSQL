package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dbo.IType;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.*;
import ch.sama.sql.query.base.check.Identifier;
import ch.sama.sql.query.exception.IllegalIdentifierException;
import ch.sama.sql.query.helper.Value;

public class TSqlDeclareQuery implements IQuery {
    private TSqlQueryCreator creator;
    private IQuery parent;
    private String variable;
    private IType type;
    private Value value;

    public TSqlDeclareQuery(TSqlQueryCreator creator, IQuery parent, String variable, IType type, Value value) {
        if (!Identifier.test(variable)) {
            throw new IllegalIdentifierException(variable);
        }

        this.creator = creator;
        this.parent = parent;
        this.variable = variable;
        this.type = type;
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
        this.parent = query;
        return query;
    }

    public String getVariable() {
        return variable;
    }

    public IType getType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public TSqlDeclareQuery declare(String variable, IType type, Value value) {
        return creator.declareQuery(this, variable, type, value);
    }

    public TSqlCteQuery with(String name) {
        return creator.cteQuery(this, name);
    }

    public TSqlSelectQuery select(Value... values) {
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
