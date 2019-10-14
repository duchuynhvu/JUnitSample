package com.tmavn.sample.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmavn.sample.common.NonNullAwareBeanUtils;
import com.tmavn.sample.entity.ListenerInfo;
import com.tmavn.sample.repository.ListenerInfoRepository;
import com.tmavn.sample.service.ListenerInfoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ListenerInfoServiceImpl implements ListenerInfoService {

    @Autowired
    private ListenerInfoRepository listenerInfoRepository;

    @Override
    public Iterable<ListenerInfo> findAll() {
        log.debug("IN - findAll");
        List<ListenerInfo> result = listenerInfoRepository.findAll();
        log.debug("get all: {}", result);
        log.debug("OUT - findAll");
        return result;
    }

    @Override
    public boolean exist(Long id) {
        log.debug("IN - exist");
        boolean result = listenerInfoRepository.exists(id);
        log.debug("existed: {}", result);
        log.debug("OUT - exist");
        return result;
    }

    @Override
    public ListenerInfo findById(Long id) {
        log.debug("IN - findById");
        log.debug("OUT - findById");
        return listenerInfoRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("IN - delete");
        log.debug("OUT - delete");
        listenerInfoRepository.delete(id);
    }

    @Override
    public Iterable<ListenerInfo> findByUserId(String userId) {
        log.debug("IN - findByUserId");
        Iterable<ListenerInfo> result = listenerInfoRepository.findByUserId(userId);
        log.debug("Get by user id: {}",result);
        log.debug("OUT - findByUserId");
        return result;
    }

    @Override
    public ListenerInfo addNewListenerInfo(ListenerInfo listenerInfo) {
        log.debug("IN - addNewListenerInfo");
        log.debug("OUT - addNewListenerInfo");
        return listenerInfoRepository.save(listenerInfo);
    }

    @Override
    public ListenerInfo updateListenerInfo(ListenerInfo listenerInfo) {
        log.debug("IN - updateListenerInfo");
        log.debug("OUT - updateListenerInfo");
        return listenerInfoRepository.save(listenerInfo);
    }

    @Override
    public ListenerInfo patchListenerInfo(Long id, ListenerInfo listenerInfo) {

        // get old data for patching
        ListenerInfo oldData = listenerInfoRepository.findOne(id);

        // apply patch
        log.debug("Copying {} to {}", listenerInfo, oldData);
        NonNullAwareBeanUtils.copyNonNullProperties(listenerInfo, oldData);

        log.debug("PATCH - patched with new data {}: ", oldData);
        ListenerInfo patchedListenerInfo = listenerInfoRepository.save(oldData);
        return patchedListenerInfo;
    }

}
