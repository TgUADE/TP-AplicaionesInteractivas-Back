package com.service;

import com.entity.Order;
import com.entity.dto.OrderRequest;
import com.repository.CategoryRepository;
import com.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

//    @Autowired
//    private UserService userService;

    @Autowired
    private ProductService productService;




    public List<Order> getOrders(){
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID orderId){
        return orderRepository.findById(orderId);
    }

    public Order createOrder(OrderRequest orderRequest){


    }

    public Order updateOrder(OrderRequest orderRequest){

    }

    public Order deleteOrder(UUID orderId){

    }


}



