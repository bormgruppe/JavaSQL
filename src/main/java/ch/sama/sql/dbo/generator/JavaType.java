package ch.sama.sql.dbo.generator;

import ch.sama.sql.dbo.IType;

public abstract class JavaType implements IType {
    public abstract Class getJavaClass();
}
