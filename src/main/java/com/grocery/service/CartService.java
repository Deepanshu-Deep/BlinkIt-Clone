package com.grocery.service;

import com.grocery.dto.CartDTO;
import com.grocery.dto.CartItemDTO;

import java.util.List;


public interface CartService {

    CartDTO addItemToCart(Long userId, CartItemDTO cartItemDTO);
    CartDTO getCartItemsByUserId(Long userId);
//    CartDTO getCartById(Long id);
    List<CartDTO> getAllCarts();
    CartDTO updateCartItem(Long userId, CartItemDTO cartItemDTO);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
    CartDTO addItemToCartByEmail(String email, CartItemDTO dto);
    CartDTO getCartByUserEmail(String email);
    CartDTO updateCartItemByEmail(String email, CartItemDTO dto);
    void removeCartItemByEmail(String email, Long cartItemId);
    void clearCartByEmail(String email);




}

