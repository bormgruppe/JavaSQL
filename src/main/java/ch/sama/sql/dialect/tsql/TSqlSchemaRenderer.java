package ch.sama.sql.dialect.tsql;

import ch.sama.sql.dbo.*;
import ch.sama.sql.dialect.tsql.type.TYPE;
import ch.sama.sql.jpa.*;
import ch.sama.sql.query.exception.BadSqlException;
import ch.sama.sql.query.helper.Value;

import java.util.List;

public class TSqlSchemaRenderer {
    private String renderTableName(Table t) {
        String schema = t.getSchema();
        if (schema == null) {
            return "[" + t.getName() + "]";
        } else {
            return "[" + schema + "].["+  t.getName() + "]";
        }
    }

    public String render(Table table) {
        // TODO: Ignores FK constraints

        StringBuilder builder = new StringBuilder();
        String prefix = "";

        builder.append("CREATE TABLE ");
        builder.append(renderTableName(table));
        builder.append(" (\n");

        for (Field field : table.getColumns()) {
            builder.append(prefix);
            builder.append("\t");
            builder.append(renderColumn(field));

            prefix = ",\n";
        }

        if (table.hasPrimaryKey()) {
            builder.append(",\n\tCONSTRAINT [PK_");
            builder.append(table.getName());
            builder.append("] PRIMARY KEY CLUSTERED (\n");

            prefix = "";
            for (Field pKey : table.getPrimaryKey()) {
                builder.append(prefix);
                builder.append("\t\t[");
                builder.append(pKey.getName());
                builder.append("] ASC");

                prefix = ",\n";
            }

            builder.append("\n\t)\n) ON [PRIMARY]");
            // Ignores all the options: WITH  (...) ON [PRIMARY]
        } else {
            builder.append("\n)");
        }

        return builder.toString();
    }

    private String renderColumn(Field field) {
        StringBuilder builder = new StringBuilder();

        String colName = field.getName();

        builder.append("[");
        builder.append(colName);
        builder.append("] [");

        IType type = field.getDataType();
        String dataType = type.getString();
        if (dataType.contains("(")) {
            int idx = dataType.indexOf("(");

            builder.append(dataType.substring(0, idx));
            builder.append("]");
            builder.append(dataType.substring(idx));
        } else {
            builder.append(dataType);
            builder.append("]");
        }

        boolean autoIncr = field.isAutoIncrement();
        if (autoIncr) {
            if (!TYPE.isEqualType(type, TYPE.INT_TYPE)) {
                throw new BadSqlException("AutoIncrement on non-integer column: " + colName);
            }

            builder.append(" IDENTITY(1,1)");
        }

        if (field.isNullable()) {
            builder.append(" NULL");
        } else {
            builder.append(" NOT NULL");
        }

        Value defaultValue = field.getDefaultValue();
        if (defaultValue != null) {
            builder.append(" CONSTRAINT [DF_");
            builder.append(field.getTableName());
            builder.append("_");
            builder.append(field.getName());
            builder.append("] DEFAULT ");

            String defaultString = defaultValue.getValue();

            if (!(defaultString.startsWith("(") && defaultString.endsWith(")"))) {
                builder.append("(");
                builder.append(defaultString);
                builder.append(")");
            } else {
                builder.append(defaultString);
            }
        }

        return builder.toString();
    }

    public <T> String render(Class<T> clazz) {
        JpaUtil<T> util = new JpaUtil<T>(clazz);

        Table t = util.toTable(clazz, TYPE::fromClass);
        return render(t);
    }

    public String renderDiff(Table lhs, Table rhs) {
        // lhs: actual
        // rhs: expected

        // This only executes non-destructive updates
        //  - No drop column
        //  - No change of PRIMARY KEY (that would require DROP and re-CREATE)

        StringBuilder builder = new StringBuilder();
        String prefix = "";

        List<Field> rhColumns = rhs.getColumns();

        for (Field fr : rhColumns) {
            String rhColumnName = fr.getName();

            if (lhs.hasColumn(rhColumnName)) {
                Field fl = lhs.getColumn(rhColumnName);

                if (!fr.compareTo(fl)) {
                    builder.append(prefix);
                    builder.append("ALTER TABLE ");
                    builder.append(renderTableName(rhs));
                    builder.append(" ALTER COLUMN ");
                    builder.append(renderColumn(fr));

                    prefix = "\n";
                }
            } else {
                builder.append(prefix);
                builder.append("ALTER TABLE [");
                builder.append(renderTableName(rhs));
                builder.append(" ADD ");
                builder.append(renderColumn(fr));

                prefix = "\n";
            }
        }

        return builder.toString();
    }

    public String getSchemaDiff(ISchema lhs, ISchema rhs) {
        // lhs: actual
        // rhs: expected

        // This only executes non-destructive updates
        //  - No drop table

        StringBuilder builder = new StringBuilder();
        String prefix = "";

        List<Table> rhTables = rhs.getTables();

        for (Table tr : rhTables) {
            String rhTableName = tr.getName();

            if (lhs.hasTable(rhTableName)) {
                Table tl = lhs.getTable(rhTableName);

                builder.append(prefix);
                builder.append(renderDiff(tl, tr));

                prefix = "\n";
            } else {
                builder.append(prefix);
                builder.append(render(tr));

                prefix = "\n";
            }
        }

        return builder.toString();
    }
}
