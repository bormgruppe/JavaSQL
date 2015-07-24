package ch.sama.sql.dialect.tsql.type;

public class VarcharType extends TypeWithLength {
    VarcharType() {
        super();
    }
    
    VarcharType(int max) {
        super(max);
    }
    
    @Override
    public String getString() {
        return "varchar" + getMaxLengthAddon();
    }

    @Override
    public Class getJavaClass() {
        return String.class;
    }
}
