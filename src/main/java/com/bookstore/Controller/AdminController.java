package com.bookstore.Controller;

import com.bookstore.POJOs.PermissionUpdatePOJO;
import com.bookstore.POJOs.PurchasePOJO;
import com.bookstore.Service.PurchaseService;
import com.bookstore.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PurchaseService purchaseService;
    private final UserService userService;

    public AdminController(
            PurchaseService purchaseService,
            UserService userService
    ) {
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<PurchasePOJO>> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @PatchMapping("/users/{id}/permission")
    public ResponseEntity<String> changeUserPermission(
            @PathVariable Integer id,
            @RequestBody PermissionUpdatePOJO request
    ) {
        return userService.changeUserPermission(id, request.getPermission());
    }
}
