package com.tmavn.sample.service;

import com.tmavn.sample.entity.ListenerInfo;

public interface ListenerInfoService {
    Iterable<ListenerInfo> findAll();

    boolean exist(Long id);

    ListenerInfo findById(Long id);

    Iterable<ListenerInfo> findByUserId(String userId);
    
    void delete(Long id);
    
    ListenerInfo addNewListenerInfo(ListenerInfo listenerInfo);

    ListenerInfo updateListenerInfo(ListenerInfo listenerInfo);

    ListenerInfo patchListenerInfo(Long id, ListenerInfo listenerInfo);
}
