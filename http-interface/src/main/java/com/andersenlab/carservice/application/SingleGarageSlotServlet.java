package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.DeleteGarageSlotUseCase;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

final class SingleGarageSlotServlet extends HttpServlet {

    private final DeleteGarageSlotUseCase useCase;

    SingleGarageSlotServlet(DeleteGarageSlotUseCase useCase) {
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
