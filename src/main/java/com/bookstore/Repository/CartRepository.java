package com.bookstore.Repository;

import com.bookstore.Entity.CartEntity;
import com.bookstore.Entity.CartId;
import com.bookstore.Entity.UserEntity;
import com.bookstore.Enum.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, CartId> {

    List<CartEntity> findByUser(UserEntity user);

    List<CartEntity> findByUserId(Integer userId);

    Optional<CartEntity> findByUserIdAndIdProductIdAndIdProdType(
            Integer userId,
            String productId,
            ProductType prodType
    );

    void deleteByIdCartId(Integer cartId);
}