package com.furnicraft.cart.service.impl;

import com.furnicraft.cart.client.ProductClient;
import com.furnicraft.cart.client.UserClient;
import com.furnicraft.cart.client.dto.ProductResponse;
import com.furnicraft.cart.client.dto.UserResponse;
import com.furnicraft.cart.dto.AddCartItemRequest;
import com.furnicraft.cart.dto.CartResponse;
import com.furnicraft.cart.dto.UpdateCartItemRequest;
import com.furnicraft.cart.entity.Cart;
import com.furnicraft.cart.entity.CartItem;
import com.furnicraft.cart.enums.CartStatus;
import com.furnicraft.cart.mapper.CartMapper;
import com.furnicraft.cart.repository.CartItemRepository;
import com.furnicraft.cart.repository.CartRepository;
import com.furnicraft.cart.service.CartService;
import com.furnicraft.common.dto.ApiResponse;
import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(UUID userId) {
        validateUser(userId);
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(UUID userId, AddCartItemRequest request) {
        validateUser(userId);

        ProductResponse product = getProduct(request.getProductId());
        validateProductForCart(product, request.getQuantity());

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getStock() == null || newQuantity > product.getStock()) {
                throw new BaseException(
                        "Requested quantity exceeds available stock",
                        ErrorCode.INSUFFICIENT_STOCK
                );
            }

            cartItem.setQuantity(newQuantity);
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setProductCode(product.getCode());
            cartItem.setProductName(product.getName());
            cartItem.setLineTotal(calculateLineTotal(product.getPrice(), newQuantity));

            cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .productCode(product.getCode())
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(request.getQuantity())
                    .lineTotal(calculateLineTotal(product.getPrice(), request.getQuantity()))
                    .build();

            cartItemRepository.save(newItem);
        }

        recalculateCart(cart);
        return mapToCartResponse(getCartById(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(UUID userId, UUID productId, UpdateCartItemRequest request) {
        validateUser(userId);

        Cart cart = getActiveCart(userId);
        CartItem cartItem = getCartItemByProductId(cart.getId(), productId);

        ProductResponse product = getProduct(productId);
        validateProductForCart(product, request.getQuantity());

        cartItem.setQuantity(request.getQuantity());
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setProductCode(product.getCode());
        cartItem.setProductName(product.getName());
        cartItem.setLineTotal(calculateLineTotal(product.getPrice(), request.getQuantity()));

        cartItemRepository.save(cartItem);

        recalculateCart(cart);
        return mapToCartResponse(getCartById(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(UUID userId, UUID productId) {
        validateUser(userId);

        Cart cart = getActiveCart(userId);
        CartItem cartItem = getCartItemByProductId(cart.getId(), productId);

        cartItemRepository.delete(cartItem);

        recalculateCart(cart);
        return mapToCartResponse(getCartById(cart.getId()));
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        validateUser(userId);

        Cart cart = getActiveCart(userId);

        cartItemRepository.deleteAllByCartId(cart.getId());

        cart.setTotalItems(0);
        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    private void validateUser(UUID userId) {
        ApiResponse<UserResponse> response = userClient.getUserById(userId);

        if (response == null || response.getData() == null) {
            throw new BaseException("User not found", ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private ProductResponse getProduct(UUID productId) {
        ApiResponse<ProductResponse> response = productClient.getProductById(productId);

        if (response == null || response.getData() == null) {
            throw new BaseException("Product not found", ErrorCode.RESOURCE_NOT_FOUND);
        }

        return response.getData();
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .status(CartStatus.ACTIVE)
                                .totalAmount(BigDecimal.ZERO)
                                .totalItems(0)
                                .build()
                ));
    }

    private Cart getActiveCart(UUID userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(
                        "Active cart not found",
                        ErrorCode.RESOURCE_NOT_FOUND
                ));
    }

    private Cart getCartById(UUID cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new BaseException(
                        "Cart not found",
                        ErrorCode.RESOURCE_NOT_FOUND
                ));
    }

    private CartItem getCartItemByProductId(UUID cartId, UUID productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new BaseException(
                        "Cart item not found",
                        ErrorCode.RESOURCE_NOT_FOUND
                ));
    }

    private void recalculateCart(Cart cart) {
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            totalAmount = totalAmount.add(item.getLineTotal());
            totalItems += item.getQuantity();
        }

        cart.setTotalAmount(totalAmount);
        cart.setTotalItems(totalItems);

        cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());
        return cartMapper.toCartResponse(cart, cartMapper.toCartItemResponseList(items));
    }

    private void validateProductForCart(ProductResponse product, Integer requestQuantity) {
        if (product == null) {
            throw new BaseException("Product not found", ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (product.getStatus() == null || !product.getStatus().name().equals("ACTIVE")) {
            throw new BaseException("Product is not available for cart", ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (product.getStock() == null || product.getStock() <= 0) {
            throw new BaseException("Product is out of stock", ErrorCode.INSUFFICIENT_STOCK);
        }

        if (requestQuantity > product.getStock()) {
            throw new BaseException("Requested quantity exceeds available stock", ErrorCode.INSUFFICIENT_STOCK);
        }

        if (product.getPrice() == null) {
            throw new BaseException("Product price is missing", ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private BigDecimal calculateLineTotal(BigDecimal unitPrice, Integer quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}