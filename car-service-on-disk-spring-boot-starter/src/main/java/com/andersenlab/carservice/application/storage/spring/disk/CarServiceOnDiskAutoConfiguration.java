package com.andersenlab.carservice.application.storage.spring.disk;

import com.andersenlab.application.storage.ReadWriteLockWrapper;
import com.andersenlab.application.storage.ThreadSafeGarageSlotStore;
import com.andersenlab.application.storage.ThreadSafeOrderStore;
import com.andersenlab.application.storage.ThreadSafeRepairerStore;
import com.andersenlab.carservice.application.storage.disk.OnDiskGarageSlotStore;
import com.andersenlab.carservice.application.storage.disk.OnDiskOrderStore;
import com.andersenlab.carservice.application.storage.disk.OnDiskRepairerStore;
import com.andersenlab.carservice.application.storage.disk.StateFile;
import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@AutoConfiguration
class CarServiceOnDiskAutoConfiguration {

    private final StateFile stateFile;
    private final ReadWriteLockWrapper lock = new ReadWriteLockWrapper();

    CarServiceOnDiskAutoConfiguration(@Value("${application.state-file.path}") Path path) {
        stateFile = new StateFile(path);
    }

    @Bean
    RepairerStore repairerStore() {
        return new ThreadSafeRepairerStore(new OnDiskRepairerStore(stateFile), lock);
    }

    @Bean
    GarageSlotStore garageSlotStore() {
        return new ThreadSafeGarageSlotStore(new OnDiskGarageSlotStore(stateFile), lock);
    }

    @Bean
    OrderStore orderStore() {
        return new ThreadSafeOrderStore(new OnDiskOrderStore(stateFile), lock);
    }
}
