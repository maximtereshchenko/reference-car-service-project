package com.andersenlab.carservice.application.storage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public final class Database {

    private final DataSource dataSource;
    private final ThreadLocal<Connection> connections = new ThreadLocal<>();

    public Database(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    <T> T transactionally(Supplier<T> action) {
        try (var connection = dataSource.getConnection()) {
            connections.set(connection);
            return action.get();
        } catch (SQLException e) {
            throw new CouldNotExecuteSql(e);
        } finally {
            connections.remove();
        }
    }

    void transactionally(Runnable action) {
        transactionally(() -> {
            action.run();
            return null;
        });
    }

    void update(String sql, Object... parameters) {
        if (connections.get() == null) {
            throw new NoOpenConnection();
        }
        try (var statement = connections.get().prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setString(i + 1, parameters[i].toString());
            }
            statement.execute();
        } catch (SQLException e) {
            throw new CouldNotExecuteSql(e);
        }
    }

    SqlResult execute(String sql, Object... parameters) {
        if (connections.get() == null) {
            throw new NoOpenConnection();
        }
        try {
            var statement = connections.get().prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++) {
                statement.setString(i + 1, parameters[i].toString());
            }
            return new SqlResult(statement, statement.executeQuery());
        } catch (SQLException e) {
            throw new CouldNotExecuteSql(e);
        }
    }

    static final class SqlResult {

        private final Statement statement;
        private final ResultSet resultSet;

        SqlResult(Statement statement, ResultSet resultSet) {
            this.statement = statement;
            this.resultSet = resultSet;
        }

        public boolean isPresent() {
            try (statement; resultSet) {
                return resultSet.next();
            } catch (SQLException e) {
                throw new CouldNotExecuteSql(e);
            }
        }

        <T> Collection<T> map(RowMapper<T> mapper) {
            try (statement; resultSet) {
                var list = new ArrayList<T>();
                while (resultSet.next()) {
                    list.add(mapper.apply(resultSet));
                }
                return list;
            } catch (SQLException e) {
                throw new CouldNotExecuteSql(e);
            }
        }

        @FunctionalInterface
        interface RowMapper<T> {

            T apply(ResultSet resultSet) throws SQLException;
        }
    }

    private static final class CouldNotExecuteSql extends RuntimeException {

        private CouldNotExecuteSql(Throwable cause) {
            super(cause);
        }
    }

    private static final class NoOpenConnection extends RuntimeException {}
}
