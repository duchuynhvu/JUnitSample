package com.tmavn.sample;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

import com.tmavn.sample.WebApplicationConfiguration;

@PrepareForTest({ WebApplicationConfiguration.class, PathMatchConfigurer.class, ContentNegotiationConfigurer.class })
public class WebApplicationConfigurationTest {

    @InjectMocks
    public WebApplicationConfiguration webApplicationConfiguration;

    @Before
    public void setUp() throws Exception {
        webApplicationConfiguration = new WebApplicationConfiguration();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfigurePathMatch_success() {
        PathMatchConfigurer configurer = PowerMockito.mock(PathMatchConfigurer.class);
        PathMatchConfigurer abc = new PathMatchConfigurer();
        PowerMockito.when(configurer.setUseSuffixPatternMatch(Mockito.anyBoolean())).thenReturn(abc);
        webApplicationConfiguration.configurePathMatch(configurer);
    }

    @Test
    public void testConfigureContentNegotiation_success() {
        ContentNegotiationConfigurer configurer = PowerMockito.mock(ContentNegotiationConfigurer.class);
        ContentNegotiationConfigurer abc = new ContentNegotiationConfigurer(null);
        PowerMockito.when(configurer.favorPathExtension(Mockito.anyBoolean())).thenReturn(abc);
        webApplicationConfiguration.configureContentNegotiation(configurer);
    }
}
