package com.bookstore.POJOs;

import com.bookstore.Enum.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionUpdatePOJO {
    private PermissionType permission;
}
