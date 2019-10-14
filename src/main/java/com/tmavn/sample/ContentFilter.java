/*
 * Demo project
 */
package com.tmavn.sample;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.tmavn.sample.common.Utils;
import com.tmavn.sample.enums.ModuleEnum;
import com.tmavn.sample.model.CheckResult;

/**
 * The Class FilterImpl.
 */
@WebFilter(urlPatterns = {"/*"})
public class ContentFilter implements Filter {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(ContentFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Check common request
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // set header reponse: Cache-control, Expires, Content-type
        Utils.setHeaderResponse(httpServletRequest, httpServletResponse);
        CheckResult result = Utils.checkRequestMessageCommon(httpServletRequest, ModuleEnum.SAMPLE_MODULE);
        if (!result.isSuccess()) {
            // set error code to res
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            String err = result.getMessage();

            httpServletResponse.getWriter().write(err);
            logger.error(result.getMessage());
            return;
        }

        // Set character encoding for request
        if ((httpServletRequest.getContentType() != null && httpServletRequest.getContentType().contains(";charset=")
                && !MediaType.APPLICATION_JSON_UTF8_VALUE.equals(httpServletRequest.getContentType()))
                || MediaType.APPLICATION_JSON_VALUE.equals(httpServletRequest.getContentType())) {
            httpServletRequest.setCharacterEncoding("UTF-8");
        }

        // Do filter
        chain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
