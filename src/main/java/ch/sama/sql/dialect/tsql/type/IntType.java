package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

public class IntType extends JavaType {
    IntType() { }
    
    @Override
    public String getString() {
        return "int";
    }

    @Override
    public Class getJavaClass() {
        return Integer.class;
    }
}
