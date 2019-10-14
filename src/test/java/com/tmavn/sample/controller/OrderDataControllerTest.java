package com.tmavn.sample.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.JsonValidation;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.controller.OrderDataController;
import com.tmavn.sample.entity.OrderData;
import com.tmavn.sample.model.CheckResult;
import com.tmavn.sample.service.OrderDataService;
import com.tmavn.sample.service.StateChangeService;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore({ "org.mockito.*" })
@PrepareForTest({ Utils.class, JsonValidation.class })
public class OrderDataControllerTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @InjectMocks
    private OrderDataController orderDataController = new OrderDataController();

    @Mock
    private OrderDataService orderDataService;

    @Mock
    private StateChangeService stateChangeService;

    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(JsonValidation.class);
        Whitebox.setInternalState(orderDataController, "orderDataService", orderDataService);
        mockMvc = MockMvcBuilders.standaloneSetup(orderDataController).build();

    }

    @After
    public void tearDown() throws Exception {
        mockMvc = null;
    }

    /* ----BEGIN GET MAPPING TEST---- */
    @Test
    public void testGetAllOrderGetAllSuccessful() throws Exception {
        List<OrderData> orderDatas = new ArrayList<>();

        OrderData order1 = new OrderData();
        OrderData order2 = new OrderData();

        order1.setId("1");
        order2.setId("2");

        order1.setDescription("Order 1");
        order2.setDescription("Order 2");

        orderDatas.add(order1);
        orderDatas.add(order2);

        when(orderDataService.findAll()).thenReturn(orderDatas);

        mockMvc.perform(get("/api/v1/order")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].description", is("Order 1"))).andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].description", is("Order 2")));

        verify(orderDataService, times(1)).findAll();
        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testGetOrderByIdGetOneExistSuccessful() throws Exception {

        OrderData order1 = new OrderData();

        order1.setId("1");

        order1.setDescription("Order 1");

        when(orderDataService.exist("1")).thenReturn(true);
        when(orderDataService.findById("1")).thenReturn(order1);

        mockMvc.perform(get("/api/v1/order/1")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is("1"))).andExpect(jsonPath("$.description", is("Order 1")));

        verify(orderDataService, times(1)).exist("1");
        verify(orderDataService, times(1)).findById("1");
        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testGetOrderByIdGetOneNotExistReturn404() throws Exception {

        OrderData order1 = new OrderData();

        order1.setId("1");

        order1.setDescription("Order 1");

        when(orderDataService.exist("1")).thenReturn(false);

        mockMvc.perform(get("/api/v1/order/1")).andExpect(status().isNotFound());

        verify(orderDataService, times(1)).exist("1");
        verify(orderDataService, times(0)).findById("1");

        verifyNoMoreInteractions(orderDataService);
    }

    /* ----BEGIN POST MAPPING TEST---- */
    @Test
    public void testAddOrderAddNewOneSuccessful() throws Exception {

        OrderData order1 = new OrderData();

        order1.setId("1");

        order1.setDescription("Order 1");

        order1.setState("Scheduled");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(order1);

        OrderData createdOrder = new OrderData();
        BeanUtils.copyProperties(order1, createdOrder);

        String now = new SimpleDateFormat(Constant.DATE_PATTERN).format(new Date());
        createdOrder.setModifyDate(now);
        createdOrder.setOrderDate(now);

        when(orderDataService.addNewOrderData(any(OrderData.class))).thenReturn(createdOrder);
        when(Utils.parseJson(any(), any())).thenReturn(order1);
        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(post("/api/v1/order").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated());

        verify(orderDataService, times(1)).addNewOrderData(any(OrderData.class));

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testAddOrderAddNewOneWhenInvalidJSONReturn400() throws Exception {

        OrderData withoutStateObj = new OrderData();
        withoutStateObj.setDescription("This object have description, but no state which required in API");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(withoutStateObj);

        CheckResult mockCheckValue = new CheckResult(false, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(post("/api/v1/order").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(orderDataService);
    }

    /* ----BEGIN PUT MAPPING TEST---- */
    @Test
    public void testUpdateEntireOrderUpdateOneSuccessful() throws Exception {

        OrderData orderData = new OrderData();
        orderData.setId("1");
        orderData.setState("Completed");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(orderData);

        when(orderDataService.exist("1")).thenReturn(true);
        when(orderDataService.putOrderData(eq("1"), any(OrderData.class))).thenReturn(orderData);
        when(Utils.parseJson(any(), any())).thenReturn(orderData);
        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(put("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated());

        verify(orderDataService, times(1)).exist("1"); // check exist
        verify(orderDataService, times(1)).findById("1"); // store old data for notify
        verify(orderDataService, times(1)).putOrderData(eq("1"), any(OrderData.class)); // update new data

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testUpdateEntireOrderUpdateOneWhenInvalidJSONReturn400() throws Exception {

        OrderData withoutStateObj = new OrderData();
        withoutStateObj.setId("1");
        withoutStateObj.setDescription("This object have description, but no state which required in API");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(withoutStateObj);

        CheckResult mockCheckValue = new CheckResult(false, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(put("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testUpdateEntireOrderUpdateOneWhenIdInPathParamNotSameAsIdInBodyReturn400() throws Exception {

        OrderData withoutStateObj = new OrderData();
        withoutStateObj.setId("1");// id in body
        withoutStateObj.setDescription("This object have id different with id in path parameter");
        withoutStateObj.setState("Scheduled");
        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(withoutStateObj);

        when(Utils.parseJson(any(), any())).thenReturn(withoutStateObj);
        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);
        mockMvc.perform(put("/api/v1/order/2").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testUpdateEntireOrderUpdateOneWhenIdNotExistReturn404() throws Exception {

        OrderData putObj = new OrderData();
        putObj.setId("1");
        putObj.setDescription("This object is valid with JSON schema, but not exist in database");
        putObj.setState("Scheduled");

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(putObj);

        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        when(Utils.parseJson(any(), any())).thenReturn(putObj);
        when(orderDataService.exist("1")).thenReturn(false); // mock not exist in database

        mockMvc.perform(put("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isNotFound());

        verify(orderDataService, times(1)).exist("1");
        verifyNoMoreInteractions(orderDataService);
    }

    /* ----BEGIN PATCH MAPPING TEST---- */

    @Test
    public void testPartialUpdateOrderPatchOneSuccessful() throws Exception {

        OrderData patchObj = new OrderData();
        patchObj.setDescription("This object is valid with JSON schema, use for patching, no id");
        patchObj.setState("Scheduled");
        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(patchObj);
        when(Utils.parseJson(any(), any())).thenReturn(patchObj);
        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);
        when(orderDataService.exist("1")).thenReturn(true);
        when(orderDataService.patchOrderData(eq("1"), any(OrderData.class))).thenReturn(patchObj);

        mockMvc.perform(patch("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isCreated());

        verify(orderDataService, times(1)).exist("1"); // check exist
        verify(orderDataService, times(1)).findById("1"); // store old data for notify
        verify(orderDataService, times(1)).patchOrderData(eq("1"), any(OrderData.class)); // patching

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testPartialUpdateOrderPatchOneWhenIdNotExistReturn404() throws Exception {

        OrderData patchObj = new OrderData();
        patchObj.setDescription("This object is valid with JSON schema, but not exist in database");
        patchObj.setState("Scheduled");
        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(patchObj);

        when(orderDataService.exist("1")).thenReturn(false); // mock not exist in database
        CheckResult mockCheckValue = new CheckResult(true, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);
        when(Utils.parseJson(any(), any())).thenReturn(patchObj);

        mockMvc.perform(patch("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isNotFound());

        verify(orderDataService, times(1)).exist("1"); // check exist

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testPartialUpdateOrderPatchOneWhenInvalidJSONReturn400() throws Exception {

        OrderData badObject = new OrderData();

        ObjectMapper mapper = new ObjectMapper();
        String jsonToTest = mapper.writeValueAsString(badObject);

        // mock check result invalid json
        CheckResult mockCheckValue = new CheckResult(false, "mock success validation");
        when(JsonValidation.validate(any(), any())).thenReturn(mockCheckValue);

        mockMvc.perform(patch("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonToTest))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(orderDataService);
    }

    /* ----BEGIN DELETE MAPPING TEST---- */
    @Test
    public void testDeleteOrderDeleteOneSuccessful() throws Exception {

        when(orderDataService.exist("1")).thenReturn(true);
        when(orderDataService.delete(eq("1"))).thenReturn(any(OrderData.class));
        mockMvc.perform(delete("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(orderDataService, times(1)).exist("1"); // check exist
        verify(orderDataService, times(1)).findById("1"); // find id to udpate state
        verify(orderDataService, times(1)).delete("1"); // deleting

        verifyNoMoreInteractions(orderDataService);
    }

    @Test
    public void testDeleteOrderDeleteOneWhenIdNotExistReturn404() throws Exception {

        when(orderDataService.exist("1")).thenReturn(false);
        mockMvc.perform(delete("/api/v1/order/1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound());

        verify(orderDataService, times(1)).exist("1"); // check exist

        verifyNoMoreInteractions(orderDataService);
    }
}
