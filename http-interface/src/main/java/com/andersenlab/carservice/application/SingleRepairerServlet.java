package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.DeleteRepairerUseCase;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

final class SingleRepairerServlet extends HttpServlet {

    private final DeleteRepairerUseCase useCase;

    SingleRepairerServlet(DeleteRepairerUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        var uri = req.getRequestURI();
        var id = UUID.fromString(uri.substring(uri.lastIndexOf('/') + 1));
        useCase.delete(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
