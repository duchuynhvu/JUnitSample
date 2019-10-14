package com.tmavn.sample.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
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
import com.tmavn.sample.dto.OrderDataDTO;
import com.tmavn.sample.entity.OrderData;
import com.tmavn.sample.model.CheckResult;
import com.tmavn.sample.service.OrderDataService;
import com.tmavn.sample.service.StateChangeService;

import lombok.extern.slf4j.Slf4j;

/**
 * Class Order Controller.
 * 
 * @author ltphat1
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderDataController {

    @Autowired
    private OrderDataService orderDataService;

    @Autowired
    private StateChangeService stateChangeService;

    /**
     * Get information of all orders.
     * 
     * @return 201 Created - with list of order in JSON.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllOrder() {
        log.debug("IN - getAllOrder");
        Iterable<OrderData> orderDatas = orderDataService.findAll();
        log.debug("GET - Order datas entity: {}", orderDatas);

        // create DTO for return
        List<OrderDataDTO> orderDatasDto = new ArrayList<OrderDataDTO>();
        for (OrderData orderData : orderDatas) {
            OrderDataDTO orderDataDto = new OrderDataDTO();
            BeanUtils.copyProperties(orderData, orderDataDto);
            orderDatasDto.add(orderDataDto);
        }

        ResponseEntity<List<OrderDataDTO>> response = ResponseEntity.status(HttpStatus.OK).body(orderDatasDto);
        log.debug("GET - size: {}, Order datas: {}", orderDatasDto.size(), orderDatasDto);
        log.debug("OUT - getAllOrder");
        return response;
    }

    /**
     * Get specific order.
     * 
     * @param id id of order.
     * @return 201 - Created - with the order with given id in JSON. 404 - Not found
     *         if id not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable(value = "id") String id) {
        log.debug("IN - getOrderById");
        if (!orderDataService.exist(id)) {
            log.debug("GET - Failed: Not found id {}", id);
            log.debug("OUT - getOrderById");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        OrderData orderData = orderDataService.findById(id);
        log.debug("GET - Order data entity: {}", orderData);
        OrderDataDTO orderDataDto = new OrderDataDTO();
        BeanUtils.copyProperties(orderData, orderDataDto);
        log.debug("Copy {} to Dto", orderData);
        ResponseEntity<OrderDataDTO> response = ResponseEntity.status(HttpStatus.OK).body(orderDataDto);
        log.debug("GET - Order datas: {}", orderDataDto);
        log.debug("OUT - getOrderById");
        return response;
    }

    /**
     * Add new order. If already exist, do nothing.
     * 
     * @param orderData order to add new.
     * @param userId    UserId parameter in header. Required = false for avoid make
     *                  exception in filter if not set
     * @return 201-Created - with the order have just added in JSON format, or 400
     *         Bad request with already exist message if order is already exist, or
     */
    @PostMapping("")
    public ResponseEntity<?> addOrder(@RequestBody String body,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId) {
        log.debug("IN - addOrder");
        // Validate JSON using JSON Schema
        CheckResult validationResult = JsonValidation
                .validate(Utils.getBaseDirectory() + Constant.JsonSchema.ORDER_DATA_POST, body);

        if (!validationResult.isSuccess()) {
            log.debug("POST - Bad request - JSON not valid by schema");
            log.debug("OUT - addOrder");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }

        // persist
        OrderData orderData = (OrderData) Utils.parseJson(body, new TypeReference<OrderData>() {
        });
        OrderData createdOrderData = orderDataService.addNewOrderData(orderData);

        // Perform notify state change
        OrderData newData = createdOrderData;
        OrderData oldData = null;

        stateChangeService.notifyStateChange(userId, newData, oldData);

        // Create DTO for return
        OrderDataDTO orderDataDto = new OrderDataDTO();
        BeanUtils.copyProperties(createdOrderData, orderDataDto);

        log.debug("POST - Created item {}", orderData);
        log.debug("OUT - addOrder");
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDataDto);
    }

    /**
     * Update entire order with new order (exclude id)
     * 
     * @param orderData new order data.
     * @param userId    UserId parameter in header. Required = false for avoid make
     *                  exception in filter if not set
     * @param id        id of the order to update.
     * @return 201 Create - with updated order in JSON format. or 404 Not found if
     *         order with id not exist. 400 Bad request if JSON not valid.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntireOrder(@RequestBody String body,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId,
            @PathVariable(value = "id") String id) {
        log.debug("IN - updateEntireOrder");
        // Validate JSON using JSON Schema
        CheckResult validationResult = JsonValidation
                .validate(Utils.getBaseDirectory() + Constant.JsonSchema.ORDER_DATA_PUT, body);

        if (!validationResult.isSuccess()) {
            log.debug("PUT - Bad request - JSON not valid by schema");
            log.debug("OUT - updateEntireOrder");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }

        OrderData orderData = (OrderData) Utils.parseJson(body, new TypeReference<OrderData>() {
        });

        // check if id in path and id in body is the same
        if (!orderData.getId().equals(id)) {
            log.debug("PUT - Failed: Id in header and body must be the same");
            log.debug("OUT - updateEntireOrder");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CheckResult.MSG_JSON_NG);
        }

        // check order is existed for updating
        if (orderDataService.exist(id)) {

            log.debug("PUT - Order data {}", orderData);
            OrderData oldData = orderDataService.findById(id);
            OrderData updatedOrderData = orderDataService.putOrderData(id, orderData);

            // Perform notify state change
            OrderData newData = updatedOrderData;
            stateChangeService.notifyStateChange(userId, newData, oldData);

            // Create DTO
            OrderDataDTO orderDataDto = new OrderDataDTO();
            BeanUtils.copyProperties(updatedOrderData, orderDataDto);
            log.debug("PUT - Success: Updated item {}", updatedOrderData);
            log.debug("OUT - updateEntireOrder");
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedOrderData);
        } else {
            log.debug("PUT - Failed: Not found id {}", id);
            log.debug("OUT - updateEntireOrder");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Partial update order data.
     * 
     * @param orderData new order data with new value to update
     * @param userId    UserId parameter in header. Required = false for avoid make
     *                  exception in filter if not set
     * @param id        <b>UserId<b> parameter in header.
     * @return 201 Create - with updated order in JSON format. or 404 Not found if
     *         order with id not exist.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateOrder(@RequestBody String body,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId,
            @PathVariable(value = "id") String id) {
        log.debug("IN - partialUpdateOrder");
        // Validate JSON using JSON Schema
        CheckResult validationResult = JsonValidation
                .validate(Utils.getBaseDirectory() + Constant.JsonSchema.ORDER_DATA_PATCH, body);

        if (!validationResult.isSuccess()) {
            log.debug("PATCH - Bad request - JSON not valid by schema");
            log.debug("OUT - partialUpdateOrder");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }

        OrderData orderData = (OrderData) Utils.parseJson(body, new TypeReference<OrderData>() {
        });

        if (orderDataService.exist(id)) {
            // store oldData for later use
            OrderData oldData = orderDataService.findById(id);

            // Perform patch
            OrderData updatedOrderData = orderDataService.patchOrderData(id, orderData);

            // Perform notify state change
            OrderData newData = updatedOrderData;
            stateChangeService.notifyStateChange(userId, newData, oldData);

            // Create DTO for return
            OrderDataDTO orderDataDto = new OrderDataDTO();
            BeanUtils.copyProperties(updatedOrderData, orderDataDto);
            log.debug("PATCH - Success: Updated item {}", updatedOrderData);
            log.debug("OUT - partialUpdateOrder");
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDataDto);
        } else {
            log.debug("PATCH - Failed: Not found id {}", id);
            log.debug("OUT - partialUpdateOrder");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete specific order (Not delete, just update state of the order to
     * {@link OrderData#STATE_FAILED}).
     * 
     * @param userId UserId parameter in header. Required = false for avoid make
     *               exception in filter if not set
     * @param id     id of order to delete.
     * @return 200 OK if order exist and update success, 404 Not found if id of
     *         order not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable(value = "id") String id,
            @RequestHeader(required = false, value = Constant.HEADER_USER_ID) String userId) {
        log.debug("IN - deleteOrder");
        // check for exist
        if (orderDataService.exist(id)) {
            // store oldData for later use
            OrderData oldData = orderDataService.findById(id);

            // Perform delete
            OrderData deletedData = orderDataService.delete(id);

            // Perform notify state change
            OrderData newData = deletedData;
            stateChangeService.notifyStateChange(userId, newData, oldData);
            log.debug("DELETE - Success: Deleted item id {}", id);
            log.debug("OUT - deleteOrder");
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            log.debug("DELETE - Failed: Not found id {}", id);
            log.debug("OUT - deleteOrder");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
