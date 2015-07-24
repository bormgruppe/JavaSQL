package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

public class BitType extends JavaType {
    BitType() { }
    
    @Override
    public String getString() {
        return "bit";
    }

    @Override
    public Class getJavaClass() {
        return Boolean.class;
    }
}
