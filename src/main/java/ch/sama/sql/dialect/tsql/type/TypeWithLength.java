package ch.sama.sql.dialect.tsql.type;

import ch.sama.sql.dbo.generator.JavaType;

public abstract class TypeWithLength extends JavaType {
    private boolean hasMaxLength;
    private int maxLength;

    public TypeWithLength() {
        this.hasMaxLength = false;
    }

    public TypeWithLength(int max) {
        this.hasMaxLength = true;
        this.maxLength = max;
    }

    public boolean hasMaxLength() {
        return hasMaxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    String getMaxLengthAddon() {
        if (hasMaxLength) {
            if (maxLength < 0) {
                return "(MAX)";
            } else {
                return "(" + maxLength  + ")";
            }
        } else {
            return "";
        }
    }
}
