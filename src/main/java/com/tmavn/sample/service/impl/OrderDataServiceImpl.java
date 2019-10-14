package com.tmavn.sample.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.NonNullAwareBeanUtils;
import com.tmavn.sample.common.RestClient;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.dto.OrderDataDTO;
import com.tmavn.sample.entity.Note;
import com.tmavn.sample.entity.OrderData;
import com.tmavn.sample.repository.OrderDataRepository;
import com.tmavn.sample.service.OrderDataService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDataServiceImpl implements OrderDataService {

    @Autowired
    private OrderDataRepository orderDataRepository;

    @Autowired
    private SimpleDateFormat dateFormat;

    @Override
    public Iterable<OrderData> findAll() {
        log.debug("IN - findAll");
        Iterable<OrderData> list = orderDataRepository.findAll();
        log.debug("get all: {}", list);
        log.debug("OUT - findAll");
        return list;
    }

    @Override
    public boolean exist(String id) {
        log.debug("IN - exist");
        boolean result = orderDataRepository.exists(id);
        log.debug("existed: {}", result);
        log.debug("OUT - exist");
        return result;
    }

    @Override
    public OrderData findById(String id) {
        log.debug("IN - findById");
        OrderData result = orderDataRepository.findOne(id);
        log.debug("get one: {}", result);
        log.debug("OUT - findById");
        return result;
    }

    @Override
    public OrderData delete(String id) {
        log.debug("IN - delete");

        // find and change state - not delete
        OrderData order = orderDataRepository.findOne(id);
        order.setState(OrderData.STATE_FAILED);
        log.debug("Update item: {}", order);

        log.debug("OUT - delete");
        return orderDataRepository.save(order);
    }

    @SuppressWarnings("unchecked")
    @Override
    public OrderData addNewOrderData(OrderData orderData) {
        log.debug("IN - addNewOrderData");
        String now = dateFormat.format(new Date());
        orderData.setOrderDate(now);
        orderData.setModifyDate(now);
        orderData.setId(null);
        log.debug("POST - Order data {}", orderData);

        // TODO using rest client to send order data to server, receive array of notes,
        // insert to order and
        // save to db, if error, return 500 to controller.
        String url = Utils.findModuleAccessUrl(Constant.ModuleResource.MODULE_OPS,
                Constant.ModuleResource.RESOURCE_CREATE_ORDER);

        Map<String, String> headers = null;
        Map<String, String> params = null;
        Map<String, String[]> queryParams = null;
        OrderData bodyData = orderData;
        ResponseEntity<?> response = RestClient.getInstance().sendPostRequestOutside(url, headers, params, queryParams,
                bodyData, String.class);

        List<Note> notes = (List<Note>) Utils.parseJson(String.valueOf(response.getBody()),
                new TypeReference<List<Note>>() {
                });
        log.debug("parse JSON: {}",response.getBody());
        for (Note note : notes) {
            note.setOrderData(orderData);
        }
        orderData.setNotes(new HashSet<>(notes));
        OrderData createdOrderData = orderDataRepository.save(orderData);
        log.debug("OUT - addNewOrderData");
        return createdOrderData;
    }

    @Override
    public OrderData putOrderData(String id, OrderData orderData) {
        log.debug("IN - putOrderData");
        OrderData oldData = orderDataRepository.findOne(orderData.getId());

        String now = dateFormat.format(new Date());
        orderData.setId(id);
        orderData.setModifyDate(now);

        BeanUtils.copyProperties(orderData, oldData, "orderDate", "modifyDate", "id");
        OrderData updatedOrderData = orderDataRepository.save(oldData);

        log.debug("PUT - updated {}", updatedOrderData);
        log.debug("OUT - putOrderData");
        return updatedOrderData;
    }

    @Override
    public OrderData patchOrderData(String id, OrderData patchData) {
        log.debug("IN - patchOrderData");
        String now = dateFormat.format(new Date());

        OrderDataDTO patchDTO = new OrderDataDTO();

        // filter properties that can be patched using DTO
        NonNullAwareBeanUtils.copyNonNullProperties(patchData, patchDTO);
        log.debug("Copying {} to {}", patchData, patchDTO);

        // get old data for patching
        OrderData oldData = orderDataRepository.findOne(id);

        // apply patch
        log.debug("Copying {} to {}", patchDTO, oldData);
        NonNullAwareBeanUtils.copyNonNullProperties(patchDTO, oldData);

        log.debug("PATCH - patched with new data {}: ", oldData);

        oldData.setModifyDate(now);

        OrderData patchedOrderData = orderDataRepository.save(oldData);
        log.debug("OUT - patchOrderData");
        return patchedOrderData;
    }

}
