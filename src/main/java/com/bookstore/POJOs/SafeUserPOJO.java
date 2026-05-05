package com.bookstore.POJOs;

import com.bookstore.CompositeType.PersonName;
import com.bookstore.Enum.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafeUserPOJO {
    private Integer id;
    private String username;
    private String email;
    private PersonName fullname;
    private PermissionType permissions;
    private Boolean regular;
    private UserCartPOJO cart;
}