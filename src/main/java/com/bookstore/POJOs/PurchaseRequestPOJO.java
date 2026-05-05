package com.bookstore.POJOs;

import com.bookstore.CompositeType.Address;
import com.bookstore.Enum.PayOptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestPOJO {
    private PayOptionType paymentOption;
    private Address address;
}