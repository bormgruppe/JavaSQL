package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dialect.tsql.TSqlQueryCreator;
import ch.sama.sql.dialect.tsql.TSqlQueryRenderer;
import ch.sama.sql.query.base.InsertQueryFinal;
import ch.sama.sql.query.base.InsertQueryIM;
import ch.sama.sql.query.helper.Value;

public class TSqlInsertQueryFinal extends InsertQueryFinal {
    private TSqlQueryCreator creator;

    public TSqlInsertQueryFinal(TSqlQueryCreator creator, InsertQueryIM parent, Field[] fields) {
        super(creator, parent, fields);

        this.creator = creator;
    }

    @Override
    public TSqlSelectQuery select(Value... values) {
        return creator.selectQuery(this, values);
    }

    public TSqlInsertQueryOutput output(Value... values) {
        return creator.insertQueryOutput(this, values);
    }
}
