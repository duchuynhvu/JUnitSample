package com.tmavn.sample.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.tmavn.sample.entity.ListenerInfo;
import com.tmavn.sample.repository.ListenerInfoRepository;
import com.tmavn.sample.service.ListenerInfoService;
import com.tmavn.sample.service.impl.ListenerInfoServiceImpl;

public class ListenerInfoServiceTest {
    @InjectMocks
    private ListenerInfoService listenerInfoService;

    @Mock
    private ListenerInfoRepository listenerInfoRepository;

    @Mock
    private SimpleDateFormat dateFormat;

    @Before
    public void init() {
        listenerInfoService = new ListenerInfoServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllGetAllSuccessful() {

        List<ListenerInfo> listenerInfos = new ArrayList<ListenerInfo>();

        ListenerInfo listenerInfo1 = new ListenerInfo();
        ListenerInfo listenerInfo2 = new ListenerInfo();

        listenerInfos.add(listenerInfo1);
        listenerInfos.add(listenerInfo2);
        when(listenerInfoRepository.findAll()).thenReturn(listenerInfos);

        Iterable<ListenerInfo> result = listenerInfoService.findAll();
        Collection<?> resultList;
        resultList = (Collection<?>) result;
        assertEquals(2, resultList.size());

        verify(listenerInfoRepository, times(1)).findAll();
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testExistSuccessful() {

        when(listenerInfoRepository.exists(1L)).thenReturn(true);

        boolean expected = true;
        boolean actual = listenerInfoService.exist(1L);

        assertEquals(expected, actual);

        verify(listenerInfoRepository, times(1)).exists(1L);
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testFindByIdSuccessful() {
        ListenerInfo result = new ListenerInfo();
        when(listenerInfoRepository.findOne(1L)).thenReturn(result);

        ListenerInfo actual = listenerInfoService.findById(1L);
        assertEquals(result, actual);
        verify(listenerInfoRepository, times(1)).findOne(1L);
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testFindByIdNotFound() {
        when(listenerInfoRepository.findOne(1L)).thenReturn(null);

        ListenerInfo actual = listenerInfoService.findById(1L);
        assertEquals(null, actual);
        verify(listenerInfoRepository, times(1)).findOne(1L);
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testFindByUserIdSuccessful() {
        ListenerInfo info1 = new ListenerInfo();
        ListenerInfo info2 = new ListenerInfo();

        List<ListenerInfo> result = new ArrayList<>();
        result.add(info1);
        result.add(info2);

        when(listenerInfoRepository.findByUserId("userA")).thenReturn(result);

        Iterable<ListenerInfo> actual = listenerInfoService.findByUserId("userA");
        assertEquals(2, ((Collection<?>) actual).size());
        verify(listenerInfoRepository, times(1)).findByUserId("userA");

        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testDeleteSuccessful() {
        listenerInfoService.delete(1L);

        verify(listenerInfoRepository, times(1)).delete(1L);
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testAddNewListenerInfoSuccessful() {
        ListenerInfo testAddInfo = new ListenerInfo();
        testAddInfo.setQuery("state=Processing");
        testAddInfo.setCallback("http://localhost:8080");

        doAnswer(new Answer<ListenerInfo>() {

            @Override
            public ListenerInfo answer(InvocationOnMock invocation) throws Throwable {
                ListenerInfo info = invocation.getArgument(0);
                // mock create database
                info.setId(1L);
                return info;
            }
        }).when(listenerInfoRepository).save(any(ListenerInfo.class));

        ListenerInfo createdListenerData = listenerInfoService.addNewListenerInfo(testAddInfo);

        assertEquals(testAddInfo.getQuery(), createdListenerData.getQuery());
        assertEquals(testAddInfo.getCallback(), createdListenerData.getCallback());

        verify(listenerInfoRepository, times(1)).save(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testUpdateListenerInfoSuccessful() {
        ListenerInfo testUpdateInfo = new ListenerInfo();
        testUpdateInfo.setId(1L);
        testUpdateInfo.setCallback("http://localhost:8080");
        testUpdateInfo.setQuery("state=Processing");

        doAnswer(new Answer<ListenerInfo>() {

            @Override
            public ListenerInfo answer(InvocationOnMock invocation) throws Throwable {
                ListenerInfo info = invocation.getArgument(0);
                return info;
            }
        }).when(listenerInfoRepository).save(any(ListenerInfo.class));

        ListenerInfo updatedInfo = listenerInfoService.updateListenerInfo(testUpdateInfo);

        assertEquals(testUpdateInfo.getCallback(), updatedInfo.getCallback());
        assertEquals(testUpdateInfo.getQuery(), updatedInfo.getQuery());

        verify(listenerInfoRepository, times(1)).save(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoRepository);
    }

    @Test
    public void testPatchListenerInfoSuccessful() {
        ListenerInfo testPatchInfo = new ListenerInfo();
        testPatchInfo.setId(1L);
        testPatchInfo.setCallback("http://localhost:8080");
        testPatchInfo.setQuery("state=Processing");

        // mock data already in database
        ListenerInfo oldInfoToPatch = new ListenerInfo();
        oldInfoToPatch.setQuery("state=Scheduled");

        when(listenerInfoRepository.findOne(1L)).thenReturn(oldInfoToPatch);
        doAnswer(new Answer<ListenerInfo>() {

            @Override
            public ListenerInfo answer(InvocationOnMock invocation) throws Throwable {
                ListenerInfo info = invocation.getArgument(0);
                return info;
            }
        }).when(listenerInfoRepository).save(any(ListenerInfo.class));

        ListenerInfo patchedInfo = listenerInfoService.patchListenerInfo(1L, testPatchInfo);

        assertEquals(testPatchInfo.getCallback(), patchedInfo.getCallback());
        assertEquals(testPatchInfo.getQuery(), patchedInfo.getQuery());

        verify(listenerInfoRepository, times(1)).findOne(1L);
        verify(listenerInfoRepository, times(1)).save(any(ListenerInfo.class));
        verifyNoMoreInteractions(listenerInfoRepository);
    }
}
