package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

public final class JdbcGarageSlotStore implements GarageSlotStore {

    private final Database database;

    public JdbcGarageSlotStore(Database database) {
        this.database = database;
    }

    @Override
    public void save(GarageSlotEntity garageSlotEntity) {
        if (has(garageSlotEntity.id())) {
            database.update(
                    "update garage_slots set status = ? where id = ?;",
                    garageSlotEntity.status(),
                    garageSlotEntity.id()
            );
        } else {
            database.update(
                    "insert into garage_slots(id, status) values (?, ?);",
                    garageSlotEntity.id(),
                    garageSlotEntity.status()
            );
        }

    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return database.execute(findAllSortedSql(sort))
                .map(this::garageSlotEntity);
    }

    @Override
    public void delete(UUID id) {
        database.update("delete from garage_slots where id = ?;", id);
    }

    @Override
    public boolean has(UUID id) {
        return database.execute("select 1 from garage_slots where id = ?;", id).isPresent();
    }

    @Override
    public GarageSlotEntity getById(UUID id) {
        return database.execute("select * from garage_slots where id = ?;", id)
                .map(this::garageSlotEntity)
                .iterator()
                .next();
    }

    @Override
    public boolean hasGarageSlotWithStatusAssigned(UUID id) {
        return database.execute(
                        "select 1 from garage_slots where id = ? and status = ?;",
                        id,
                        GarageSlotStatus.ASSIGNED
                )
                .isPresent();
    }

    private GarageSlotEntity garageSlotEntity(ResultSet resultSet) throws SQLException {
        return new GarageSlotEntity(
                UUID.fromString(resultSet.getString("id")),
                GarageSlotStatus.valueOf(resultSet.getString("status"))
        );
    }

    private String findAllSortedSql(Sort sort) {
        return "select * from garage_slots order by " + sort.name().toLowerCase(Locale.ROOT);
    }
}
