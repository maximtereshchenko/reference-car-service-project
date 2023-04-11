package com.andersenlab.carservice.application.storage.disk;

public final class CanNotWriteState extends RuntimeException {

    CanNotWriteState(Throwable cause) {
        super(cause);
    }
}
