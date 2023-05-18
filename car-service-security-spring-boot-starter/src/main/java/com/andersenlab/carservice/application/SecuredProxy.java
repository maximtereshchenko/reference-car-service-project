package com.andersenlab.carservice.application;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.function.Supplier;

public class SecuredProxy {

    @PreAuthorize("hasAnyRole(#roles)")
    <T> T secured(Supplier<T> action, String... roles) {
        return action.get();
    }

    @PreAuthorize("hasAnyRole(#roles)")
    void secured(Runnable action, String... roles) {
        action.run();
    }
}
