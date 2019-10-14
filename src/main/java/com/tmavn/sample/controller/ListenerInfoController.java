package com.tmavn.sample.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.JsonValidation;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.entity.ListenerInfo;
import com.tmavn.sample.model.CheckResult;
import com.tmavn.sample.service.ListenerInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * Class ListenerInfoController.
 * 
 * @author ltphat1
 *
 */
@RestController
@RequestMapping("/api/v1/listener")
@Slf4j
public class ListenerInfoController {

    @Autowired
    private ListenerInfoService listenerInfoService;

    /**
     * Get all listener info.
     * 
     * @return list of listener infos.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllListenerInfo() {
        log.debug("IN - getAllListenerInfo");
        Collection<ListenerInfo> list = (Collection<ListenerInfo>) listenerInfoService.findAll();
        log.debug("GET - Get all listener info, size: {}", list.size());
        log.debug("OUT - getAllListenerInfo");
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    /**
     * Get specific listener info by id.
     * 
     * @param id id to get listener info.
     * @return listener info by given id if found, else return 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getListenerInfoById(@PathVariable(value = "id") Long id) {
        log.debug("IN - getListenerInfoById");
        ListenerInfo listenerInfo = listenerInfoService.findById(id);
        if (listenerInfo == null) {
            log.debug("GET - Not found item with id: {}", id);
            log.debug("OUT - getListenerInfoById");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.debug("GET - Get one listener info by id {}, item: {}", id, listenerInfo);
        log.debug("OUT - getListenerInfoById");
        return ResponseEntity.status(HttpStatus.OK).body(listenerInfo);
    }

    /**
     * Add new (register) listener info.
     * 
     * @param listenerInfo listener info to add new.
     * @param userId       mandatory header parameter.
     * @return registered lister info with status 200, or return status 400 if JSON
     *         invalid.
     */
    @PostMapping("")
    public ResponseEntity<?> addListenerInfo(@RequestBody String body,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId) {
        log.debug("IN - addListenerInfo");
        // Validate JSON using JSON Schema
        CheckResult validationResult = JsonValidation
                .validate(Utils.getBaseDirectory() + Constant.JsonSchema.LISTENER_INFO_POST, body);

        if (!validationResult.isSuccess()) {
            log.debug("POST- Bad request");
            log.debug("OUT - addListenerInfo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }

        ListenerInfo listenerInfo = (ListenerInfo) Utils.parseJson(body, new TypeReference<ListenerInfo>() {
        });

        if (null == listenerInfo.getUserId()) {
            log.debug("POST- Set user id to {}", userId);
            listenerInfo.setUserId(userId);
        }
        // persist
        ListenerInfo createdListenerInfo = listenerInfoService.addNewListenerInfo(listenerInfo);
        log.debug("POST- Created listener info {}", createdListenerInfo);
        log.debug("OUT - addListenerInfo");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdListenerInfo);
    }

    /**
     * Update listener info.
     * 
     * @param listenerInfo new data for update listener info.
     * @param userId       mandatory header parameter.
     * @param id           id of listener info to update.
     * @return updated listener info with status 200, or status 400 if JSON invalid.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntireListenerInfo(@RequestBody String body,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId,
            @PathVariable(value = "id") Long id) {

        log.debug("IN - updateEntireListenerInfo");

        // Validate JSON using JSON Schema
        CheckResult validationResult = JsonValidation
                .validate(Utils.getBaseDirectory() + Constant.JsonSchema.LISTENER_INFO_PUT, body);

        if (!validationResult.isSuccess()) {
            log.debug("PUT - Validate JSON failed: {}", validationResult.getMessage());
            log.debug("OUT- updateEntireListenerInfo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }

        ListenerInfo listenerInfo = (ListenerInfo) Utils.parseJson(body, new TypeReference<ListenerInfo>() {
        });
        // check if id in path and id in body is the same
        if (!listenerInfo.getId().equals(id)) {
            log.debug("PUT - Failed: Id in path param and body must be the same.");
            log.debug("OUT - updateEntireListenerInfo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CheckResult.MSG_JSON_NG);
        }

        // check exist for updating
        if (!listenerInfoService.exist(id)) {
            log.debug("PUT - Failed: Not found id {}", id);
            log.debug("OUT - updateEntireListenerInfo");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // perform update
        ListenerInfo updatedListenerInfo = listenerInfoService.updateListenerInfo(listenerInfo);
        log.debug("PUT - Updated/Replaced item {}, id {}", updatedListenerInfo, id);
        log.debug("OUT - updateEntireListenerInfo");
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedListenerInfo);
    }

    /**
     * Partial update listener info.
     * 
     * @param listenerInfo new data for update listener info.
     * @param userId       mandatory header parameter.
     * @param id           id of listener info to partial update.
     * @return updated listener info with status 201, or status 400 if JSON invalid,
     *         or status 404 if not found listener info with given id.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateListenerInfo(@RequestBody String body,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId,
            @PathVariable(value = "id") Long id) {

        log.debug("IN - partialUpdateListenerInfo");
        
        // Validate JSON using JSON Schema
        CheckResult validationResult = JsonValidation
                .validate(Utils.getBaseDirectory() + Constant.JsonSchema.LISTENER_INFO_PATCH, body);

        if (!validationResult.isSuccess()) {
            log.debug("PATCH - Validate JSON failed: {}", validationResult.getMessage());
            log.debug("OUT - partialUpdateListenerInfo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }

        ListenerInfo listenerInfo = (ListenerInfo) Utils.parseJson(body, new TypeReference<ListenerInfo>() {
        });

        // check for exist
        if (!listenerInfoService.exist(id)) {
            log.debug("PATCH - Failed: Not found id {}", id);
            log.debug("OUT - partialUpdateListenerInfo");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Perform patch
        ListenerInfo updatedOrderData = listenerInfoService.patchListenerInfo(id, listenerInfo);

        log.debug("PUT - Updated/Modified item {}, id {}", updatedOrderData, id);
        log.debug("OUT - partialUpdateListenerInfo");
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedOrderData);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListenerInfo(@PathVariable(value = "id") Long id,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId) {
        
        log.debug("IN - deleteListenerInfo");
        
        // check for exist
        if (listenerInfoService.exist(id)) {
            // Perform delete
            listenerInfoService.delete(id);
            log.debug("DELETE - Success: Deleted item id {}", id);
            log.debug("OUT - deleteListenerInfo");
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            log.debug("DELETE - Failed: Not found id {}", id);
            log.debug("OUT - deleteListenerInfo");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
