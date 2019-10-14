package com.tmavn.sample.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmavn.sample.enums.ModuleEnum;
import com.tmavn.sample.model.CheckResult;
import com.tmavn.sample.model.ModuleAccess;

@PrepareForTest({ Utils.class, LoggerFactory.class, Logger.class, ObjectMapper.class, JsonValidation.class })
public class UtilsTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private Logger logger;

    @Mock
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBaseDirectoryOK() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = null;
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        Utils.getBaseDirectory();

        verify(logger, times(3)).debug(Mockito.anyString());
        verify(logger, times(2)).debug(Mockito.anyString(), Mockito.any(Object.class));
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testGetBaseDirectoryPropertiesNotContainBASE_DIRECTORY() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = null;
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        Properties mockBaseProperties = PowerMockito.mock(Properties.class);
        PowerMockito.whenNew(Properties.class).withNoArguments().thenReturn(mockBaseProperties);
        PowerMockito.when(mockBaseProperties.containsKey(Constant.BASE_DIRECTORY)).thenReturn(false);

        Utils.getBaseDirectory();
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testGetBaseDirectoryPropertiesContainBASE_DIRECTORYFail() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = null;
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        Properties mockBaseProperties = PowerMockito.mock(Properties.class);
        PowerMockito.whenNew(Properties.class).withNoArguments().thenReturn(mockBaseProperties);

        PowerMockito.doThrow(new IOException()).when(mockBaseProperties).load(Mockito.any(InputStream.class));

        Utils.getBaseDirectory();
    }

    @Test(expected = NullPointerException.class)
    public void testGetBaseDirectoryInputStreamCloseFail() throws IOException {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = null;
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        PowerMockito.mockStatic(Thread.class);
        ClassLoader mockClassloader = PowerMockito.mock(ClassLoader.class);
        PowerMockito.when(Thread.currentThread().getContextClassLoader()).thenReturn(mockClassloader);

        Utils.getBaseDirectory();
    }

    @Test
    public void testGetBaseDirectoryBaseDirectoryNotNull() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = "/abc";
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        Utils.getBaseDirectory();

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.any(Object.class));
    }

    @Test
    public void testAppendSlashIfNotExistPathContainsSlashAtEnd() {
        String path = "htt://localhost:8080/abc/";
        String expected = path;
        String actual = Utils.appendSlashIfNotExist(path);
        assertEquals(expected, actual);
    }

    @Test
    public void testAppendSlashIfNotExistPathNotContainsSlashAtEnd() {
        String path = "htt://localhost:8080/abc";
        String expected = path + "/";
        String actual = Utils.appendSlashIfNotExist(path);
        assertEquals(expected, actual);
    }

    @Test
    public void testSetHeaderResponseRequestMethodIsDELETE() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);
        when(mockReq.getMethod()).thenReturn(RequestMethod.DELETE.toString());
        Utils.setHeaderResponse(mockReq, mockRes);

        verify(logger, atLeast(2)).debug(Mockito.anyString());

        verify(mockRes, times(0)).setContentType(Mockito.anyString());
        verify(mockRes, times(0)).setDateHeader(Mockito.anyString(), Mockito.anyLong());
        verify(mockRes, times(0)).setHeader(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testSetHeaderResponseHttpStatusIsCREATED() {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockRes.getStatus()).thenReturn(HttpStatus.CREATED.value());
        Utils.setHeaderResponse(mockReq, mockRes);

        verify(mockRes, times(1)).setContentType(Mockito.anyString());
        verify(mockRes, times(1)).setDateHeader(Mockito.anyString(), Mockito.anyLong());
        verify(mockRes, times(1)).setHeader(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testSetHeaderResponseRequestMethodNotDELETEAndHttpStatusNotCREATED() {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockRes.getStatus()).thenReturn(HttpStatus.OK.value());
        Utils.setHeaderResponse(mockReq, mockRes);

        verify(mockRes, times(0)).setContentType(Mockito.anyString());
        verify(mockRes, times(1)).setDateHeader(Mockito.anyString(), Mockito.anyLong());
        verify(mockRes, times(1)).setHeader(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testCheckRequestMessageCommonIgnoreProcessForHomepage() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("ab");
        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);
        verify(logger, times(2)).debug(Mockito.anyString());
        verify(mockReq, times(0)).getProtocol();
    }

    @Test
    public void testCheckRequestMessageCommonProtocolIsNotHTTP_1_1() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("def");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyString());
        verify(mockReq, times(2)).getProtocol();
    }

    @Test
    public void testCheckRequestMessageCommonContentTypeAPPLICATION_JSON_VALUE() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockReq.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(3)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonContentTypeAPPLICATION_JSON_VALUE_ContentNotEmpty() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockReq.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
        when(mockReq.getContentLength()).thenReturn(2);

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(5)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonContentTypeNotAPPLICATION_JSON_VALUE_NotAPPLICATION_JSON_UTF8_VALUE_ContentNotEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockReq.getContentType()).thenReturn(MediaType.APPLICATION_ATOM_XML_VALUE);
        when(mockReq.getContentLength()).thenReturn(2);

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(3)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonContentTypeAPPLICATION_JSON_UTF8_VALUE() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockReq.getContentType()).thenReturn(MediaType.APPLICATION_JSON_UTF8_VALUE);

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(3)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonContentTypeTooLarge() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockReq.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
        when(mockReq.getContentLength()).thenReturn(-1);

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(3)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonContentLengthEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(mockReq.getContentType()).thenReturn(MediaType.APPLICATION_JSON_UTF8_VALUE);
        when(mockReq.getContentLength()).thenReturn(0);

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(3)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonMethodGETUserIdIsEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(mockReq.getHeader(Constant.HEADER_USER_ID)).thenReturn("");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonMethodGETOperatorIdIsEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(mockReq.getHeader(Constant.HEADER_MODULE_ID)).thenReturn("");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonMethodGETOperatorIdIsNotEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(mockReq.getHeader(Constant.HEADER_MODULE_ID)).thenReturn("def");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonMethodDELETEUserIdIsEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.DELETE.toString());
        when(mockReq.getHeader(Constant.HEADER_USER_ID)).thenReturn("");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonMethodDELETEOperatorIdIsEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.DELETE.toString());
        when(mockReq.getHeader(Constant.HEADER_MODULE_ID)).thenReturn("");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonMethofDELETEOperatorIdIsNotEmpty() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.DELETE.toString());
        when(mockReq.getHeader(Constant.HEADER_MODULE_ID)).thenReturn("def");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testCheckRequestMessageCommonSAMPLE_MODULE() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        when(mockReq.getRequestURI()).thenReturn("abc");
        when(mockReq.getContextPath()).thenReturn("abc");
        when(mockReq.getProtocol()).thenReturn("HTTP/1.1");
        when(mockReq.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(mockReq.getHeader(Constant.HEADER_USER_ID)).thenReturn("abc");

        Utils.checkRequestMessageCommon(mockReq, ModuleEnum.SAMPLE_MODULE);

        verify(logger, times(8)).debug(Mockito.anyString());
        verify(logger, times(4)).debug(Mockito.anyString(), Mockito.anyString());
        verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void testIsEmptyReturnFalse() throws Exception {
        String str = "abc";
        Boolean actual = Whitebox.invokeMethod(Utils.class, "isEmpty", str);
        assertFalse(actual);
    }

    @Test
    public void testIsEmptyReturnTrueInputStringIsEmpty() throws Exception {
        String str = "";
        Boolean actual = Whitebox.invokeMethod(Utils.class, "isEmpty", str);
        assertTrue(actual);
    }

    @Test
    public void testIsEmptyReturnTrueInputStringIs_Null() throws Exception {
        String str = null;
        Boolean actual = Whitebox.invokeMethod(Utils.class, "isEmpty", str);
        assertTrue(actual);
    }

    @Test
    public void testGetModuleAccessListModuleAccessListIsNotNull() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> moduleAccessList = new ArrayList<ModuleAccess>();

        Whitebox.setInternalState(Utils.class, "moduleAccessList", moduleAccessList);

        Utils.getModuleAccessList();

        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testGetModuleAccessListJsonValidationIsSuccess() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = "src/test/resources/config";
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        ModuleAccess moduleAccess = new ModuleAccess();
        moduleAccess.setModuleName("OPS");
        moduleAccess.setResourceName("CreateOrder");
        moduleAccess.setUrl("http://192.168.17.121:8080/stub/commonrest");

        List<ModuleAccess> modules = null;

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        CheckResult result = new CheckResult(true, "");
        PowerMockito.stub(PowerMockito.method(JsonValidation.class, "validate", String.class, String.class))
                .toReturn(result);

        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        list.add(moduleAccess);

        PowerMockito.stub(PowerMockito.method(Utils.class, "parseJson", String.class, TypeReference.class))
                .toReturn(list);

        Utils.getModuleAccessList();

        List<ModuleAccess> expected = new ArrayList<ModuleAccess>();
        expected.add(moduleAccess);

        List<ModuleAccess> actual = Utils.getModuleAccessList();

        assertEquals(expected.get(0).getModuleName(), actual.get(0).getModuleName());
        assertEquals(expected.get(0).getResourceName(), actual.get(0).getResourceName());
        assertEquals(expected.get(0).getUrl(), actual.get(0).getUrl());
    }

    @Test
    public void testGetModuleAccessListJsonValidationIsNotSuccess() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String baseDirectory = "src/test/resources/config";
        Whitebox.setInternalState(Utils.class, "baseDirectory", baseDirectory);

        List<ModuleAccess> modules = null;

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        CheckResult result = new CheckResult(false, "");
        PowerMockito.stub(PowerMockito.method(JsonValidation.class, "validate", String.class, String.class))
                .toReturn(result);

        Utils.getModuleAccessList();

        verify(logger, times(1)).warn(Mockito.anyString());
    }

    @Test
    public void testGetModuleAccessListJsonIsNull() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> modules = null;

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        PowerMockito.stub(PowerMockito.method(Utils.class, "readJsonFile", String.class)).toReturn(null);

        Utils.getModuleAccessList();

        verify(logger, times(1)).error(Mockito.anyString());
    }

    @Test
    public void testReadJsonFileOK() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String path = "/config/jsonSchema/module_access.json";

        Utils.readJsonFile(path);

        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testReadJsonFileFail() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        String filePath = "abc";

        PowerMockito.mockStatic(Files.class);
        when(Files.readAllBytes(Paths.get(filePath))).thenThrow(new IOException());

        Utils.readJsonFile(filePath);
        verify(logger, times(1)).error(Mockito.anyString(), Mockito.any(Throwable.class));
        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testReadJsonFile2ParamsFail() throws IOException {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Whitebox.setInternalState(Utils.class, "mapper", mapper);

        String filePath = "src/test/resources/config/jsonSchema/module_access.json";
        TypeReference<?> typeReference = new TypeReference<Object>() {
        };
        byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
        when(mapper.readValue(jsonData, typeReference)).thenThrow(new IOException());

        Utils.readJsonFile(filePath, typeReference);
        verify(logger, times(1)).error(Mockito.anyString(), Mockito.any(Throwable.class));
    }

    @Test
    public void testReadJsonFile2ParamsOK() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String filePath = "src/test/resources/config/jsonSchema/module_access.json";
        TypeReference<?> typeReference = new TypeReference<Object>() {
        };

        Utils.readJsonFile(filePath, typeReference);

        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testParseJsonOK() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        String jsonData = "{\"name\":\"abc\"}";
        TypeReference<?> typeReference = new TypeReference<Object>() {
        };
        Utils.parseJson(jsonData, typeReference);
        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testParseJsonFail() throws Exception {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Whitebox.setInternalState(Utils.class, "mapper", mapper);

        String jsonData = new String();
        TypeReference<?> typeReference = new TypeReference<Object>() {
        };

        when(mapper.readValue(jsonData, typeReference)).thenThrow(new IOException());
        Utils.parseJson(jsonData, typeReference);
        verify(logger, times(1)).error(Mockito.anyString(), Mockito.any(Throwable.class));
    }

    @Test
    public void testParseObjectToJsonObjectIsNull() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Utils.parseObjectToJson(null);
        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testParseObjectToJsonOK() throws JsonProcessingException {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Whitebox.setInternalState(Utils.class, "mapper", mapper);

        Object obj = new Object();
        Utils.parseObjectToJson(obj);
        verify(mapper, times(1)).writeValueAsString(Mockito.any(Object.class));
        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testParseObjectToJsonFail() throws JsonProcessingException {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Whitebox.setInternalState(Utils.class, "mapper", mapper);

        when(mapper.writeValueAsString(Mockito.any(Object.class))).thenThrow(JsonProcessingException.class);

        Object obj = new Object();
        Utils.parseObjectToJson(obj);

        verify(mapper, times(1)).writeValueAsString(Mockito.any(Object.class));
        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testReadPropertiesFileOK() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String filePath = "src/test/resources/config/sample.conf";
        Utils.readPropertiesFile(filePath);
        verify(logger, times(2)).debug(Mockito.anyString());
    }

    @Test
    public void testReadPropertiesFileFail() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        String filePath = "";
        Utils.readPropertiesFile(filePath);
        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(1)).error(Mockito.anyString(), Mockito.any(Throwable.class));
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_CONNECTION_DRIVER() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Utils.checkHibernateConfig(new Properties());
        verify(logger, times(1)).error("{} proprerty isn't configed", "hibernate.connection.driver_class");
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_CONNECTION_URL() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(1)).error("{} proprerty isn't configed", "hibernate.connection.url");
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_CONNECTION_USERNAME() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        p.put("hibernate.connection.url", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(1)).error("{} proprerty isn't configed", "hibernate.connection.username");
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_CONNECTION_PASSWORD() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        p.put("hibernate.connection.url", "");
        p.put("hibernate.connection.username", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(1)).error("{} proprerty isn't configed", "hibernate.connection.password");
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_DIALECT() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        p.put("hibernate.connection.url", "");
        p.put("hibernate.connection.username", "");
        p.put("hibernate.connection.password", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(1)).error("{} property isn't configed", "hibernate.dialect");
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_SHOW_SQL() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        p.put("hibernate.connection.url", "");
        p.put("hibernate.connection.username", "");
        p.put("hibernate.connection.password", "");
        p.put("hibernate.dialect", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(1)).error("{} property isn't configed", "hibernate.show_sql");
    }

    @Test
    public void testCheckHibernateConfigNotContainHIBERNATE_HBM2DDL_AUTO() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        p.put("hibernate.connection.url", "");
        p.put("hibernate.connection.username", "");
        p.put("hibernate.connection.password", "");
        p.put("hibernate.dialect", "");
        p.put("hibernate.show_sql", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(1)).error("{} property isn't configed", "hibernate.hbm2ddl.auto");
    }

    @Test
    public void testCheckHibernateConfigOK() {
        Whitebox.setInternalState(Utils.class, "logger", logger);
        Properties p = new Properties();
        p.put("hibernate.connection.driver_class", "");
        p.put("hibernate.connection.url", "");
        p.put("hibernate.connection.username", "");
        p.put("hibernate.connection.password", "");
        p.put("hibernate.dialect", "");
        p.put("hibernate.show_sql", "");
        p.put("hibernate.hbm2ddl.auto", "");
        Utils.checkHibernateConfig(p);
        verify(logger, times(2)).debug(Mockito.anyString());
        verify(logger, times(0)).error(Mockito.anyString(), Mockito.any(Object.class));
    }

    @Test
    public void testFindModuleAccessUrlModuleAccessListMduleNameAndResourceNameEqual() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> modules = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess1 = new ModuleAccess();
        moduleAccess1.setModuleName("m1");
        moduleAccess1.setResourceName("rs1");
        moduleAccess1.setUrl("url1");
        modules.add(moduleAccess1);

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        String moduleName = "m1";
        String resourceName = "rs1";
        Utils.findModuleAccessUrl(moduleName, resourceName);

        verify(logger, times(1)).debug("OUT - findModuleAccessUrl()");
    }

    @Test
    public void testFindModuleAccessUrlModuleAccessListIsNotNullModuleNameEqual() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> modules = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess1 = new ModuleAccess();
        moduleAccess1.setModuleName("m1");
        moduleAccess1.setResourceName("rs1");
        moduleAccess1.setUrl("url1");
        modules.add(moduleAccess1);

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        String moduleName = "m1";
        String resourceName = "rs2";
        Utils.findModuleAccessUrl(moduleName, resourceName);

        verify(logger, times(1)).debug("OUT - findModuleAccessUrl()");
    }

    @Test
    public void testFindModuleAccessUrlModuleAccessListIsNotNullResourceNameEqual() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> modules = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess1 = new ModuleAccess();
        moduleAccess1.setModuleName("m1");
        moduleAccess1.setResourceName("rs1");
        moduleAccess1.setUrl("url1");
        modules.add(moduleAccess1);

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        String moduleName = "m2";
        String resourceName = "rs1";
        Utils.findModuleAccessUrl(moduleName, resourceName);

        verify(logger, times(1)).debug("OUT - findModuleAccessUrl()");
    }

    @Test
    public void testFindModuleAccessUrlUrlIsNotSet() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> modules = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess1 = new ModuleAccess();
        moduleAccess1.setModuleName("m1");
        moduleAccess1.setResourceName("rs1");
        moduleAccess1.setUrl("url1");
        modules.add(moduleAccess1);

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        String moduleName = "m2";
        String resourceName = "rs2";
        Utils.findModuleAccessUrl(moduleName, resourceName);

        verify(logger, times(1)).debug("OUT - findModuleAccessUrl()");
    }

    @Test
    public void testFindModuleAccessUrlModuleAccessListIsNull() {
        Whitebox.setInternalState(Utils.class, "logger", logger);

        List<ModuleAccess> modules = null;

        Whitebox.setInternalState(Utils.class, "moduleAccessList", modules);

        String moduleName = "m1";
        String resourceName = "rs1";
        Utils.findModuleAccessUrl(moduleName, resourceName);

        verify(logger, times(1)).debug("OUT - findModuleAccessUrl()");
    }
}
