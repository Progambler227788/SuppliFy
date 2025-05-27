package com.supplify.services;

import com.supplify.dto.OrderDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Order;

import java.util.List;

public interface OrderService {

    public void processOrder(OrderDto orderDto, Buyer buyer);

}
