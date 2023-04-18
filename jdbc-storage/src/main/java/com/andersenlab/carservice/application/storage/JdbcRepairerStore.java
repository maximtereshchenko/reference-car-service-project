package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.port.external.RepairerStore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

public final class JdbcRepairerStore implements RepairerStore {

    private final Database database;

    public JdbcRepairerStore(Database database) {
        this.database = database;
    }

    @Override
    public void save(RepairerEntity repairerEntity) {
        if (has(repairerEntity.id())) {
            database.update(
                    "update repairers set name= ?, status = ? where id = ?;",
                    repairerEntity.name(),
                    repairerEntity.status(),
                    repairerEntity.id()
            );
        } else {
            database.update(
                    "insert into repairers(id, name, status, is_deleted) values (?, ?, ?, false);",
                    repairerEntity.id(),
                    repairerEntity.name(),
                    repairerEntity.status()
            );
        }
    }

    @Override
    public Collection<RepairerEntity> findAllSorted(Sort sort) {
        return database.execute(findAllSortedSql(sort))
                .map(this::repairerEntity);
    }

    @Override
    public void delete(UUID id) {
        database.update("update repairers set is_deleted = true where id = ?;", id);
    }

    @Override
    public boolean has(UUID id) {
        return database.execute("select 1 from repairers where id = ? and is_deleted = false;", id)
                .isPresent();
    }

    @Override
    public boolean hasRepairerWithStatusAssigned(UUID id) {
        return database.execute(
                        "select 1 from repairers where id = ? and status = ? and is_deleted = false;",
                        id,
                        RepairerStatus.ASSIGNED
                )
                .isPresent();
    }

    @Override
    public RepairerEntity getById(UUID id) {
        return database.execute("select * from repairers where id = ? and is_deleted = false;", id)
                .map(this::repairerEntity)
                .iterator()
                .next();
    }

    private RepairerEntity repairerEntity(ResultSet resultSet) throws SQLException {
        return new RepairerEntity(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getString("name"),
                RepairerStatus.valueOf(resultSet.getString("status"))
        );
    }

    private String findAllSortedSql(Sort sort) {
        return "select * from repairers where is_deleted = false order by " + sort.name().toLowerCase(Locale.ROOT);
    }
}
