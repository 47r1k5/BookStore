package com.bookstore.Controller;

import com.bookstore.Enum.PermissionType;
import com.bookstore.Enum.ProductType;
import com.bookstore.POJOs.CartItemPOJO;
import com.bookstore.POJOs.PurchaseRequestPOJO;
import com.bookstore.POJOs.UserPOJO;
import com.bookstore.Service.CartService;
import com.bookstore.Service.PurchaseService;
import com.bookstore.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    private final PurchaseService purchaseService;

    public UserController(
            UserService userService,
            CartService cartService,
            PurchaseService purchaseService
    ) {
        this.userService = userService;
        this.cartService = cartService;
        this.purchaseService = purchaseService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserPOJO user) {
        user.setPermissions(PermissionType.USER);
        return userService.registerUser(user);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        Integer userId = userService.getUserIdByUsername(authentication.getName());
        return userService.getUserById(userId);
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCurrentUserCart(Authentication authentication) {
        Integer userId = userService.getUserIdByUsername(authentication.getName());
        return cartService.getUserCart(userId);
    }

    @PostMapping("/cart")
    public ResponseEntity<String> addToCurrentUserCart(
            Authentication authentication,
            @RequestBody CartItemPOJO cartItem
    ) {
        Integer userId = userService.getUserIdByUsername(authentication.getName());
        return cartService.addToUserCart(userId, cartItem);
    }

    @PutMapping("/cart")
    public ResponseEntity<String> updateCurrentUserCartItem(
            Authentication authentication,
            @RequestBody CartItemPOJO cartItem
    ) {
        Integer userId = userService.getUserIdByUsername(authentication.getName());
        return cartService.updateCartItemQuantity(userId, cartItem);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> deleteFromCurrentUserCart(
            Authentication authentication,
            @RequestParam String productId,
            @RequestParam ProductType prodType
    ) {
        Integer userId = userService.getUserIdByUsername(authentication.getName());
        return cartService.deleteFromUserCart(userId, productId, prodType);
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseCurrentUserCart(
            Authentication authentication,
            @RequestBody PurchaseRequestPOJO request
    ) {
        Integer userId = userService.getUserIdByUsername(authentication.getName());
        return purchaseService.purchaseUserCart(userId, request);
    }
}
