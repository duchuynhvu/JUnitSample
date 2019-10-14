package com.tmavn.sample.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmavn.sample.common.JsonValidation;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.entity.ListenerInfo;
import com.tmavn.sample.model.CheckResult;
import com.tmavn.sample.service.ListenerInfoService;


@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore({ "org.mockito.*" })
@PrepareForTest({ Utils.class, JsonValidation.class })
public class ListenerInfoControllerTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @InjectMocks
    private ListenerInfoController listenerInfoControler = new ListenerInfoController();

    @Mock
    private ListenerInfoService listenerInfoService;

    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(JsonValidation.class);
        Whitebox.setInternalState(listenerInfoControler, "listenerInfoService", listenerInfoService);
        mockMvc = MockMvcBuilders.standaloneSetup(listenerInfoControler).build();
    }

    @After
    public void tearDown() throws Exception {
        mockMvc = null;
    }

    @Test
    public void testGetAllListenerInfoGetAllSuccessful() throws Exception {

        List<ListenerInfo> mockList = new ArrayList<ListenerInfo>();

        ListenerInfo listenerInfo1 = new ListenerInfo();
        listenerInfo1.setId(1L);
        listenerInfo1.setQuery("state=Processing");

        ListenerInfo listenerInfo2 = new ListenerInfo();
        listenerInfo2.setId(2L);
        listenerInfo2.setQuery("state=Scheduled");

        mockList.add(listenerInfo1);
        mockList.add(listenerInfo2);

        when(listenerInfoService.findAll()).thenReturn(mockList);
        mockMvc.perform(get("/api/v1/listener")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].query", is("state=Processing")))
                .andExpect(jsonPath("$[1].id", is(2))).andExpect(jsonPath("$[1].query", is("state=Scheduled")));
        verify(listenerInfoService, times(1)).findAll();
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testGetListenerInfoByIdGetOneSuccessful() throws Exception {

        ListenerInfo listenerInfo = new ListenerInfo();
        listenerInfo.setId(1L);
        listenerInfo.setQuery("state=Processing");

        when(listenerInfoService.findById(1L)).thenReturn(listenerInfo);
        mockMvc.perform(get("/api/v1/listener/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.query", is("state=Processing")));

        verify(listenerInfoService, times(1)).findById(1L);
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testGetListenerInfoByIdGetOneNotFoundReturn_404() throws Exception {

        ListenerInfo listenerInfo = new ListenerInfo();
        listenerInfo.setId(1L);
        listenerInfo.setQuery("state=Processing");

        mockMvc.perform(get("/api/v1/listener/1")).andExpect(status().isNotFound());

        verify(listenerInfoService, times(1)).findById(1L);
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testAddListenerInfoAddOneSuccesful() throws Exception {

        ListenerInfo listenerInfo = new ListenerInfo();

        listenerInfo.setQuery("state=Processing");
        listenerInfo.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(listenerInfo);

        when(Utils.parseJson(any(), any())).thenReturn(listenerInfo);
        doAnswer(new Answer<ListenerInfo>() {
            @Override
            public ListenerInfo answer(InvocationOnMock invocation) throws Throwable {
                ListenerInfo listenerInfo = invocation.getArgument(0);
                return listenerInfo;
            }
        }).when(listenerInfoService).addNewListenerInfo(any(ListenerInfo.class));

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(post("/api/v1/listener").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.query", is("state=Processing")))
                .andExpect(jsonPath("$.callback", is("http://localhost:8080/ListenerProject/api/v1/listener")));
        verify(listenerInfoService, times(1)).addNewListenerInfo(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testAddListenerInfoWithUserIdInBodyAddOneSuccesful() throws Exception {

        ListenerInfo listenerInfo = new ListenerInfo();

        listenerInfo.setQuery("state=Processing");
        listenerInfo.setUserId("userA");
        listenerInfo.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(listenerInfo);

        when(Utils.parseJson(any(), any())).thenReturn(listenerInfo);
        doAnswer(new Answer<ListenerInfo>() {
            @Override
            public ListenerInfo answer(InvocationOnMock invocation) throws Throwable {
                ListenerInfo listenerInfo = invocation.getArgument(0);
                return listenerInfo;
            }
        }).when(listenerInfoService).addNewListenerInfo(any(ListenerInfo.class));

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(post("/api/v1/listener").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.query", is("state=Processing")))
                .andExpect(jsonPath("$.callback", is("http://localhost:8080/ListenerProject/api/v1/listener")));
        verify(listenerInfoService, times(1)).addNewListenerInfo(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testAddListenerInfoAddOneFailedInvalidJSONReturn_400() throws Exception {

        ListenerInfo listenerInfo = new ListenerInfo();

        listenerInfo.setQuery("state=Processing"); // this listener is not set query field, will cause 400 bad request

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(listenerInfo);

        //mock check result invalid json
        CheckResult mockCheckValue = new CheckResult(false, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(post("/api/v1/listener").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testUpdateEntireListenerInfoUpdateOneSuccessful() throws Exception {
        ListenerInfo testUpdateToListener = new ListenerInfo();
        testUpdateToListener.setQuery("state=Processing,Completed");
        testUpdateToListener.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        testUpdateToListener.setId(1L);
        testUpdateToListener.setUserId("userA");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testUpdateToListener);

        when(Utils.parseJson(any(), any())).thenReturn(testUpdateToListener);

        doAnswer(new Answer<ListenerInfo>() {
            @Override
            public ListenerInfo answer(InvocationOnMock invocation) throws Throwable {
                ListenerInfo info = invocation.getArgument(0);
                return info;
            }
        }).when(listenerInfoService).updateListenerInfo(any(ListenerInfo.class));

        when(listenerInfoService.exist(1L)).thenReturn(true);

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                put("/api/v1/listener/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is("userA")))
                .andExpect(jsonPath("$.callback", is("http://localhost:8080/ListenerProject/api/v1/listener")))
                .andExpect(jsonPath("$.query", is("state=Processing,Completed")));

        verify(listenerInfoService, times(1)).exist(1L);
        verify(listenerInfoService, times(1)).updateListenerInfo(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testUpdateEntireListenerInfoUpdateOneWhenIdNotExistReturn_404() throws Exception {
        ListenerInfo testUpdateToListener = new ListenerInfo();
        testUpdateToListener.setQuery("state=Processing,Completed");
        testUpdateToListener.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        testUpdateToListener.setId(1L); // id not exist
        testUpdateToListener.setUserId("userA");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testUpdateToListener);

        when(Utils.parseJson(any(), any())).thenReturn(testUpdateToListener);

        when(listenerInfoService.exist(1L)).thenReturn(false); // mock id not exist

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                put("/api/v1/listener/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isNotFound());

        verify(listenerInfoService, times(1)).exist(1L);
        verify(listenerInfoService, times(0)).updateListenerInfo(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testUpdateEntireListenerInfoUpdateOneWhenInvalidJSONReturn400() throws Exception {
        // mock missing query field to cause 400 bad request
        ListenerInfo testUpdateToListener = new ListenerInfo();
        testUpdateToListener.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        testUpdateToListener.setId(1L);
        testUpdateToListener.setUserId("userA");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testUpdateToListener);

        // mock check result invalid json
        CheckResult mockCheckValue = new CheckResult(false, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                put("/api/v1/listener/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verify(listenerInfoService, times(0)).exist(1L);
        verify(listenerInfoService, times(0)).updateListenerInfo(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testUpdateEntireListenerInfoUpdateOneWhenIdInPathParamNotSameAsIdInBodyReturn_400() throws Exception {
        ListenerInfo testUpdateToListener = new ListenerInfo();
        testUpdateToListener.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        testUpdateToListener.setId(1L); // id in body
        testUpdateToListener.setUserId("userA");
        testUpdateToListener.setQuery("state=Processing");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testUpdateToListener);

        when(Utils.parseJson(any(), any())).thenReturn(testUpdateToListener);

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                // mock path id = 2 to cause 400 bad request
                put("/api/v1/listener/2").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verify(listenerInfoService, times(0)).exist(1L);
        verify(listenerInfoService, times(0)).updateListenerInfo(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testPartialUpdateListenerInfoPatchOneSuccessful() throws Exception {
        ListenerInfo testPatchToListener = new ListenerInfo();
        testPatchToListener.setQuery("state=Processing");
        testPatchToListener.setUserId("userB");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testPatchToListener);

        ListenerInfo patchedObject = new ListenerInfo();
        patchedObject.setQuery(testPatchToListener.getQuery());
        patchedObject.setUserId(testPatchToListener.getUserId());
        patchedObject.setId(1L);
        patchedObject.setCallback("http://localhost:8080");
        // mock object exist
        when(listenerInfoService.exist(eq(1L))).thenReturn(true);

        when(listenerInfoService.patchListenerInfo(eq(1L), any(ListenerInfo.class))).thenReturn(patchedObject);

        when(Utils.parseJson(any(), any())).thenReturn(patchedObject);

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                patch("/api/v1/listener/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is("userB")))
                .andExpect(jsonPath("$.callback", is("http://localhost:8080")))
                .andExpect(jsonPath("$.query", is("state=Processing"))).andExpect(jsonPath("$.id", is(1)));

        verify(listenerInfoService, times(1)).exist(1L);
        verify(listenerInfoService, times(1)).patchListenerInfo(eq(1L), any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testPartialUpdateListenerInfoPatchOneIdNotExistReturn_404() throws Exception {
        ListenerInfo testPatchToListener = new ListenerInfo();
        testPatchToListener.setQuery("state=Processing");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testPatchToListener);

        // mock object not exist
        when(listenerInfoService.exist(eq(1L))).thenReturn(false);

        when(Utils.parseJson(any(), any())).thenReturn(testPatchToListener);

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                patch("/api/v1/listener/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isNotFound());

        verify(listenerInfoService, times(1)).exist(1L);
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testPartialUpdateListenerInfoPatchOneWhenInvalidJSONReturn400() throws Exception {
        ListenerInfo testPatchToListener = new ListenerInfo();
        testPatchToListener.setQuery("state=abcxyz"); // invalid json schema

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(testPatchToListener);

        // mock check result invalid json
        CheckResult mockCheckValue = new CheckResult(false, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(
                patch("/api/v1/listener/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verify(listenerInfoService, times(0)).exist(1L);
        verify(listenerInfoService, times(0)).patchListenerInfo(anyLong(), any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testDeleteListenerInfoDeleteOneSuccessful() throws Exception {

        // mock object exist
        when(listenerInfoService.exist(1L)).thenReturn(true);

        // mock delete
        doNothing().when(listenerInfoService).delete(1L);

        mockMvc.perform(delete("/api/v1/listener/1")).andExpect(status().isOk());

        verify(listenerInfoService, times(1)).exist(1L);
        verify(listenerInfoService, times(1)).delete(1L);
        verifyNoMoreInteractions(listenerInfoService);
    }

    @Test
    public void testDeleteListenerInfoDeleteOneIdNotExistReturn404() throws Exception {

        // mock object not exist
        when(listenerInfoService.exist(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/listener/1")).andExpect(status().isNotFound());

        verify(listenerInfoService, times(1)).exist(1L);
        verify(listenerInfoService, times(0)).delete(1L);
        verifyNoMoreInteractions(listenerInfoService);
    }
}
