package ch.sama.sql.dialect.mysql;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.GenericType;
import ch.sama.sql.dbo.result.map.MapTransformer;
import ch.sama.sql.dbo.schema.ISchema;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.dbo.connection.IQueryExecutor;
import ch.sama.sql.dbo.result.map.MapResult;
import ch.sama.sql.query.exception.ObjectNotFoundException;
import ch.sama.sql.query.helper.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MySqlSchema implements ISchema {
    private static final Logger logger = LoggerFactory.getLogger(MySqlSchema.class);

    private static final MySqlQueryFactory sql = new MySqlQueryFactory();
    private static final MySqlValueFactory value = sql.value();
    private static final MySqlSourceFactory source = sql.source();
    private static final MySqlQueryRenderer renderer = sql.renderer();

    private Map<String, Table> tables;

    public MySqlSchema(String db, IQueryExecutor executor) {
        loadSchema(db, executor, table -> true);
    }

    public MySqlSchema(String db, IQueryExecutor executor, Function<String, Boolean> filter) {
        loadSchema(db, executor, filter);
    }

    private void loadSchema(String db, IQueryExecutor executor, Function<String, Boolean> filter) {
        MapTransformer transformer = new MapTransformer();

        tables = new HashMap<String, Table>();

        List<MapResult> result = executor.query(
                sql.query()
                        .select(
                                value.field("TABLE_SCHEMA"),
                                value.field("TABLE_NAME")
                        )
                        .from(source.table("INFORMATION_SCHEMA", "TABLES").as("t"))
                        .where(Condition.eq(value.field("t", "TABLE_SCHEMA"), value.string(db)))
                .getSql(),
                transformer
        );

        for (MapResult row : result) {
            String schema = row.getAsString("TABLE_SCHEMA");
            String table = row.getAsString("TABLE_NAME");

            if (!filter.apply(table)) {
                continue;
            }

            logger.debug("Creating table: " + table);

            Table t = new Table(schema, table);
            tables.put(table, t);

            List<MapResult> columns = executor.query(
                    sql.query()
                            .select(
                                    value.field("COLUMN_NAME"),
                                    value.field("DATA_TYPE"),
                                    value.field("CHARACTER_MAXIMUM_LENGTH"),
                                    value.field("IS_NULLABLE"),
                                    value.field("COLUMN_DEFAULT"),
                                    value.function("COALESCE",
                                            value.query(
                                                    sql.query()
                                                            .select(value.numeric(1))
                                                            .from(source.table("INFORMATION_SCHEMA", "KEY_COLUMN_USAGE").as("kcu"))
                                                            .where(
                                                                    Condition.and(
                                                                            Condition.eq(value.field("kcu", "CONSTRAINT_NAME"), value.string("PRIMARY")),
                                                                            Condition.eq(value.field("kcu", "COLUMN_NAME"), value.field("COLUMNS", "COLUMN_NAME")),
                                                                            Condition.eq(value.field("kcu", "TABLE_NAME"), value.field("COLUMNS", "TABLE_NAME"))
                                                                    )
                                                            )
                                            ),
                                            value.numeric(0)
                                    ).as("IS_PKEY"),
                                    value.plain("(CASE WHEN EXTRA LIKE '%auto_increment%' THEN 1 ELSE 0 END)").as("AUTO_INCR")
                            )
                            .from(source.table("INFORMATION_SCHEMA", "COLUMNS"))
                            .where(Condition.eq(value.field("TABLE_NAME"), value.string(table)))
                    .getSql(),
                    transformer
            );

            for (MapResult column : columns) {
                Field f = new Field(t, column.getAsString("COLUMN_NAME"));

                String dataType = column.getAsString("DATA_TYPE");
                Object maxLength = column.get("CHARACTER_MAXIMUM_LENGTH");
                if (maxLength != null && maxLength instanceof Long) {
                    long l = (Long) maxLength;

                    if (l == -1) {
                        dataType += "(max)";
                    } else {
                        dataType += "(" + l + ")";
                    }
                }
                f.setDataType(new GenericType(dataType));

                String nullable = column.getAsString("IS_NULLABLE");
                if ("NO".equals(nullable)) {
                    f.setNotNullable();
                } else {
                    f.setNullable();
                }

                String defaultValue = column.getAsString("COLUMN_DEFAULT");
                if (defaultValue != null) {
                    // The data type is inconsequential here
                    f.setDefaultValue(value.plain(defaultValue));
                }

                t.addColumn(f);

                if (column.getAsLong("IS_PKEY") == 1) {
                    f.setAsPrimaryKey();
                }

                if (column.getAsLong("AUTO_INCR") == 1) {
                    f.setAutoIncrement();
                }
            }
        }
    }

    // I think MySql makes all tables lowercase
    private String formatTableName(String table) {
        return table.toLowerCase();
    }

    @Override
    public List<Table> getTables() {
        return new ArrayList<Table>(tables.values());
    }

    @Override
    public boolean hasTable(String name) {
        return tables.containsKey(formatTableName(name));
    }

    @Override
    public Table getTable(String name) {
        String fName = formatTableName(name);

        if (!tables.containsKey(fName)) {
            throw new ObjectNotFoundException("Table " + name + " could not be cound");
        }

        return tables.get(fName);
    }

    public static String getTableSchema(Table table) {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ");
        builder.append(renderer.renderObjectName(table.getName()));
        builder.append(" (\n");

        builder.append(
                table.getColumns().stream()
                        .map(f -> {
                            StringBuilder sb = new StringBuilder();

                            sb.append("\t");
                            sb.append(renderer.renderObjectName(f.getName()));
                            sb.append(" ");
                            sb.append(f.getDataType().getString());

                            if (f.hasDefaultValue()) {
                                if (!f.isNullable()) {
                                    sb.append(" NOT NULL");
                                }

                                sb.append(" DEFAULT ");
                                sb.append(f.getDefaultValue().getValue());
                            } else {
                                if (f.isNullable()) {
                                    sb.append(" DEFAULT NULL");
                                } else {
                                    sb.append(" NOT NULL");
                                }
                            }

                            return sb.toString();
                        })
                        .collect(Collectors.joining(",\n"))
        );

        builder.append("\n);");

        for (Field field : table.getColumns()) {
            if (field.isPrimaryKey()) {
                builder.append("\n\nALTER TABLE ");
                builder.append(renderer.renderObjectName(table.getName()));
                builder.append("\nADD PRIMARY KEY (");
                builder.append(renderer.renderObjectName(field.getName()));
                builder.append(");");
            }

            if (field.isAutoIncrement()) {
                builder.append("\n\nALTER TABLE ");
                builder.append(renderer.renderObjectName(table.getName()));
                builder.append("\nMODIFY ");
                builder.append(renderer.renderObjectName(field.getName()));
                builder.append(" ");
                builder.append(field.getDataType().getString());

                // TODO: I guess I can ignore default here?
                if (!field.isNullable()) {
                    builder.append(" NOT NULL");
                }

                builder.append(" AUTO_INCREMENT;");
            }
        }

        return builder.toString();
    }
}
