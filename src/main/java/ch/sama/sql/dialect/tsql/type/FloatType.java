package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

public class FloatType extends JavaType {
    FloatType() { }
    
    @Override
    public String getString() {
        return "float";
    }

    @Override
    public Class getJavaClass() {
        return Double.class;
    }
}
