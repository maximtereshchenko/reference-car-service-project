package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.exception.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

final class BusinessExceptionHandlingFilter extends HttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessExceptionHandlingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (
                GarageSlotAdditionIsDisabled |
                GarageSlotDeletionIsDisabled |
                GarageSlotIsAssigned |
                GarageSlotWasNotFound |
                GarageSlotWithSameIdExists |
                OrderHasBeenAlreadyCanceled |
                OrderHasBeenAlreadyCompleted |
                OrderHasNoGarageSlotAssigned |
                OrderHasNoRepairersAssigned |
                OrderWasNotFound |
                RepairerIsAssigned |
                RepairerWasNotFound |
                RepairerWithSameIdExists e
        ) {
            LOG.warn("Caught business related exception", e);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
