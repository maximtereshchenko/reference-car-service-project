package com.andersenlab.carservice.application;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

final class GenericExceptionHandlingFilter extends HttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionHandlingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (IllegalArgumentException e) {
            LOG.warn("Caught generic exception", e);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
