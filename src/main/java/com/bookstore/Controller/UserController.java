package com.bookstore.Controller;

import com.bookstore.Enum.ProductType;
import com.bookstore.POJOs.CartItemPOJO;
import com.bookstore.POJOs.UserPOJO;
import com.bookstore.Service.CartService;
import com.bookstore.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartService cartService;

    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserPOJO user) {
        return userService.registerUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Integer id,
            @RequestBody UserPOJO user
    ) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/cart")
    public ResponseEntity<?> getUserCart(@PathVariable Integer id) {
        return cartService.getUserCart(id);
    }

    @PostMapping("/{id}/cart")
    public ResponseEntity<String> addToUserCart(
            @PathVariable Integer id,
            @RequestBody CartItemPOJO cartItem
    ) {
        return cartService.addToUserCart(id, cartItem);
    }

    @PutMapping("/{id}/cart")
    public ResponseEntity<String> updateCartItemQuantity(
            @PathVariable Integer id,
            @RequestBody CartItemPOJO cartItem
    ) {
        return cartService.updateCartItemQuantity(id, cartItem);
    }

    @DeleteMapping("/{id}/cart")
    public ResponseEntity<String> deleteFromUserCart(
            @PathVariable Integer id,
            @RequestParam String productId,
            @RequestParam ProductType prodType
    ) {
        return cartService.deleteFromUserCart(id, productId, prodType);
    }
}