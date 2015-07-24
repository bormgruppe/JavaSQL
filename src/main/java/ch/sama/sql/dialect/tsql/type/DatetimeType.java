package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

import java.util.Date;

public class DatetimeType extends JavaType {
    DatetimeType() { }
    
    @Override
    public String getString() {
        return "datetime";
    }

    @Override
    public Class getJavaClass() {
        return Date.class;
    }
}
