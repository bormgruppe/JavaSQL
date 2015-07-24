package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

public class UniqueidentifierType extends JavaType {
    UniqueidentifierType() { }
    
    @Override
    public String getString() {
        return "uniqueidentifier";
    }

    @Override
    public Class getJavaClass() {
        return String.class;
    }
}
