package ch.sama.sql.dialect.tsql.type;

public class CharType extends TypeWithLength {
    CharType() {
        super();
    }

    CharType(int max) {
        super(max);
    }
    
    @Override
    public String getString() {
        return "char" + getMaxLengthAddon();
    }

    @Override
    public Class getJavaClass() {
        return String.class;
    }
}
