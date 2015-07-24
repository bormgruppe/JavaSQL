package ch.sama.sql.dialect.tsql.type;

public class NVarcharType extends TypeWithLength {
    NVarcharType() {
        super();
    }

    NVarcharType(int max) {
        super(max);
    }
    
    @Override
    public String getString() {
        return "nvarchar" + getMaxLengthAddon();
    }

    @Override
    public Class getJavaClass() {
        return String.class;
    }
}
