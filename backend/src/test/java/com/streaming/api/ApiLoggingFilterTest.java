package com.streaming.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static jakarta.servlet.DispatcherType.*;
import static org.mockito.Mockito.*;

class ApiLoggingFilterTest {

    private ApiLoggingFilter filter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private ServletRequest servletRequest;

    @Mock
    private ServletResponse servletResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new ApiLoggingFilter();
    }

    @Test
    void testDoFilter_withHttpServletRequestAndResponse() throws IOException, ServletException {
        when(httpRequest.getDispatcherType()).thenReturn(REQUEST);
        when(httpRequest.getMethod()).thenReturn("GET");
        when(httpRequest.getRequestURI()).thenReturn("/api/test");
        when(httpResponse.getStatus()).thenReturn(200);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(httpRequest, httpResponse);
        verify(httpRequest, atLeastOnce()).getMethod();
        verify(httpRequest, atLeastOnce()).getRequestURI();
        verify(httpResponse).getStatus();
    }

    @Test
    void testDoFilter_withNonRequestDispatcher() throws IOException, ServletException {
        when(httpRequest.getDispatcherType()).thenReturn(FORWARD);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(httpRequest, httpResponse);
        verify(httpRequest).getDispatcherType();
        verify(httpRequest, never()).getMethod();
    }

    @Test
    void testDoFilter_withNonHttpServletRequest() throws IOException, ServletException {
        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain).doFilter(servletRequest, servletResponse);
    }

    @Test
    void testDoFilter_withIncludeDispatcher() throws IOException, ServletException {
        when(httpRequest.getDispatcherType()).thenReturn(INCLUDE);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(httpRequest, httpResponse);
        verify(httpRequest, never()).getMethod();
    }

    @Test
    void testDoFilter_withErrorDispatcher() throws IOException, ServletException {
        when(httpRequest.getDispatcherType()).thenReturn(ERROR);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(httpRequest, httpResponse);
        verify(httpRequest, never()).getMethod();
    }
}

