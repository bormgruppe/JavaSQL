package ch.sama.sql.dialect.tsql.query;

import ch.sama.sql.dbo.Field;
import ch.sama.sql.dbo.Table;
import ch.sama.sql.dialect.tsql.TSqlQueryFactory;
import ch.sama.sql.dialect.tsql.TSqlSourceFactory;
import ch.sama.sql.dialect.tsql.TSqlValueFactory;
import ch.sama.sql.query.exception.IllegalIdentifierException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InsertQueryTest {
    private static final TSqlQueryFactory sql = new TSqlQueryFactory();
    private static final TSqlValueFactory value = sql.value();
    private static final TSqlSourceFactory source = sql.source();

    @Test
    public void insertIntoString() {
        assertEquals(
                "INSERT INTO [TABLE]",
                sql.query()
                        .insert().into("TABLE")
                .getSql()
        );
    }

    @Test
    public void insertIntoTable() {
        assertEquals(
                "INSERT INTO [dbo].[TABLE]",
                sql.query()
                        .insert().into(new Table("dbo", "TABLE"))
                .getSql()
        );
    }
    
    @Test
    public void oneStringField() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD])",
                sql.query()
                        .insert().into("TABLE").columns("FIELD")
                .getSql()
        );
    }
    
    @Test
    public void multiStringField() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])",
                sql.query()
                        .insert().into("TABLE").columns("FIELD1", "FIELD2")
                .getSql()
        );    
    }

    @Test
    public void oneField() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD])",
                sql.query()
                        .insert().into("TABLE").columns(new Field("TABLE", "FIELD"))
                .getSql()
        );
    }

    @Test
    public void multiField() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])",
                sql.query()
                        .insert().into("TABLE").columns(new Field("TABLE", "FIELD1"), new Field("TABLE", "FIELD2"))
                .getSql()
        );
    }
    
    @Test
    public void insertSelect() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])\nSELECT 1, 2",
                sql.query()
                        .insert().into("TABLE").columns("FIELD1", "FIELD2")
                        .select(value.numeric(1), value.numeric(2))
                .getSql()
        );
    }
    
    @Test
    public void insertCte() {
        assertEquals(
                "WITH [CTE] AS (\nSELECT [FIELD1], [FIELD2]\nFROM [TABLE1]\n)\nINSERT INTO [TABLE2] ([F1], [F2])\nSELECT *\nFROM [CTE]",
                sql.query()
                        .with("CTE")
                        .as(
                                sql.query()
                                        .select(value.field("FIELD1"), value.field("FIELD2"))
                                        .from(source.table("TABLE1"))
                        )
                        .insert()
                        .into(new Table("TABLE2"))
                        .columns("F1", "F2")
                        .select(TSqlValueFactory.ALL)
                        .from(source.table("CTE"))
                .getSql()
        );
    }

    @Test
    public void insertOutput() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])\nOUTPUT [INSERTED].*\nSELECT 'A', 'B'",
                sql.query()
                        .insert().into("TABLE")
                        .columns("FIELD1", "FIELD2")
                        .output(value.table("INSERTED"))
                        .select(value.string("A"), value.string("B"))
                .getSql()
        );
    }

    @Test
    public void insertOutputInto() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])\nOUTPUT [INSERTED].* INTO @tmp\nSELECT 'A', 'B'",
                sql.query()
                        .insert().into("TABLE")
                        .columns("FIELD1", "FIELD2")
                        .output(value.table("INSERTED")).into("@tmp")
                        .select(value.string("A"), value.string("B"))
                .getSql()
        );
    }

    @Test (expected = IllegalIdentifierException.class)
    public void insertOutputIntoBadDestination() {
        sql.query()
                .insert().into("TABLE")
                .columns("FIELD1", "FIELD2")
                .output(value.table("INSERTED")).into("@[tmp]")
                .select(value.string("A"), value.string("B"))
        .getSql();
    }

    @Test
    public void insertValues() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])\nVALUES ('A', 'B')",
                sql.query()
                        .insert().into("TABLE")
                        .columns("FIELD1", "FIELD2")
                        .values(value.string("A"), value.string("B"))
                .getSql()
        );
    }

    @Test
    public void insertMultipleRows() {
        assertEquals(
                "INSERT INTO [TABLE] ([FIELD1], [FIELD2])\nSELECT 'A', 'B'\nUNION ALL\nSELECT 'C', 'D'",
                sql.query()
                        .insert().into("TABLE")
                        .columns("FIELD1", "FIELD2")
                    .unionAll(
                                sql.query().select(value.string("A"), value.string("B")),
                                sql.query().select(value.string("C"), value.string("D"))
                        )
                .getSql()
        );
    }
}
