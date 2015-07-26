package ch.sama.sql.jpa;

import ch.sama.sql.dbo.IType;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.helper.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JpaUtil<T> {
    private Class<T> clazz;

    public JpaUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<Field> getColumns() {
        Field[] fields = clazz.getDeclaredFields();

        List<Field> columns = new ArrayList<Field>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);

                columns.add(field);
            }
        }

        return columns;
    }

    public boolean isColumn(String colName, Field field) {
        return field
                .getAnnotation(Column.class)
                .name()
                .equalsIgnoreCase(colName);
    }

    public T toObject(Map<String, Object> map) throws IllegalAccessException, InstantiationException {
        T instance = clazz.newInstance();

        List<Field> columns = getColumns();

        for (String key : map.keySet()) {
            Object val = map.get(key);

            if (val != null) {
                for (Field col : columns) {
                    if (isColumn(key, col)) {
                        col.set(instance, val);
                    }
                }
            }
        }

        return instance;
    }

    public Map<String, Object> toMap(T obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();

        List<Field> columns = getColumns();

        for (Field col : columns) {
            map.put(
                    col.getAnnotation(Column.class).name(),
                    col.get(obj)
            );
        }

        return map;
    }

    public Table toTable(Class<T> clazz, Function<Class<?>, IType> classToType) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new JpaException("Class must be annotated with @Entity");
        }

        Table table;

        Entity entity = clazz.getAnnotation(Entity.class);

        String tableName = entity.name();
        if ("".equals(tableName)) {
            tableName = clazz.getSimpleName();
        }

        String schema = entity.schema();

        if ("".equals(schema)) {
            table = new Table(tableName);
        } else {
            table = new Table(schema, tableName);
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                Column aCol = f.getAnnotation(Column.class);

                ch.sama.sql.dbo.Field column = new ch.sama.sql.dbo.Field(table, aCol.name());

                if (aCol.nullable()) {
                    column.setNullable();
                } else {
                    column.setNotNullable();
                }

                if (f.isAnnotationPresent(PrimaryKey.class)) {
                    column.setAsPrimaryKey();
                }

                if (f.isAnnotationPresent(AutoIncrement.class)) {
                    column.setAutoIncrement();
                }

                if (f.isAnnotationPresent(Default.class)) {
                    column.setDefaultValue(new Value(f.getAnnotation(Default.class).value()));
                }

                column.setDataType(classToType.apply(f.getType()));

                table.addColumn(column);
            }
        }

        return table;
    }
}
