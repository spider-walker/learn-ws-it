package com.emrys.learn.it.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emrys.learn.it.web.models.Order;
import com.emrys.learn.it.web.models.OrderRequest;
import com.emrys.learn.it.web.models.OrderResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller class for handling web requests.
 */
@RestController
@RequestMapping("/v1/api/leant-it-ws/rest")
@Slf4j
public class Controller {

    @PostMapping("/hello")
    public ResponseEntity<OrderResponse> hello(@RequestBody OrderRequest request) {
        log.info("Received OrderRequest with ordeId: " + request);


        return ResponseEntity.ok(createOrderResponse(request));
    }

    private OrderResponse createOrderResponse(OrderRequest request) {
        return OrderResponse.builder()
                .order(Order.builder()
                        .id(request.getOrderId())
                        .item("Sample Item")
                        .quantity(1)
                        .price(9.99)
                        .build())
                .build();
    }

}
