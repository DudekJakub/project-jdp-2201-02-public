package com.kodilla.ecommercee.controller;

import com.kodilla.ecommercee.domain.*;
import com.kodilla.ecommercee.mapper.*;
import com.kodilla.ecommercee.service.DbServiceCart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final DbServiceCart dbServiceCart;
    private final CartMapper cartMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCart(@RequestBody CartDto cartDto) {
        Cart cart = cartMapper.mapToCart(cartDto);
        dbServiceCart.saveCart(cart);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idCart}")
    public List<ProductDto> getProductsFromCart(@PathVariable Long idCart) {

        if (dbServiceCart.ifExist(idCart)) {
            return cartMapper.mapToProductsDto(dbServiceCart.getAllProducts(idCart));

        } else {
            return new ArrayList<>();
        }
    }

    @PutMapping("/{idCart}/{idProduct}")
    public ResponseEntity<CartDto> updateCart(@PathVariable Long idCart, @PathVariable Long idProduct) throws CartNotFoundException, ProductNotFoundException {
        return ResponseEntity.ok(cartMapper.mapToCartDto(dbServiceCart.updateCart(idCart, idProduct)));
    }

    @DeleteMapping("/{idCart}/{idProduct}")
    public ResponseEntity<Void> deleteFromCart(@PathVariable Long idCart, @PathVariable Long idProduct) throws CartNotFoundException, ProductNotFoundException {
        dbServiceCart.deleteFromCart(idCart, idProduct);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/createOrder/{idCart}")
    public ResponseEntity<Void> createOrder(@PathVariable Long idCart) {
        Order order = dbServiceCart.createOrder(idCart);
        return ResponseEntity.ok().build();
    }
}