package ch.sama.sql.jpa;

import ch.sama.sql.dbo.connection.IConnection;
import ch.sama.sql.dbo.result.IResultSetTransformer;
import ch.sama.sql.dbo.result.obj.ObjectTransformer;
import ch.sama.sql.query.base.*;
import ch.sama.sql.query.exception.BadSqlException;
import ch.sama.sql.query.exception.ConnectionException;
import ch.sama.sql.query.helper.Condition;
import ch.sama.sql.query.helper.Value;
import ch.sama.sql.query.helper.condition.ICondition;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private IConnection connection;
    private IQueryFactory factory;
    private IValueFactory value;
    private ISourceFactory source;

    public Manager(IConnection connection, IQueryFactory factory) {
        this.connection = connection;
        this.factory = factory;
        this.value = factory.value();
        this.source = factory.source();
    }

    private void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) { }
    }

    private <T> Class<?> getObjectClass(T object) {
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new JpaException("Object must be annotated with @Entity");
        }

        return clazz;
    }

    private String getTableName(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        String name = entity.name();

        return "".equals(name) ? clazz.getSimpleName() : name;
    }

    private void execute(IQuery query) {
        String sQuery = query.getSql();
        if ("".equals(sQuery)) {
            return;
        }

        Statement statement;
        try {
            Connection con = connection.open();
            statement = con.createStatement();
        } catch (Exception e) {
            connection.close();

            throw new ConnectionException(e);
        }

        try {
            statement.executeUpdate(sQuery);
        } catch (SQLException e) {
            closeStatement(statement);
            connection.close();

            throw new BadSqlException(e);
        }

        closeStatement(statement);
        connection.close();
    }

    private ResultSet query(IQuery query) {
        Statement statement;
        try {
            Connection con = connection.open();
            statement = con.createStatement();
        } catch (Exception e) {
            connection.close();

            throw new ConnectionException(e);
        }

        ResultSet result = null;
        try {
            result = statement.executeQuery(query.getSql());
        } catch (SQLException e) {
            closeStatement(statement);
            connection.close();

            throw new BadSqlException(e);
        }

        closeStatement(statement);
        connection.close();

        return result;
    }

    public <T> IQuery getInsertQuery(T object) {
        Class<?> clazz = getObjectClass(object);
        String tableName = getTableName(clazz);

        Field[] fields = clazz.getDeclaredFields();

        List<String> columns = new ArrayList<String>();
        List<Value> values = new ArrayList<Value>();

        try {
            for (Field f : fields) {
                if (f.isAnnotationPresent(Column.class) && !f.isAnnotationPresent(AutoIncrement.class)) {
                    columns.add(f.getAnnotation(Column.class).name());
                    values.add(value.object(f.get(object)));
                }
            }
        } catch (IllegalAccessException e) {
            throw new JpaException(e);
        }

        if (!columns.isEmpty() && !values.isEmpty()) { // can I insert an empty object?
            return factory.query()
                    .insert().into(tableName)
                    .columns(columns.toArray(new String[columns.size()]))
                    .values(values.toArray(new Value[values.size()]));
        }

        return factory.query(); // TODO: should this throw?
    }

    public <T> void insert(T object) {
        execute(getInsertQuery(object));
    }

    public <T> IQuery getUpdateQuery(T object) {
        Class<?> clazz = getObjectClass(object);
        String tableName = getTableName(clazz);

        Field[] fields = clazz.getDeclaredFields();

        UpdateQuery start = factory.query().update(tableName);

        UpdateQueryIM iter = null;
        List<ICondition> cond = new ArrayList<ICondition>();

        try {
            for (Field f : fields) {
                String colName = f.getAnnotation(Column.class).name();
                Value val = value.object(f.get(object));

                if (f.isAnnotationPresent(PrimaryKey.class)) {
                    cond.add(Condition.eq(value.field(tableName, colName), val));

                    // TODO: Should primary key be updated as well?
                } else if (!f.isAnnotationPresent(AutoIncrement.class)){
                    if (iter == null) {
                        iter = start.set(colName, val);
                    } else {
                        iter = iter.set(colName, val);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new JpaException(e);
        }

        if (iter != null) { // should only happen when there's no fields
            if (!cond.isEmpty()) {// this would update everything
                if (cond.size() == 1) {
                    return iter.where(cond.get(0));
                } else {
                    return iter.where(Condition.and(cond.toArray(new ICondition[cond.size()])));
                }
            }
        }

        return factory.query(); // TODO: should this throw?
    }

    public <T> void update(T object) {
        execute(getUpdateQuery(object));
    }

    public <T> IQuery getDeleteQuery(T object) {
        Class<?> clazz = getObjectClass(object);
        String tableName = getTableName(clazz);

        Field[] fields = clazz.getDeclaredFields();

        List<ICondition> cond = new ArrayList<ICondition>();

        try {
            for (Field f : fields) {
                String colName = f.getAnnotation(Column.class).name();
                Value val = value.object(f.get(object));

                if (f.isAnnotationPresent(PrimaryKey.class)) {
                    cond.add(Condition.eq(value.field(tableName, colName), val));
                }
            }
        } catch (IllegalAccessException e) {
            throw new JpaException(e);
        }

        DeleteQueryIM query = factory.query().delete().from(tableName);

        if (!cond.isEmpty()) { // this would delete everything
            if (cond.size() == 1) {
                return query.where(cond.get(0));
            } else {
                return query.where(Condition.and(cond.toArray(new ICondition[cond.size()])));
            }
        }

        return factory.query(); // TODO: should this throw?
    }

    public <T> void delete(T object) {
        execute(getDeleteQuery(object));
    }

    public <T> IQuery getGetterQuery(Class<T> clazz) {
        return getGetterQuery(clazz, null);
    }

    public <T> IQuery getGetterQuery(Class<T> clazz, ICondition condition) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new JpaException("Object must be annotated with @Entity");
        }

        String tableName = getTableName(clazz);

        Field[] fields = clazz.getDeclaredFields();

        List<Value> values = new ArrayList<Value>();

        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                values.add(value.field(tableName, f.getAnnotation(Column.class).name()));
            }
        }

        FromQuery part = factory.query()
                .select(values.toArray(new Value[values.size()]))
                .from(source.table(tableName));

        if (condition == null) {
            return part;
        } else {
            return part.where(condition);
        }
    }

    public <T> List<T> get(Class<T> clazz) {
        return get(clazz, null);
    }

    public <T> List<T> get(Class<T> clazz, ICondition condition) {
        IResultSetTransformer<List<T>> transformer = new ObjectTransformer<T>(clazz);

        ResultSet resultSet = query(getGetterQuery(clazz, condition));

        try {
            return transformer.transform(resultSet);
        } catch (SQLException e) {
            throw new BadSqlException(e);
        }
    }
}
