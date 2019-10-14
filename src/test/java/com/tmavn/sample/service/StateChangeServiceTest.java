package com.tmavn.sample.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.tmavn.sample.common.AsyncRestClient;
import com.tmavn.sample.entity.ListenerInfo;
import com.tmavn.sample.entity.OrderData;
import com.tmavn.sample.entity.StateChangeNotify;
import com.tmavn.sample.repository.StateChangeNotifyRepository;
import com.tmavn.sample.service.ListenerInfoService;
import com.tmavn.sample.service.impl.StateChangeServiceImpl;

@PrepareForTest({ StateChangeServiceImpl.class, AsyncRestClient.class })
public class StateChangeServiceTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private StateChangeServiceImpl stateChangeService = new StateChangeServiceImpl();

    @Mock
    private ListenerInfoService listenerInfoService;

    @Mock
    private StateChangeNotifyRepository stateChangeNotifyRepository;

    @Mock
    private AsyncRestClient instance;

    @InjectMocks
    private StateChangeServiceImpl mockStateChangeService;

    @Before
    public void init() {
        mockStateChangeService = PowerMockito.spy(stateChangeService);
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(AsyncRestClient.class, "instance", instance);
    }

    @Test
    public void testNotifyStateChangeSaveAndNotifySuccessful() throws Exception {
        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing");
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Processing");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock repository
        Mockito.doAnswer(new Answer<StateChangeNotify>() {
            @Override
            public StateChangeNotify answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        }).when(stateChangeNotifyRepository).save(any(StateChangeNotify.class));

        // mock state change = true to notify
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(OrderData.class),
                any(OrderData.class));

        // mock find listener by user id
        Mockito.when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        // mock check valid = true to notify
        PowerMockito.doReturn(true).when(mockStateChangeService, "isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));

        // mock notify to server
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(OrderData.class),
                any(OrderData.class));
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verifyPrivate(mockStateChangeService, times(2)).invoke("isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));
        verify(stateChangeNotifyRepository, times(2)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(2)).invoke("sendNotify", any(ListenerInfo.class),
                any(StateChangeNotify.class));
    }

    @Test
    public void testNotifyStateChangeStateNoChangeDoNothing() throws Exception {
        // mock new and old data as same
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Processing");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing");
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Processing");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock state change = false to notify
        PowerMockito.doReturn(false).when(mockStateChangeService, "isStateChanged", any(OrderData.class),
                any(OrderData.class));

        // mock find listener by user id
        Mockito.when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        // mock check valid = true to notify
        PowerMockito.doReturn(true).when(mockStateChangeService, "isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));

        // mock notify to server
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(OrderData.class),
                any(OrderData.class));
        verify(listenerInfoService, times(0)).findByUserId("userA");
        verifyPrivate(mockStateChangeService, times(0)).invoke("isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));
        verify(stateChangeNotifyRepository, times(0)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(0)).invoke("sendNotify", any(ListenerInfo.class),
                any(StateChangeNotify.class));
    }

    @Test
    public void testNotifyStateChangeNotValidToNotifyDoNothing() throws Exception {
        // mock new and old data as same
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Processing");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing");
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Processing");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock state change = true to notify
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(OrderData.class),
                any(OrderData.class));

        // mock find listener by user id
        Mockito.when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        // mock check valid = false to avoid notify
        PowerMockito.doReturn(false).when(mockStateChangeService, "isValidForNotifyStateChange",
                any(ListenerInfo.class), any(OrderData.class));

        // mock notify to server
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(OrderData.class),
                any(OrderData.class));
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verifyPrivate(mockStateChangeService, times(2)).invoke("isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));
        verify(stateChangeNotifyRepository, times(0)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(0)).invoke("sendNotify", any(ListenerInfo.class),
                any(StateChangeNotify.class));
    }

    @Test
    public void testIsStateChangedStateNoChangeReturnFalse() throws InterruptedException, ExecutionException {

        // mock new and old data that have same state
        OrderData newData = new OrderData();
        newData.setState("Scheduled");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing");
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Scheduled");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        // because no change, verify that below method is not called
        verify(listenerInfoService, times(0)).findByUserId("userA");
        verify(stateChangeNotifyRepository, times(0)).save(any(StateChangeNotify.class));
    }

    @Test
    public void testIsStateChangedStateChangedReturnTrue() throws Exception {

        // mock new and old data that have same state
        OrderData newData = new OrderData();
        newData.setState("Scheduled");
        OrderData oldData = new OrderData();
        oldData.setState("Processing");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing");
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Scheduled");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        // mock send notify do nothing
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        // because no change, verify that below method is not called
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verify(stateChangeNotifyRepository, times(1)).save(any(StateChangeNotify.class));
    }

    @Test
    public void testIsStateChangedNewDataReturnTrue() throws Exception {

        // mock new and old data that have same state
        OrderData newData = new OrderData();
        newData.setState("Scheduled");
        OrderData oldData = null;

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing");
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Scheduled");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        // mock send notify do nothing
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        // because no change, verify that below method is not called
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verify(stateChangeNotifyRepository, times(1)).save(any(StateChangeNotify.class));
    }

    @Test
    public void testIsValidForNotifyStateChangeNoStateFoundReturnTrue() throws Exception {

        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery(""); // notify all with this query
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock state change true
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(), any());

        // mock send notify do nothing
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verify(listenerInfoService, times(1)).findByUserId("userA");
        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(), any());
        verify(stateChangeNotifyRepository, times(2)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(2)).invoke("sendNotify", any(), any());
    }

    @Test
    public void testIsValidForNotifyStateChangeStateFoundButNoStatusValueReturnTrue() throws Exception {

        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state="); // notify all with this query
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock state change true
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(), any());

        // mock send notify do nothing
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(), any());
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verify(stateChangeNotifyRepository, times(2)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(2)).invoke("sendNotify", any(), any());
    }

    @Test
    public void testIsValidForNotifyStateChangeStateFoundAndMatchedStatusReturnTrue() throws Exception {

        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Processing"); // notify all with this query
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Processing");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock state change true
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(), any());

        // mock send notify do nothing
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(), any());
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verify(stateChangeNotifyRepository, times(2)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(2)).invoke("sendNotify", any(), any());
    }

    @Test
    public void testIsValidForNotifyStateChangeNoMatchedAnyStatusReturnFalse() throws Exception {

        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/ListenerProject/api/v1/listener");
        info1.setQuery("state=Completed"); // state listener not match with order
        ListenerInfo info2 = new ListenerInfo();
        info2.setCallback("http://localhost:8090/ListenerProject/api/v1/listener");
        info2.setQuery("state=Failed");

        listenerList.add(info1);
        listenerList.add(info2);

        // mock state change true
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(), any());

        // mock send notify do nothing
        PowerMockito.doNothing().when(mockStateChangeService, "sendNotify", any(), any());

        // mock find listener by user id
        when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(), any());
        verify(listenerInfoService, times(1)).findByUserId("userA");
        verify(stateChangeNotifyRepository, times(0)).save(any(StateChangeNotify.class));
        verifyPrivate(mockStateChangeService, times(0)).invoke("sendNotify", any(), any());
    }

    @SuppressWarnings("unused")
    @Test
    public void testSendNotifyRequestFailure() throws Exception {

        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/timeOutURL");
        info1.setQuery("state=");

        listenerList.add(info1);

        // mock state change true
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(), any());

        // mock check valid = true to notify
        PowerMockito.doReturn(true).when(mockStateChangeService, "isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));

        // mock find listener by user id
        Mockito.when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        Mockito.doAnswer(new Answer<StateChangeNotify>() {
            @Override
            public StateChangeNotify answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        }).when(stateChangeNotifyRepository).save(any(StateChangeNotify.class));
        // mock server with 500 internal server error for cause request failure

        // mock request failure
        ListenableFuture<ResponseEntity<?>> successFuture = new SettableListenableFuture<>();
        ResponseEntity<?> respResult = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        ((SettableListenableFuture<ResponseEntity<?>>) successFuture)
                .setException(new Exception("Internal server exception"));

        PowerMockito.doReturn(successFuture).when(instance).sendPostRequestOutside(any(), any(), any(), any(), any(),
                any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verify(listenerInfoService, times(1)).findByUserId("userA");
        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(), any());
        verify(stateChangeNotifyRepository, times(1)).save(any(StateChangeNotify.class));
    }

    @Test
    public void testSendNotifyRequestSuccess() throws Exception {

        // mock new and old data
        OrderData newData = new OrderData();
        newData.setState("Processing");
        OrderData oldData = new OrderData();
        oldData.setState("Scheduled");

        // mock listener
        List<ListenerInfo> listenerList = new ArrayList<ListenerInfo>();
        ListenerInfo info1 = new ListenerInfo();
        info1.setCallback("http://localhost:8080/successUrl");
        info1.setQuery("state=");

        listenerList.add(info1);

        // mock state change true
        PowerMockito.doReturn(true).when(mockStateChangeService, "isStateChanged", any(), any());

        // mock check valid = true to notify
        PowerMockito.doReturn(true).when(mockStateChangeService, "isValidForNotifyStateChange", any(ListenerInfo.class),
                any(OrderData.class));

        // mock find listener by user id
        Mockito.when(listenerInfoService.findByUserId("userA")).thenReturn(listenerList);

        Mockito.doAnswer(new Answer<StateChangeNotify>() {
            @Override
            public StateChangeNotify answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        }).when(stateChangeNotifyRepository).save(any(StateChangeNotify.class));
        // mock server with 500 internal server error for cause request failure

        ListenableFuture<ResponseEntity<?>> successFuture = new SettableListenableFuture<>();
        ResponseEntity<?> respResult = ResponseEntity.status(HttpStatus.CREATED).build();
        ((SettableListenableFuture<ResponseEntity<?>>) successFuture).set(respResult);

        PowerMockito.doReturn(successFuture).when(instance).sendPostRequestOutside(any(), any(), any(), any(), any(),
                any());
//        PowerMockito.doReturn(successFuture).when(instance, "sendRequestWithDataNonUTF8", any(), any(), any(), any(), any(),
//                any(), any());

        mockStateChangeService.notifyStateChange("userA", newData, oldData);

        verify(listenerInfoService, times(1)).findByUserId("userA");
        verifyPrivate(mockStateChangeService, times(1)).invoke("isStateChanged", any(), any());
        verify(stateChangeNotifyRepository, times(1)).save(any(StateChangeNotify.class));
    }
}
