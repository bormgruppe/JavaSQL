package ch.sama.sql.query.standard;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.IType;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.IQueryRenderer;
import ch.sama.sql.query.base.IValueFactory;
import ch.sama.sql.query.exception.UnknownValueException;
import ch.sama.sql.query.helper.Function;
import ch.sama.sql.query.helper.Value;

public abstract class ValueFactory implements IValueFactory {
    private IQueryRenderer renderer;

    public ValueFactory(IQueryRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Value field(Field field) {
        return new Value(field, renderer.render(field));
    }

    @Override
    public Value field(String field) {
        return field(new Field(field));
    }

    @Override
    public Value field(String table, String field) {
        return field(new Field(table, field));
    }

    @Override
    public Value field(Table table, String field) {
        return field(new Field(table, field));
    }

    @Override
    public Value plain(String s) {
        return new Value(s);
    }

    @Override
    public Value string(String s) {
        return new Value(s, "'" + s.replace("'", "\\'") + "'");
    }

    @Override
    public Value numeric(Integer i) {
        return new Value(i, i.toString());
    }

    @Override
    public Value numeric(Float f) {
        return new Value(f, f.toString());
    }

    @Override
    public Value numeric(Double d) {
        return new Value(d, d.toString());
    }
    
    @Override
    public Value bool(Boolean b) {
        if (b) {
            return new Value(true, "1");
        } else {
            return new Value(false, "0");
        }
    }

    @Override
    public Value function(String fnc, Value... parameters) {
        return function(new Function(fnc, parameters));
    }

    @Override
    public Value function(Function fnc) {
        return new Value(fnc, renderer.render(fnc));
    }

    @Override
    public Value query(IQuery query) {
        return new Value(query, "(\n" + query.getSql() + "\n)");
    }

    @Override
    public Value value(Value.VALUE value) {
        switch (value) {
            case NULL:
                return new Value(value, "NULL");
            case ALL:
                return new Value(value, "*");
            default:
                throw new UnknownValueException("Caused by: " + value);
        }
    }
    
    @Override
    public Value combine(String operator, Value... values) {
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        
        for (Value v : values) {
            builder.append(prefix);
            builder.append(v.getValue());
            
            prefix = " " + operator + " ";
        }
        
        return new Value(builder.toString());
    }
    
    @Override
    public Value bracket(Value value) {
        return new Value("(" + value.getValue() + ")");
    }
    
    @Override
    public Value type(IType type) {
        return new Value(type, type.getString());
    }
}