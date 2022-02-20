package com.kodilla.ecommerce.controller;

import com.kodilla.ecommerce.domain.Order;
import com.kodilla.ecommerce.domain.OrderDto;
import com.kodilla.ecommerce.exception.CartNotFoundException;
import com.kodilla.ecommerce.exception.OrderNotFoundException;
import com.kodilla.ecommerce.exception.UserNotFoundException;
import com.kodilla.ecommerce.mapper.OrderMapper;
import com.kodilla.ecommerce.service.DbServiceOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final DbServiceOrder dbServiceOrder;
    private final OrderMapper orderMapper;

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping(value = "/userOrders/{userId}")
    public List<OrderDto> getUserOrders(@PathVariable Long userId) throws UserNotFoundException {
        return orderMapper.mapToOrderDtoList(dbServiceOrder.getAllUserOrders(userId));
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) throws OrderNotFoundException {
       return ResponseEntity.ok(orderMapper.mapToOrderDto(dbServiceOrder.getOrder(id)));
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{idCart}")
    public ResponseEntity<OrderDto> createOrder(@PathVariable Long idCart, @RequestParam int shippingCompany) throws CartNotFoundException {
        Order order = dbServiceOrder.createOrder(idCart, shippingCompany);
        return ResponseEntity.ok(orderMapper.mapToOrderDto(order));
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PutMapping("/{id}/{shippingCompany}")
    public ResponseEntity<OrderDto> updateOrderChooseShippingCompany(@PathVariable Long id, @PathVariable int shippingCompany) throws OrderNotFoundException {
        return ResponseEntity.ok(orderMapper.mapToOrderDto(dbServiceOrder.updateOrderChooseShippingCompany(id, shippingCompany)));
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PutMapping("/send/{id}")
    public ResponseEntity<OrderDto> updateOrderIsSent(@PathVariable Long id) throws OrderNotFoundException {
        return ResponseEntity.ok(orderMapper.mapToOrderDto(dbServiceOrder.updateOrderIsSent(id)));
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PutMapping("/pay/{id}")
    public ResponseEntity<OrderDto> updateOrderIsPaid(@PathVariable Long id) throws OrderNotFoundException {
        return ResponseEntity.ok(orderMapper.mapToOrderDto(dbServiceOrder.updateOrderIsPaid(id)));
    }
}