package com.tmavn.sample.service;

import com.tmavn.sample.entity.OrderData;

public interface OrderDataService {

    Iterable<OrderData> findAll();
    
    boolean exist(String id);

    OrderData findById(String id);

    OrderData delete(String id);

    OrderData addNewOrderData(OrderData orderData);

    OrderData putOrderData(String id, OrderData orderData);

    OrderData patchOrderData(String id, OrderData orderData);
}
