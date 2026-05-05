package com.bookstore.POJOs;

import com.bookstore.CompositeType.PersonName;
import com.bookstore.Enum.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPOJO {
    private Integer id;
    private String username;
    private String email;
    private String pass;
    private PersonName fullname;
    private PermissionType permissions;
    private Boolean regular;
}