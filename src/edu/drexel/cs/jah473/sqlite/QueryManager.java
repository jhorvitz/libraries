package edu.drexel.cs.jah473.sqlite;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import edu.drexel.cs.jah473.util.Pair;

/**
 * Class to abstract interaction with a SQLite database via <a
 * href="http://www.oracle.com/technetwork/java/javase/jdbc/index.html"
 * >JDBC</a>.
 */
public class QueryManager implements Closeable {
    private boolean open = false;
    private Connection conn;
    private Map<Object, Pair<PreparedStatement, Class<? extends QueryResults>>> queries = new HashMap<>();

    /**
     * Constructs a new query manager connected to the given database.
     *
     * @param dbPath
     *            the path to the database
     * @throws SQLException
     *             when an SQL error occurs
     */
    public QueryManager(String dbPath) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String urlToDB = "jdbc:sqlite:" + dbPath;
        conn = DriverManager.getConnection(urlToDB);
        Statement stat = conn.createStatement();
        stat.executeUpdate("PRAGMA foreign_keys = ON;");
        open = true;
    }

    /**
     * Adds a labeled query to the QM. The identifier is a label and can be any
     * object.
     *
     * @param identifier
     *            an identifying object for the query
     * 
     * @param sqlQuery
     *            the text of the query, with question marks representing
     *            parameters
     * @param resultsClass
     *            the type of the results object to be returned
     * @return {@code true} if the query was successfully added
     */
    public boolean addQuery(Object identifier, String sqlQuery, Class<? extends QueryResults> resultsClass) {
        PreparedStatement prep;
        try {
            prep = conn.prepareStatement(sqlQuery);
        } catch (SQLException e) {
            return false;
        }
        queries.put(identifier, new Pair<>(prep, resultsClass));
        return true;
    }

    /**
     * Removes the query associated with the specified identifier, if one is
     * present.
     *
     * @param identifier
     *            the identifying object for the query
     */
    public void removeQuery(Object identifier) {
        queries.remove(identifier);
    }

    /**
     * Executes the query associated with the specified identifier.
     * 
     * @param <R>
     *            the results type of the query
     * 
     * @param identifier
     *            the identifying label for the query
     * @param params
     *            the parameters to fill into the query
     * @throws IllegalArgumentException
     *             if there is no query associated with the specified identifier
     * @return the results of the query
     */
    @SuppressWarnings("unchecked")
    public synchronized <R extends QueryResults> R executeQuery(Object identifier, Object... params) {
        Pair<PreparedStatement, Class<? extends QueryResults>> p = queries.get(identifier);
        if (p == null) {
            throw new IllegalArgumentException("no query for identifier " + identifier);
        }
        PreparedStatement prep = p.fst;
        try {
            int expectedParams = prep.getParameterMetaData().getParameterCount();
            if (expectedParams != params.length) {
                throw new IllegalArgumentException("parameter mismatch - query " + identifier + " requires "
                        + expectedParams + ", got " + params.length);
            }
            int paramNum = 1;
            for (Object param : params) {
                prep.setObject(paramNum++, param);
            }
            ResultSet rs = prep.executeQuery();
            Class<?> c = p.snd;
            Constructor<?> cons = c.getDeclaredConstructor();
            cons.setAccessible(true);
            QueryResults res = (QueryResults) cons.newInstance();
            res.setResuts(rs);
            return (R) c.cast(res);
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException
                | SecurityException | NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Closes the connection to the database.
     */
    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
        } finally {
            open = false;
        }
    }

    /**
     * Checks whether the connection to the database is open.
     *
     * @return {@code true} if the connection to the database is open
     */
    public boolean isOpen() {
        return open;
    }
}
