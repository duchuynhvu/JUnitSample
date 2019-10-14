package com.tmavn.sample.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.RestClient;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.entity.Note;
import com.tmavn.sample.entity.OrderData;
import com.tmavn.sample.repository.OrderDataRepository;
import com.tmavn.sample.service.impl.OrderDataServiceImpl;

@PrepareForTest({ RestClient.class, Utils.class })
public class OrderDataServiceTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private RestClient restClient;

    @InjectMocks
    private OrderDataServiceImpl orderDataService = new OrderDataServiceImpl();

    @Mock
    private OrderDataRepository orderDataRepository;

    @Mock
    private SimpleDateFormat dateFormat;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Utils.class);
        Whitebox.setInternalState(RestClient.class, "instance", restClient);
    }

    @Test
    public void testFindAllGetAllSuccessful() {

        List<OrderData> orderDatas = new ArrayList<OrderData>();

        OrderData order1 = new OrderData();
        OrderData order2 = new OrderData();

        orderDatas.add(order1);
        orderDatas.add(order2);
        when(orderDataRepository.findAll()).thenReturn(orderDatas);

        Iterable<OrderData> result = orderDataService.findAll();
        Collection<?> resultList;
        resultList = (Collection<?>) result;
        assertEquals(2, resultList.size());

        verify(orderDataRepository, times(1)).findAll();
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testExistSuccessful() {

        when(orderDataRepository.exists("1")).thenReturn(true);

        boolean expected = true;
        boolean actual = orderDataService.exist("1");

        assertEquals(expected, actual);

        verify(orderDataRepository, times(1)).exists("1");
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testFindByIdSuccessful() {
        OrderData result = new OrderData();
        when(orderDataRepository.findOne("1")).thenReturn(result);

        OrderData actual = orderDataService.findById("1");
        assertEquals(result, actual);
        verify(orderDataRepository, times(1)).findOne("1");
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testFindByIdNotFound() {
        when(orderDataRepository.findOne("1")).thenReturn(null);

        OrderData actual = orderDataService.findById("1");
        assertEquals(null, actual);
        verify(orderDataRepository, times(1)).findOne("1");
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testDeleteSuccessful() {
        OrderData testOrder = new OrderData();
        testOrder.setState("Scheduled");
        OrderData deletedOrder = testOrder;
        deletedOrder.setState("Failed");
        when(orderDataRepository.findOne("1")).thenReturn(testOrder);
        when(orderDataRepository.save(testOrder)).thenReturn(deletedOrder);
        OrderData result = orderDataService.delete("1");

        assertEquals("Failed", result.getState());
        verify(orderDataRepository, times(1)).findOne("1");
        verify(orderDataRepository, times(1)).save(any(OrderData.class));
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testAddNewOrderDataSuccessful() throws Exception {
        SimpleDateFormat testFormat = new SimpleDateFormat(Constant.DATE_PATTERN);
        OrderData testAddOrder = new OrderData();
        testAddOrder.setState("Scheduled");
        testAddOrder.setDescription("Description");

        // mock method format(Date,StringBuffer,FieldPosition)
        // because format(Date) is final, cannot mock it
        when(dateFormat.format(any(), any(), any()))
                .thenReturn(new StringBuffer(testFormat.format(new java.util.Date())));

        String mockStringJson = "{}";
        String mockUrl = "http://mockUrl";
        when(Utils.findModuleAccessUrl(any(), any())).thenReturn(mockUrl);

        ResponseEntity<?> responseEntity = ResponseEntity.status(HttpStatus.OK).body(mockStringJson);

        List<Note> mockNotes = new ArrayList<Note>();
        Note note1 = new Note();
        note1.setAuthor("mockAuthor");
        note1.setDate("mockDate");
        note1.setText("mockTetxt");
        mockNotes.add(note1);
        when(Utils.parseJson(any(), any())).thenReturn(mockNotes);

        Mockito.doReturn(responseEntity).when(restClient).sendPostRequestOutside(any(), any(), any(), any(), any(),
                any());

        doAnswer(new Answer<OrderData>() {

            @Override
            public OrderData answer(InvocationOnMock invocation) throws Throwable {
                OrderData orderData = invocation.getArgument(0);
                orderData.setId("1");
                return orderData;
            }
        }).when(orderDataRepository).save(any(OrderData.class));

        OrderData createdOrderData = orderDataService.addNewOrderData(testAddOrder);

        assertNotNull(createdOrderData.getOrderDate());
        assertNotNull(createdOrderData.getModifyDate());

        verify(orderDataRepository, times(1)).save(any(OrderData.class));
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testPutOrderDataSuccessful() {
        SimpleDateFormat testFormat = new SimpleDateFormat(Constant.DATE_PATTERN);
        String now = testFormat.format(new java.util.Date());
        OrderData testUpdateOrder = new OrderData();
        testUpdateOrder.setId("1");
        testUpdateOrder.setState("Scheduled");
        testUpdateOrder.setDescription("Description");

        // mock method format(Date,StringBuffer,FieldPosition)
        // because format(Date) is final, cannot mock it
        when(dateFormat.format(any(), any(), any())).thenReturn(new StringBuffer(now));

        when(orderDataRepository.findOne("1")).thenReturn(testUpdateOrder);
        doAnswer(new Answer<OrderData>() {

            @Override
            public OrderData answer(InvocationOnMock invocation) throws Throwable {
                OrderData orderData = invocation.getArgument(0);
                return orderData;
            }
        }).when(orderDataRepository).save(any(OrderData.class));

        OrderData updatedOrderData = orderDataService.putOrderData("1", testUpdateOrder);

        assertEquals(now, updatedOrderData.getModifyDate());

        verify(orderDataRepository, times(1)).findOne("1");
        verify(orderDataRepository, times(1)).save(any(OrderData.class));
        verifyNoMoreInteractions(orderDataRepository);
    }

    @Test
    public void testPatchOrderDataSuccessful() {
        SimpleDateFormat testFormat = new SimpleDateFormat(Constant.DATE_PATTERN);
        String now = testFormat.format(new java.util.Date());
        OrderData testPatchOrder = new OrderData();
        testPatchOrder.setState("Processing");

        // mock data already in database
        OrderData oldDataToPatch = new OrderData();
        oldDataToPatch.setState("Scheduled");

        // mock method format(Date,StringBuffer,FieldPosition)
        // because format(Date) is final, cannot mock it
        when(dateFormat.format(any(), any(), any())).thenReturn(new StringBuffer(now));

        when(orderDataRepository.findOne("1")).thenReturn(oldDataToPatch);
        doAnswer(new Answer<OrderData>() {

            @Override
            public OrderData answer(InvocationOnMock invocation) throws Throwable {
                OrderData orderData = invocation.getArgument(0);
                return orderData;
            }
        }).when(orderDataRepository).save(any(OrderData.class));

        OrderData patchedOrderData = orderDataService.patchOrderData("1", testPatchOrder);

        assertEquals(now, patchedOrderData.getModifyDate());
        assertEquals("Processing", patchedOrderData.getState());
        verify(orderDataRepository, times(1)).findOne("1");
        verify(orderDataRepository, times(1)).save(any(OrderData.class));
        verifyNoMoreInteractions(orderDataRepository);
    }
}
