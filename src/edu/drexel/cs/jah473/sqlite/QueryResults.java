package edu.drexel.cs.jah473.sqlite;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import edu.drexel.cs.jah473.util.Input;

/**
 * Class to be extended with fields that are filled with query results.
 * Supported field types are all primitives and their wrapper classes,
 * {@link java.math.BigDecimal}, {@link java.time.LocalDate}, and
 * {@link java.time.LocalDateTime}.
 * 
 * @author Justin Horvitz
 *
 */
public abstract class QueryResults {
    private ResultSet rs;
    private int cols;
    private Field[] fields;

    /**
     * Retrieves this object's underlying result set.
     * 
     * @return the underlying result set
     */
    public ResultSet getResultSet() {
        return rs;
    }

    /**
     * Sets the declared fields to the values of the next row of the results, if
     * there is another row.
     * 
     * @return {@code true} if there is another row in the results
     */
    public final boolean nextRow() {
        try {
            boolean more = rs.next();
            if (!more) {
                rs.close();
                return false;
            }
            for (int i = 0; i < cols; i++) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set(this, rs.getObject(i + 1));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    Class<?> type = fields[i].getType();
                    try {
                        fields[i].set(this, rs.getObject(i + 1, type));
                    } catch (Exception ex) {
                    }
                    if (fields[i].get(this) != null) {
                        continue;
                    }
                    Object o = null;
                    if (type == BigDecimal.class) {
                        o = rs.getBigDecimal(i + 1);
                    } else if (type == LocalDate.class) {
                        o = rs.getObject(i + 1);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate d = LocalDate.parse(String.valueOf(o), dtf);
                        o = d;
                    } else if (type == LocalDateTime.class) {
                        o = rs.getObject(i + 1);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime dt = LocalDateTime.parse(String.valueOf(o), dtf);
                        o = dt;
                    }
                    fields[i].set(this, o);
                }
            }
        } catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            Input.close(rs);
            return false;
        }
        return true;
    }

    /**
     * Retrieves the number of columns in these results.
     * 
     * @return the number of columns in these results
     */
    public final int getCols() {
        return cols;
    }

    final void setResuts(ResultSet rs) throws SQLException {
        fields = this.getClass().getDeclaredFields();
        cols = Math.min(rs.getMetaData().getColumnCount(), fields.length);
        this.rs = rs;
    }

}
