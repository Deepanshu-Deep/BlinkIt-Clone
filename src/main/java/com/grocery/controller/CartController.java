package com.grocery.controller;

import com.grocery.dto.CartDTO;
import com.grocery.dto.CartItemDTO;
import com.grocery.service.CartService;
import com.grocery.serviceImpl.CartServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    // ADD ITEM
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @Valid @RequestBody CartItemDTO dto,
            Authentication authentication) {

        String email = authentication.getName();

        logger.info("Adding item to cart for user: {}", email);

        return ResponseEntity.ok(cartService.addItemToCartByEmail(email, dto));
    }

    // UPDATE ITEM
    @PutMapping("/items")
    public ResponseEntity<CartDTO> updateItem(
            @Valid @RequestBody CartItemDTO dto,
            Authentication authentication) {

        String email = authentication.getName();

        logger.info("Updating cart item for user: {}", email);

        return ResponseEntity.ok(cartService.updateCartItemByEmail(email, dto));
    }

    // DELETE ITEM
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        String email = authentication.getName();

        logger.info("Removing cart item {} for user: {}", cartItemId, email);

        cartService.removeCartItemByEmail(email, cartItemId);

        return ResponseEntity.noContent().build();
    }

    // GET MY CART
    @GetMapping
    public ResponseEntity<CartDTO> getMyCart(Authentication authentication) {

        String email = authentication.getName();

        logger.info("Fetching cart for user: {}", email);

        return ResponseEntity.ok(cartService.getCartByUserEmail(email));
    }

    // CLEAR CART
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {

        String email = authentication.getName();

        logger.info("Clearing cart for user: {}", email);

        cartService.clearCartByEmail(email);

        return ResponseEntity.noContent().build();
    }

    // FETCH ALL CARTS ONLY ADMIN PERMISSION
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<CartDTO>> getAllCarts() {

        logger.info("Fetching all carts (ADMIN)");

        return ResponseEntity.ok(cartService.getAllCarts());
    }
}