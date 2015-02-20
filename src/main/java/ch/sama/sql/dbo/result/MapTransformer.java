package ch.sama.sql.dbo.result;

import ch.sama.sql.query.exception.BadSqlException;

import java.io.BufferedReader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapTransformer implements IResultSetTransformer<MapResult> {
    public List<MapResult> transform(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();

        int colCount = meta.getColumnCount();
        String[] colNames = new String[colCount];
        for (int i = 0; i < colCount; ++i) {
            colNames[i] = meta.getColumnName(i + 1);
        }

        List<MapResult> result = new ArrayList<MapResult>();

        while (resultSet.next()) {
            MapResult row = new MapResult();
            
            for (int i = 0; i < colCount; ++i) {
                Object val = resultSet.getObject(i + 1);

                if (val instanceof Clob) {
                    row.put(colNames[i], clobToString((Clob)val));
                } else {
                    row.put(colNames[i], val);
                }
            }

            result.add(row);
        }

        return result;
    }

    private String clobToString(Clob clob) {
        StringBuilder builder = new StringBuilder();

        String line;
        BufferedReader bufferedReader;
        try {
            String lineBreak = "";
            bufferedReader = new BufferedReader(clob.getCharacterStream());

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(lineBreak);
                builder.append(line);

                lineBreak = "\n";
            }
        } catch (Exception e) {
            throw new BadSqlException(e);
        }

        return builder.toString();
    }
}