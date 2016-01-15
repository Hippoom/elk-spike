package com.scaleworks.analytics.mdc.filter;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class AddXRequestId2MdcFilter implements Filter {

    public static final String X_REQUEST_ID = "X-Request-Id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            String xRequestId = getXRequestIdFrom(req);

            MDC.put(X_REQUEST_ID, xRequestId);
            postMdcPut();
            res.setHeader(X_REQUEST_ID, xRequestId);
            chain.doFilter(req, response);
        } finally {
            MDC.remove(X_REQUEST_ID);
        }

    }

    /**
     * mainly for test purpose,
     * ideally, testability could be achieved by delegating {@link MDC} manipulating to other object,
     * but it's just over engineering at this moment
     */
    protected void postMdcPut() {

    }

    private String getXRequestIdFrom(HttpServletRequest req) {
        return req.getHeader(X_REQUEST_ID) == null ? UUID.randomUUID().toString() : req.getHeader(X_REQUEST_ID);
    }

    @Override
    public void destroy() {

    }
}
