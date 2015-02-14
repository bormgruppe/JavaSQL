package ch.sama.sql.tsql.dialect;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.query.base.IQueryRenderer;
import ch.sama.sql.query.base.IValueFactory;
import ch.sama.sql.query.exception.BadParameterException;
import ch.sama.sql.query.helper.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TSqlMerger {
    public static class Pair {
        private Field f;
        private Value v;
        
        Pair(Field f, Value v) {
            if (f.getDataType() == null) {
                throw new BadParameterException("Field [" + f.getName() + "] has no type");
            }
            
            this.f = f;
            this.v = v;
        }
        
        public Field getField() {
            return f;
        }
        
        public Value getValue() {
            return v;
        }
    }
    
    public static class MergeQuery {
        private Table table;
        
        MergeQuery(Table table) {
            this.table = table;
        }
        
        Table getTable() {
            return table;
        }
        
        @SafeVarargs
        public final MergeValues values(List<Pair>... values) {
            List<List<Pair>> list = new ArrayList<List<Pair>>();
            Collections.addAll(list, values);
            
            return new MergeValues(this, list);
        }
    }

    public static class MergeValues {
        private List<List<Pair>> values;
        private MergeQuery parent;
        
        MergeValues(MergeQuery parent, List<List<Pair>> values) {
            if (values.size() == 0) {
                throw new BadParameterException("No values provided");
            }
            
            this.parent = parent;
            
            this.values = new ArrayList<List<Pair>>();

            int length = values.get(0).size();
            for (List<Pair> row : values) {
                if (row.size() != length) {
                    throw new BadParameterException("All rows must have same size");
                }
                
                this.values.add(row);
            }
        }
        
        MergeQuery getParent() {
            return parent;
        }

        List<List<Pair>> getValues() {
            return values;
        }
        
        public final MatchFields matching(Field... fields) {
            return new MatchFields(this, fields);
        }
        
        public final MatchFields matching(String... fields) {
            Field[] fieldList = new Field[fields.length];
            for (int i = 0; i < fields.length; ++i) {
                fieldList[i] = new Field(fields[i]);
            }
            
            return matching(fieldList);
        }
    }

    public static class MatchFields {
        private List<Field> fields;
        private MergeValues parent;
        
        MatchFields(MergeValues parent, Field... fields) {
            // Not possible, since the matching methods would be ambiguous
            if (fields.length == 0) {
                throw new BadParameterException("Must match at least one field");
            }
            
            this.parent = parent;
            
            this.fields = new ArrayList<Field>();
            Collections.addAll(this.fields, fields);
        }
        
        MergeValues getParent() {
            return parent;
        }

        List<Field> getFields() {
            return fields;
        }

        public final OmitFields omit() {
            return new OmitFields(this);
        }

        public final OmitFields omit(String... fields) {
            return new OmitFields(this, fields);
        }

        public final OmitFields omit(Field... fields) {
            String[] fieldList = new String[fields.length];
            for (int i = 0; i < fields.length; ++i) {
                fieldList[i] = fields[i].getName();
            }

            return omit(fieldList);
        }
    }

    public static class OmitFields {
        private IQueryRenderer renderer;
        private List<String> fields;
        private MatchFields parent;
        
        OmitFields(MatchFields parent, String... fields) {
            this.parent = parent;

            if (fields.length == getParent().getParent().getValues().get(0).size()) {
                throw new BadParameterException("Cannot omit everything");
            }

            this.fields = new ArrayList<String>();
            Collections.addAll(this.fields, fields);

            this.renderer = new TSqlQueryRenderer();
        }

        MatchFields getParent() {
            return parent;
        }
        
        private boolean omitField(String field) {
            return fields.contains(field);
        }
        
        public String getSql() {
            StringBuilder builder = new StringBuilder();
            String prefix;
            
            MatchFields matchFields = getParent();
            MergeValues mergeValues = matchFields.getParent();
            MergeQuery mergeQuery = mergeValues.getParent();
            
            Table table = mergeQuery.getTable();
            List<List<Pair>> values = mergeValues.getValues();
            List<Pair> fields = values.get(0);
            List<Field> matchers = matchFields.getFields();
            
            builder.append("DECLARE @table (\n");
            
            prefix = "";
            for (Pair pair : fields) {
                Field field = pair.getField();
                String name = field.getName();
                String type = field.getDataType();
                
                builder.append(prefix);
                builder.append(name);
                builder.append(" ");
                builder.append(type);
                
                prefix = ",\n";
            }
            
            builder.append("\n)\n");
            builder.append("INSERT INTO @table\n");
            
            prefix = "";
            for (List<Pair> row : values) {
                builder.append(prefix);
                
                builder.append("SELECT ");
                
                String listPrefix = "";
                for (Pair pair : row) {
                    Value val = pair.getValue();
                    
                    builder.append(listPrefix);
                    builder.append(val.getValue());
                    
                    listPrefix = ", ";
                }
                
                prefix = " UNION ALL\n";
            }
            
            builder.append("\n");
            builder.append("MERGE INTO ");
            builder.append(table.getString(renderer));
            builder.append(" AS [old]\n");
            builder.append("USING @table AS [new] ON (\n");
            
            prefix = "";
            for (Field field : matchers) {
                String name = field.getName();
                
                builder.append(prefix);
                builder.append("[old].[");
                builder.append(name);
                builder.append("] = [new].[");
                builder.append(name);
                builder.append("]");
                
                prefix = " AND ";
            }
            
            builder.append("\n)\n");
            builder.append("WHEN MATCHED THEN\n");
            builder.append("UPDATE SET ");
            
            prefix = "";
            for (Pair pair : fields) {
                String name = pair.getField().getName();
                
                if (!omitField(name)) {
                    builder.append(prefix);
                    builder.append("[");
                    builder.append(name);
                    builder.append("] = [new].[");
                    builder.append(name);
                    builder.append("]");

                    prefix = ", ";
                }
            }
            
            builder.append("\n");
            builder.append("WHEN NOT MATCHED BY TARGET THEN\n");
            builder.append("INSERT (");

            prefix = "";
            for (Pair pair : fields) {
                String name = pair.getField().getName();

                if (!omitField(name)) {
                    builder.append(prefix);
                    builder.append("[");
                    builder.append(name);
                    builder.append("]");

                    prefix = ", ";
                }
            }

            builder.append(") VALUES (");

            prefix = "";
            for (Pair pair : fields) {
                String name = pair.getField().getName();

                if (!omitField(name)) {
                    builder.append(prefix);
                    builder.append("[new].[");
                    builder.append(name);
                    builder.append("]");

                    prefix = ", ";
                }
            }
            
            builder.append(")\n");
            builder.append("OUTPUT ");

            prefix = "";
            for (Pair pair : fields) {
                String name = pair.getField().getName();
                
                builder.append(prefix);
                builder.append("INSERTED.[");
                builder.append(name);
                builder.append("]");

                prefix = ", ";
            }
            
            return builder.toString();
        }
    }

    public static final String NORM_DATE = "^[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}$";
    public static final String ISO_DATE = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";
    public static final String NUMERIC = "^(\\d+|\\d+\\.\\d*|\\d*\\.\\d+)";
    
    public TSqlMerger() { }
    
    public MergeQuery merge(Table table) {
        return new MergeQuery(table); 
    }
    
    public MergeQuery merge(String table) {
        return merge(new Table(table));
    }

    public List<Pair> row(Pair... values) {
        List<Pair> row = new ArrayList<Pair>();
        Collections.addAll(row, values);
        
        return row;
    }
    
    public Pair value(Field f, Value v) {
        return new Pair(f, v);
    }

    /**
     * Do you like guessing? 
     * @param field with guessed type
     * @param o to be guessed
     * @return field-value pair
     */
    public Pair value(String field, Object o) {
        IValueFactory value = new TSqlQueryFactory().value();
        Field f = new Field(field);
        
        if (o == null) {
            f.setDataType("int");
            return new Pair(f, value.value(Value.VALUE.NULL));
        }
        
        if (o instanceof Double || o instanceof Float) {
            f.setDataType("float");
            
            if (o instanceof Double) {
                return new Pair(f, value.numeric((double) o));
            } else {
                return new Pair(f, value.numeric((float) o));
            }
        }
        
        if (o instanceof Integer || o instanceof Short || o instanceof Long) {
            f.setDataType("int");
            
            if (o instanceof Integer) {
                return new Pair(f, value.numeric((int) o));
            } else if (o instanceof Short) {
                return new Pair(f, value.numeric((int) ((short) o)));
            } else {
                return new Pair(f, value.numeric((int) ((long) o)));
            }
        }
        
        if (o instanceof Date) {
            f.setDataType("datetime");
            return new Pair(f, value.date((Date) o));
        }
        
        if (o instanceof String) {
            String s = (String) o;
            
            if (s.length() == 0 || s.toLowerCase().equals("null")) {
                f.setDataType("int");
                return new Pair(f, value.value(Value.VALUE.NULL));
            }
            
            if (s.matches(NUMERIC)) {
                f.setDataType("float");
                return new Pair(f, value.numeric(Double.parseDouble(s)));
            }
            
            if (s.matches(NORM_DATE)) {
                f.setDataType("datetime");
                
                String[] parts = s.split("\\.");
                
                return new Pair(
                        f,
                        value.function(
                                "CONVERT",
                                value.plain("datetime"),
                                value.string(parts[2] + "-" + parts[1] + "-" + parts[0] + " 00:00:00.000"),
                                value.numeric(21)
                        )
                );
            }
            
            if (s.matches(ISO_DATE)) {
                f.setDataType("datetime");
                
                return new Pair(
                        f,
                        value.function(
                                "CONVERT",
                                value.plain("datetime"),
                                value.string(s + " 00:00:00.000"),
                                value.numeric(21)
                        )
                );
            }
            
            f.setDataType("varchar(MAX)");
            return new Pair(f, value.string(s));
        }
        
        throw new BadParameterException("Cannot guess {" + o.getClass().getName() + ": " + o.toString() + "}");
    }
}