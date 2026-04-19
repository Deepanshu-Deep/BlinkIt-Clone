package com.grocery.serviceImpl;

import com.grocery.dto.CartDTO;
import com.grocery.dto.CartItemDTO;
import com.grocery.exception.CartException;
import com.grocery.exception.ProductException;
import com.grocery.exception.UserException;
import com.grocery.model.Cart;
import com.grocery.model.CartItem;
import com.grocery.model.Product;
import com.grocery.model.User;
import com.grocery.repository.CartItemRepository;
import com.grocery.repository.CartRepository;
import com.grocery.repository.ProductRepository;
import com.grocery.repository.UserRepository;
import com.grocery.service.CartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Transactional
    @Override
    public CartDTO addItemToCart(Long userId, CartItemDTO dto) {

        logger.info("Adding item to cart for userId: {}", userId);

        User user = getUser(userId);
        Product product = getProduct(dto.getProductId());
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = findExistingCartItem(cart, product);

        if (cartItem != null) {
            updateCartItemQuantity(cartItem, dto.getQuantity());
        } else {
            createCartItem(cart, product, dto);
        }

        updateCart(cart);

        return mapper.map(cart, CartDTO.class);
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
    }

    private Product getProduct(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));
    }

    private Cart getOrCreateCart(User user) {

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private CartItem findExistingCartItem(Cart cart, Product product) {

        if (cart.getCartItems() == null) return null;

        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
    }

    private void updateCartItemQuantity(CartItem item, int quantity) {

        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);

        logger.info("Updated quantity for cartItemId: {}", item.getId());
    }

    private CartItem createCartItem(Cart cart, Product product, CartItemDTO dto) {

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());

        cartItemRepository.save(item);

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        cart.getCartItems().add(item);

        logger.info("Created new cart item for productId: {}", product.getId());

        return item;
    }

    private void updateCart(Cart cart) {

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public CartDTO getCartItemsByUserId(Long userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CartException("User not found with ID: " + userId));

            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new CartException("Cart not found for user ID: " + userId));

            List<CartItemDTO> itemDTOs = cart.getCartItems().stream().map(item -> {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found for ID: " + item.getProduct().getId()));

                CartItemDTO dto = new CartItemDTO();
                dto.setId(item.getId());
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                dto.setProductPrice(product.getPrice());
                dto.setQuantity(item.getQuantity());

                return dto;
            }).collect(Collectors.toList());

            CartDTO cartDTO = new CartDTO();
            cartDTO.setId(cart.getId());
            cartDTO.setUserId(user.getId());
            cartDTO.setCartItems(itemDTOs);

            return cartDTO;

    }

//    @Override
//    public CartDTO getCartById(Long id) {
//
//        Cart cart = cartRepository.findById(id)
//                .orElseThrow(() -> new CartException("Cart not found with ID: " + id));
//        return mapper.map(cart, CartDTO.class);
//    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(cart -> mapper.map(cart, CartDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public CartDTO updateCartItem(Long userId, CartItemDTO cartItemDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CartException("User not found with ID: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("Cart not found for user ID: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemDTO.getId())
                .orElseThrow(() -> new CartException("Cart item not found with ID: " + cartItemDTO.getId()));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartException("Cart item does not belong to this user's cart");
        }

        if (cartItemDTO.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItemRepository.save(cartItem);
        }

        cart.setUpdatedAt(java.time.LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);

        return mapper.map(savedCart, CartDTO.class);
    }


    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartException("Cart item not found"));

        Cart cart = cartItem.getCart();

        if (cart == null || !cart.getUser().getId().equals(userId)) {
            throw new CartException("Unauthorized cart item access");
        }

        cartItemRepository.delete(cartItem);
    }


    @Override
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CartException("User not found with ID: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("Cart not found for user ID: " + userId));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        cart.setUpdatedAt(java.time.LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public CartDTO addItemToCartByEmail(String email, CartItemDTO dto) {

        User user = getUserByEmail(email);

        return addItemToCart(user.getId(), dto);
    }

    @Override
    public CartDTO getCartByUserEmail(String email) {
        User user = getUserByEmail(email);

        return getCartItemsByUserId(user.getId());
    }

    @Override
    public CartDTO updateCartItemByEmail(String email, CartItemDTO dto) {
        User user = getUserByEmail(email);

        return updateCartItem(user.getId(), dto);
    }

    @Override
    public void removeCartItemByEmail(String email, Long cartItemId) {
        User user = getUserByEmail(email);

        removeCartItem(user.getId(), cartItemId);
    }

    @Override
    public void clearCartByEmail(String email) {
        User user = getUserByEmail(email);

        clearCart(user.getId());
    }


    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found with email: " + email));
    }


}
