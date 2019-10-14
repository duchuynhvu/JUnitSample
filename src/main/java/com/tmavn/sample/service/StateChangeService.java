package com.tmavn.sample.service;

import com.tmavn.sample.entity.OrderData;

public interface StateChangeService {

    void notifyStateChange(String userId, OrderData newData, OrderData oldData);
}
