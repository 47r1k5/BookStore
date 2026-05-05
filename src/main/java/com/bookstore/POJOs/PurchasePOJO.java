package com.bookstore.POJOs;

import com.bookstore.CompositeType.Address;
import com.bookstore.CompositeType.PurchaseCartItem;
import com.bookstore.Enum.PayOptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasePOJO {
    private Integer id;
    private Integer userId;
    private String username;
    private PayOptionType paymentOption;
    private Address address;
    private LocalDate purchaseDate;
    private List<PurchaseCartItem> cart;
}
