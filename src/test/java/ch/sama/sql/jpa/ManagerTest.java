package ch.sama.sql.jpa;

import ch.sama.sql.dialect.tsql.TSqlQueryFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ManagerTest {
    private static final TSqlQueryFactory fac = new TSqlQueryFactory();
    private static final Manager manager = new Manager(fac);

    @Entity (name = "TABLE")
    public static class Table1 {
        @PrimaryKey
        @Column(name = "ID")
        public int id;

        @Column(name = "COLUMN")
        public String s;

        public Table1(int id, String s) {
            this.id = id;
            this.s = s;
        }
    }

    @Entity (name = "TABLE")
    public static class Table2 {
        @PrimaryKey
        @AutoIncrement
        @Column(name = "ID")
        public int id;

        @Column(name = "COLUMN")
        public String s;

        public Table2(int id, String s) {
            this.id = id;
            this.s = s;
        }
    }

    @Entity (name = "TABLE")
    public static class Table3 {
        @PrimaryKey
        @Column(name = "ID")
        public int id;

        @AutoIncrement
        @Column(name = "COUNTER")
        public int counter;

        @Column(name = "COLUMN")
        public String s;

        public Table3(int id, String s) {
            this.id = id;
            this.s = s;
        }
    }

    @Entity (name = "TABLE")
    public static class Table4 {
        @PrimaryKey
        @Column(name = "ID1")
        public int id1;

        @PrimaryKey
        @Column(name = "ID2")
        public int id2;

        public Table4(int id1, int id2) {
            this.id1 = id1;
            this.id2 = id2;
        }
    }

    @Test
    public void insert() {
        assertEquals(
                "INSERT INTO [TABLE] ([ID], [COLUMN])\nVALUES (1, 'Hello')",
                manager.getInsertQuery(new Table1(1, "Hello")).getSql()
        );
    }

    @Test
    public void dontInsertAutoIncr() {
        assertEquals(
                "INSERT INTO [TABLE] ([COLUMN])\nVALUES ('Hello')",
                manager.getInsertQuery(new Table2(1, "Hello")).getSql()
        );
    }

    @Test
    public void update() {
        assertEquals(
                "UPDATE [TABLE] SET\n[COLUMN] = 'Hello'\nWHERE [TABLE].[ID] = 1",
                manager.getUpdateQuery(new Table1(1, "Hello")).getSql()
        );
    }

    @Test
    public void dontUpdateAutoIncr() {
        assertEquals(
                "UPDATE [TABLE] SET\n[COLUMN] = 'Hello'\nWHERE [TABLE].[ID] = 1",
                manager.getUpdateQuery(new Table3(1, "Hello")).getSql()
        );
    }

    @Test
    public void delete() {
        assertEquals(
                "DELETE FROM [TABLE]\nWHERE [TABLE].[ID] = 1",
                manager.getDeleteQuery(new Table1(1, "Hello")).getSql()
        );
    }

    @Test
    public void deleteWithMultipleKeys() {
        assertEquals(
                "DELETE FROM [TABLE]\nWHERE ([TABLE].[ID1] = 1 AND [TABLE].[ID2] = 2)",
                manager.getDeleteQuery(new Table4(1, 2)).getSql()
        );
    }
}
