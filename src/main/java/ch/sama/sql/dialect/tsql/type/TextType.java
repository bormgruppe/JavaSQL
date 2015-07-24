package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

public class TextType extends JavaType {
    TextType() { }
    
    @Override
    public String getString() {
        return "text";
    }

    @Override
    public Class getJavaClass() {
        return String.class;
    }
}
