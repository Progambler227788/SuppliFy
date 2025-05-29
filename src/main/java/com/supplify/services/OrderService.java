package com.supplify.services;

import com.supplify.dto.OrderDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Order;
import com.supplify.entity.Seller;

import java.util.List;

public interface OrderService {

    public void processOrder(OrderDto orderDto, Buyer buyer);



    public List<Order> getOrdersBySellers(Seller seller, String status);

    public Order getOrderById(Long orderId);

    public void updateOrderStatus(Long orderId, String status);

}
