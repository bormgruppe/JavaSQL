package ch.sama.sql.dbo.result.obj;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.jpa.AutoIncrement;
import ch.sama.sql.jpa.Column;
import ch.sama.sql.jpa.Entity;
import ch.sama.sql.jpa.PrimaryKey;
import ch.sama.sql.query.base.IQuery;
import ch.sama.sql.query.base.IQueryFactory;
import ch.sama.sql.query.base.IValueFactory;
import ch.sama.sql.query.helper.Condition;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;
import ch.sama.sql.query.standard.ValueFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JpaObject {
    public List<java.lang.reflect.Field> getColumns() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> {
                    if (field.isAnnotationPresent(Column.class)) {
                        field.setAccessible(true);

                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }
    
    public List<java.lang.reflect.Field> getPrimaryKeys() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> {
                    if (field.isAnnotationPresent(PrimaryKey.class)) {
                        field.setAccessible(true);

                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }
    
    public String getTableName() {
        Class<?> clazz = this.getClass();
        
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = (Entity) clazz.getAnnotation(Entity.class);
            String name = entity.name();
            
            if (!"".equals(name)) {
                return name;
            }
        }
        
        return clazz.getSimpleName();
    }

    private Table getTable() {
        return new Table(getTableName());
    }

    private String getColumnName(java.lang.reflect.Field field) {
        return field.getAnnotation(Column.class).name();
    }

    private ICondition getMatchCondition(IQueryFactory fac) {
        Table self = new Table(getTableName());

        return Condition.and(
                getPrimaryKeys().stream()
                        .map(field -> Condition.eq(fac.value().field(new Field(self, getColumnName(field))), toValue(field, fac.value())))
                        .toArray(ICondition[]::new)
        );
    }

    private Value toValue(java.lang.reflect.Field field, IValueFactory val) {
        Object o;
        try {
            o = field.get(this);
        } catch (IllegalAccessException e) {
            o = null;
        }
        
        if (o == null) {
            return ValueFactory.NULL;
        }

        return val.object(o);
    }

    public IQuery update(IQueryFactory fac) {
        Table self = getTable();

        Map<Field, Value> values = getColumns().stream()
                .filter(field -> !field.isAnnotationPresent(PrimaryKey.class))
                .collect(Collectors.toMap(field -> new Field(self, getColumnName(field)), field -> toValue(field, fac.value())));
        
        return fac.query()
                .update(self)
                .set(values)
                .where(getMatchCondition(fac));
    }

    public IQuery delete(IQueryFactory fac) {
        return fac.query()
                .delete()
                .from(getTable())
                .where(getMatchCondition(fac));
    }

    public IQuery insert(IQueryFactory fac) {
        Table self = getTable();

        List<java.lang.reflect.Field> insertFields = getColumns().stream()
                .filter(field -> !field.isAnnotationPresent(AutoIncrement.class))
                .collect(Collectors.toList());

        return fac.query()
                .insert()
                .into(self)
                .columns(
                        insertFields.stream()
                                .map(field -> new Field(self, getColumnName(field)))
                                .toArray(Field[]::new)
                )
                .values(
                        insertFields.stream()
                                .map(field -> toValue(field, fac.value()))
                                .toArray(Value[]::new)
                );
    }
}