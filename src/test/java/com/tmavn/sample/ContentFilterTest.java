/*
 * Copyright©2017 NTT corp． All Rights Reserved．
 */
package com.tmavn.sample;

import static org.mockito.Mockito.mock;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.http.MediaType;

import com.tmavn.sample.ContentFilter;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.enums.ModuleEnum;
import com.tmavn.sample.model.CheckResult;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore({ "org.mockito.*" })
@PrepareForTest({ Utils.class, ContentFilter.class })
public class ContentFilterTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @InjectMocks
    public ContentFilter contentFilter;

    @Before
    public void setUp() throws Exception {
        contentFilter = new ContentFilter();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDoFilter_fail() throws Exception {
        Logger logger = mock(Logger.class);
        PowerMockito.mockStatic(Utils.class);

        // Mock arguments.
        HttpServletRequestWrapper httpServletRequestWrapper = mock(HttpServletRequestWrapper.class);
        HttpServletResponseWrapper httpServletResponseWrapper = mock(HttpServletResponseWrapper.class);
        PowerMockito.when(httpServletResponseWrapper.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        FilterChain chain = mock(FilterChain.class);

        // Mock check result.
        CheckResult checkResult = new CheckResult();
        checkResult.setSuccess(false);
        checkResult.setMessage("Test");
        PowerMockito.when(Utils.class, "checkRequestMessageCommon", Mockito.any(HttpServletRequest.class),
                Mockito.any(ModuleEnum.class)).thenReturn(checkResult);
        PowerMockito.doNothing().when(Utils.class, "setHeaderResponse", Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        // Mock logger.
        Whitebox.setInternalState(contentFilter, "logger", logger);

        contentFilter.doFilter(httpServletRequestWrapper, httpServletResponseWrapper, chain);

        Mockito.verify(logger, Mockito.atLeast(1)).error(Mockito.anyString());
    }

    @Test
    public void testDoFilter_success_withSetEncodingCase1() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        FilterChain chain = null;

        // Mock arguments.
        HttpServletRequestWrapper httpServletRequestWrapper = mock(HttpServletRequestWrapper.class);
        HttpServletResponseWrapper httpServletResponseWrapper = mock(HttpServletResponseWrapper.class);
        PowerMockito.when(httpServletRequestWrapper.getContentType()).thenReturn("application/xml;charset=UTF-8");

        chain = mock(FilterChain.class);
        Mockito.doNothing().when(chain).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        // Mock check result.
        CheckResult checkResult = new CheckResult();
        checkResult.setSuccess(true);
        checkResult.setMessage("Test");
        PowerMockito.when(Utils.class, "checkRequestMessageCommon", Mockito.any(HttpServletRequest.class),
                Mockito.any(ModuleEnum.class)).thenReturn(checkResult);
        PowerMockito.doNothing().when(Utils.class, "setHeaderResponse", Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        contentFilter.doFilter(httpServletRequestWrapper, httpServletResponseWrapper, chain);

        Mockito.verify(chain, Mockito.atLeast(1)).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));
    }

    @Test
    public void testDoFilter_success_withSetEncodingCase2() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        FilterChain chain = null;

        // Mock arguments.
        HttpServletRequestWrapper httpServletRequestWrapper = mock(HttpServletRequestWrapper.class);
        HttpServletResponseWrapper httpServletResponseWrapper = mock(HttpServletResponseWrapper.class);
        PowerMockito.when(httpServletRequestWrapper.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);

        chain = mock(FilterChain.class);
        Mockito.doNothing().when(chain).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        // Mock check result.
        CheckResult checkResult = new CheckResult();
        checkResult.setSuccess(true);
        checkResult.setMessage("Test");
        PowerMockito.when(Utils.class, "checkRequestMessageCommon", Mockito.any(HttpServletRequest.class),
                Mockito.any(ModuleEnum.class)).thenReturn(checkResult);
        PowerMockito.doNothing().when(Utils.class, "setHeaderResponse", Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        contentFilter.doFilter(httpServletRequestWrapper, httpServletResponseWrapper, chain);

        Mockito.verify(chain, Mockito.atLeast(1)).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));
    }

    @Test
    public void testDoFilter_success_withSetEncodingCase3() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        FilterChain chain = null;

        // Mock arguments.
        HttpServletRequestWrapper httpServletRequestWrapper = mock(HttpServletRequestWrapper.class);
        HttpServletResponseWrapper httpServletResponseWrapper = mock(HttpServletResponseWrapper.class);
        PowerMockito.when(httpServletRequestWrapper.getContentType()).thenReturn(MediaType.APPLICATION_JSON_UTF8_VALUE);

        chain = mock(FilterChain.class);
        Mockito.doNothing().when(chain).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        // Mock check result.
        CheckResult checkResult = new CheckResult();
        checkResult.setSuccess(true);
        checkResult.setMessage("Test");
        PowerMockito.when(Utils.class, "checkRequestMessageCommon", Mockito.any(HttpServletRequest.class),
                Mockito.any(ModuleEnum.class)).thenReturn(checkResult);
        PowerMockito.doNothing().when(Utils.class, "setHeaderResponse", Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        contentFilter.doFilter(httpServletRequestWrapper, httpServletResponseWrapper, chain);

        Mockito.verify(chain, Mockito.atLeast(1)).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));
    }

    @Test
    public void testDoFilter_success_noSetEncoding() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        FilterChain chain = null;

        // Mock arguments.
        HttpServletRequestWrapper httpServletRequestWrapper = mock(HttpServletRequestWrapper.class);
        HttpServletResponseWrapper httpServletResponseWrapper = mock(HttpServletResponseWrapper.class);
        PowerMockito.when(httpServletRequestWrapper.getContentType()).thenReturn(null);

        chain = mock(FilterChain.class);
        Mockito.doNothing().when(chain).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        // Mock check result.
        CheckResult checkResult = new CheckResult();
        checkResult.setSuccess(true);
        checkResult.setMessage("Test");
        PowerMockito.when(Utils.class, "checkRequestMessageCommon", Mockito.any(HttpServletRequest.class),
                Mockito.any(ModuleEnum.class)).thenReturn(checkResult);
        PowerMockito.doNothing().when(Utils.class, "setHeaderResponse", Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

        contentFilter.doFilter(httpServletRequestWrapper, httpServletResponseWrapper, chain);

        Mockito.verify(chain, Mockito.atLeast(1)).doFilter(Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));

    }

    @Test
    public void testDestroy_success() {
        // Method does not implement.
        contentFilter.destroy();
    }

    @Test
    public void testInit_success() throws ServletException {
        // Method does not implement.
        FilterConfig filterConfig = mock(FilterConfig.class);
        contentFilter.init(filterConfig);
    }
}
