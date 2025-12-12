package com.emrys.learn.it.web.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    private String id;
    private String item;
    private int quantity;
    private double price;
}
