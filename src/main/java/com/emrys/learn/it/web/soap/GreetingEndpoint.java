package com.emrys.learn.it.web.soap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.emrys.learn.it.models.GetOrderRequestIP;
import com.emrys.learn.it.models.GetOrderRequestOP;
import com.emrys.learn.it.web.exceptions.InvalidOperationException;

import lombok.extern.slf4j.Slf4j;

@Endpoint
@Slf4j
public class GreetingEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/order";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequestOP")
    @ResponsePayload
    public GetOrderRequestIP getGreeting(@RequestPayload GetOrderRequestOP request) {
        if (request.getOrderId() == null || request.getOrderId().isEmpty()) {
            throw new InvalidOperationException("Order ID cannot be null or empty", "ERR001");
        }
        log.info("Received order request for Order ID: {}", request.getOrderId());
        GetOrderRequestIP response = new GetOrderRequestIP();
        GetOrderRequestIP.Order order = new GetOrderRequestIP.Order();
        order.setOrderId(request.getOrderId());
        order.setItem("Sample Item");
        order.setQuantity(1);
        order.setPrice(99.99);
        response.setOrder(order);
        return response;
    }
}
