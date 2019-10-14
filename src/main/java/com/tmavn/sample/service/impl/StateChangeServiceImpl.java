package com.tmavn.sample.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.tmavn.sample.common.AsyncRestClient;
import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.entity.ListenerInfo;
import com.tmavn.sample.entity.OrderData;
import com.tmavn.sample.entity.StateChangeNotify;
import com.tmavn.sample.repository.StateChangeNotifyRepository;
import com.tmavn.sample.service.ListenerInfoService;
import com.tmavn.sample.service.StateChangeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StateChangeServiceImpl implements StateChangeService {

    @Autowired
    private ListenerInfoService listenerInfoService;

    @Autowired
    private StateChangeNotifyRepository stateChangeNotifyRepository;

    /**
     * Send notification asynchronously using AsyncRestTemplate.
     * 
     * @param listenerInfo      listener with registered callback to notify.
     * @param stateChangeNotify info to notify.
     */
    @SuppressWarnings("unchecked")
    private void sendNotify(ListenerInfo listenerInfo, StateChangeNotify stateChangeNotify) {
        log.debug("IN - sendNotify");
        String url = listenerInfo.getCallback();

        log.debug("POST - Trigger state change {}", stateChangeNotify);
        Map<String, String> header = new HashMap<String, String>();
        header.put(Constant.HEADER_USER_ID, listenerInfo.getUserId());
        header.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        log.debug("POST with to {} with body: {}", url, Utils.parseObjectToJson(stateChangeNotify));

        ListenableFuture<ResponseEntity<Object>> future = (ListenableFuture<ResponseEntity<Object>>) AsyncRestClient
                .getInstance().sendPostRequestOutside(url, header, null, null, stateChangeNotify, Object.class);


        ListenableFutureCallback<ResponseEntity<Object>> callback = new ListenableFutureCallback<ResponseEntity<Object>>() {

            @Override
            public void onSuccess(ResponseEntity<Object> result) {
                log.debug("POST - status {}. Success received response: {}", result.getStatusCode(),
                        Utils.parseObjectToJson(result.getBody()));
            }

            @Override
            public void onFailure(Throwable ex) {
                log.debug("POST - Failure: {}", ex.getLocalizedMessage());
            }

        };
        future.addCallback(callback);
        log.debug("OUT - sendNotify");
    }

    @Override
    public void notifyStateChange(String userId, OrderData newData, OrderData oldData) {
        log.debug("IN - notifyStateChange");
        // if state has no changes, do nothing
        if (!isStateChanged(oldData, newData)) {
            log.debug("State no changes, do nothing");
            log.debug("OUT - notifyStateChange");
            return;
        }

        // Find all user's register listeners
        Iterable<ListenerInfo> listenerInfos = listenerInfoService.findByUserId(userId);

        for (ListenerInfo listenerInfo : listenerInfos) {
            // check if the state of order is suitable for notify
            if (isValidForNotifyStateChange(listenerInfo, newData)) {

                // notify state change
                StateChangeNotify stateChangeNotify = new StateChangeNotify();
                stateChangeNotify.setTriggerTime(new Date());
                stateChangeNotify.setTriggerType(StateChangeNotify.TYPE_STATE_CHANGE_NOTIFY);
                stateChangeNotify.setTriggerData(newData);
                StateChangeNotify createdStateChange = stateChangeNotifyRepository.save(stateChangeNotify);
                sendNotify(listenerInfo, createdStateChange);
            }
        }
        log.debug("OUT - notifyStateChange");
    }

    /**
     * Check if state is changed.
     * 
     * @param oldData old data of order data.
     * @param newData new data of order data.
     * @return true if state is changed, else false if remain the same as before.
     */
    private boolean isStateChanged(OrderData oldData, OrderData newData) {
        log.debug("IN - isStateChanged");
        String oldState;
        if (oldData == null) {
            // completely new data, add default
            oldState = "";
        } else {
            oldState = oldData.getState();
        }

        String newState = newData.getState();

        // check if state of order has no change
        if (oldState.equals(newState)) {
            log.debug("Check state - FALSE - No change, old: {}, new: {}", oldState, newState);
            log.debug("OUT - isStateChanged");
            return false;
        }
        log.debug("Check state - TRUE - State changed, old: {}, new: {}", oldState, newState);
        log.debug("OUT - isStateChanged");
        return true;
    }

    /**
     * Check if the new state of order match with state that user want to receive
     * notification (in <b>query</b>).<br>
     * <b>Query</b> will have pattern:
     * 
     * <pre>
     * query = "state=&lt;statusValue&gt;,&lt;statusValue&gt;"
     * </pre>
     * 
     * i.e: query="Scheduled,Processing".<br>
     * The following conditions will return <i>true</i> to make notify:<br>
     * - The query of @param listenerInfo have empty value. Ex: query=""<br>
     * - The query of @param listenerInfo have value but have no status value. Ex:
     * query="status="<br>
     * - The query of @param listenerInfo contains status value that user want to
     * receive notification (match with new state).<br>
     * 
     * @param listenerInfo user registered listener info
     * @param newData      new order data after it's state was changed.
     * @return true if valid for perform notify state change, else false.
     */
    private boolean isValidForNotifyStateChange(ListenerInfo listenerInfo, OrderData newData) {
        log.debug("IN - isValidForNotifyStateChange");
        String newState = newData.getState();
        log.debug("Check state - query: {}, new status: {}", listenerInfo.getQuery(), newData.getState());
        // check if have "state" key
        String query = listenerInfo.getQuery();
        if (query.contains("state=")) {

            String regex = "^(state=)(.*)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(query);
            matcher.find();
            if (!StringUtils.isEmpty(matcher.group(2))) {
                String foundString = matcher.group(2);
                log.debug("Regex found: {}", foundString);
                // check if new state match with state which user want to receive notification
                // foundString was validated with JSON schema for legal status only
                if (foundString.contains(newState)) {
                    log.debug("Check state - TRUE - Valid to notify");
                    log.debug("OUT - isValidForNotifyStateChange");
                    return true;
                } else {
                    // have status values but not contain state that user want to receive
                    // notification
                    log.debug("Check state - FALSE - No status matched");
                    log.debug("OUT - isValidForNotifyStateChange");
                    return false;
                }
            } else {
                // notify no matter what the state is
                log.debug("Check state - TRUE - Notify for all status - 'state' with no 'status'");
                log.debug("OUT - isValidForNotifyStateChange");
                return true;
            }
        } else {
            // notify no matter what the state is
            log.debug("Check state - TRUE - Notify for all status - no 'state' detected");
            log.debug("OUT - isValidForNotifyStateChange");
            return true;
        }
    }
}
