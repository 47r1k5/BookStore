package com.bookstore.Service;

import com.bookstore.CompositeType.PurchaseCartItem;
import com.bookstore.Entity.BookEntity;
import com.bookstore.Entity.CartEntity;
import com.bookstore.Entity.MovieEntity;
import com.bookstore.Entity.MusicEntity;
import com.bookstore.Entity.PurchaseEntity;
import com.bookstore.Entity.UserEntity;
import com.bookstore.Enum.ProductType;
import com.bookstore.POJOs.PurchasePOJO;
import com.bookstore.POJOs.PurchaseRequestPOJO;
import com.bookstore.Repository.BookRepository;
import com.bookstore.Repository.CartRepository;
import com.bookstore.Repository.MovieRepository;
import com.bookstore.Repository.MusicRepository;
import com.bookstore.Repository.PurchaseRepository;
import com.bookstore.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final MovieRepository movieRepository;
    private final MusicRepository musicRepository;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            BookRepository bookRepository,
            MovieRepository movieRepository,
            MusicRepository musicRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.movieRepository = movieRepository;
        this.musicRepository = musicRepository;
    }

    // Non-transactional wrapper — catches exceptions thrown by the transactional core
    // and converts them to ResponseEntity without triggering UnexpectedRollbackException.
    public ResponseEntity<String> purchaseUserCart(Integer userId, PurchaseRequestPOJO request) {
        try {
            return doPurchase(userId, request);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Purchase failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // Transactional core — throws on any error so @Transactional can roll back cleanly.
    @Transactional
    public ResponseEntity<String> doPurchase(Integer userId, PurchaseRequestPOJO request) {
        if (request == null) {
            throw new IllegalArgumentException("Purchase request cannot be null");
        }

        if (request.getPaymentOption() == null) {
            throw new IllegalArgumentException("Payment option cannot be empty");
        }

        if (request.getAddress() == null) {
            throw new IllegalArgumentException("Address cannot be empty");
        }

        UserEntity user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<CartEntity> cartItems = cartRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("User cart is empty");
        }

        List<PurchaseCartItem> purchaseCartItems = new ArrayList<>();

        for (CartEntity cartItem : cartItems) {
            validateCartItem(cartItem);
            validateAndDecreaseStock(cartItem);

            purchaseCartItems.add(new PurchaseCartItem(
                    cartItem.getId().getCartId(),
                    cartItem.getId().getProductId(),
                    cartItem.getId().getProdType(),
                    cartItem.getQuantity()
            ));
        }

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setUser(user);
        purchase.setPaymentOption(request.getPaymentOption());
        purchase.setAddress(request.getAddress());
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setCart(purchaseCartItems);

        purchaseRepository.save(purchase);
        cartRepository.deleteAll(cartItems);

        return new ResponseEntity<>(
                "Purchase was successful. Purchase ID: " + purchase.getId(),
                HttpStatus.OK
        );
    }

    public ResponseEntity<List<PurchasePOJO>> getAllPurchases() {
        List<PurchasePOJO> purchases = purchaseRepository.findAll()
                .stream()
                .map(this::mapPurchaseToPOJO)
                .toList();

        return new ResponseEntity<>(purchases, HttpStatus.OK);
    }

    private PurchasePOJO mapPurchaseToPOJO(PurchaseEntity purchase) {
        UserEntity user = purchase.getUser();

        return new PurchasePOJO(
                purchase.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getUsername(),
                purchase.getPaymentOption(),
                purchase.getAddress(),
                purchase.getPurchaseDate(),
                purchase.getCart()
        );
    }

    private void validateCartItem(CartEntity cartItem) {
        if (cartItem == null || cartItem.getId() == null) {
            throw new IllegalArgumentException("Invalid cart item");
        }

        if (cartItem.getId().getProductId() == null || cartItem.getId().getProductId().isBlank()) {
            throw new IllegalArgumentException("Cart item has empty product ID");
        }

        if (cartItem.getId().getProdType() == null) {
            throw new IllegalArgumentException("Cart item has empty product type");
        }

        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid quantity for product: " + cartItem.getId().getProductId()
            );
        }
    }

    private void validateAndDecreaseStock(CartEntity cartItem) {
        String productId = cartItem.getId().getProductId();
        ProductType prodType = cartItem.getId().getProdType();
        Short quantity = cartItem.getQuantity();

        switch (prodType) {
            case BOOK -> decreaseBookStock(productId, quantity);
            case MOVIE -> decreaseMovieStock(productId, quantity);
            case MUSIC -> decreaseMusicStock(productId, quantity);
        }
    }

    private void decreaseBookStock(String isbn, Short quantity) {
        BookEntity book = bookRepository.findBookEntityByIsbn(isbn);

        if (book == null) {
            throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
        }

        if (book.getStock() == null) {
            throw new IllegalArgumentException("Book stock is null for ISBN: " + isbn);
        }

        if (book.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Not enough stock for book. ISBN: " + isbn
                            + ", available: " + book.getStock()
                            + ", requested: " + quantity
            );
        }

        book.setStock((short) (book.getStock() - quantity));
        bookRepository.save(book);
    }

    private void decreaseMovieStock(String productId, Short quantity) {
        Long movieId = parseProductIdAsLong(productId, "Movie");

        MovieEntity movie = movieRepository.findById(movieId).orElse(null);

        if (movie == null) {
            throw new IllegalArgumentException("Movie not found with ID: " + productId);
        }

        if (movie.getStock() == null) {
            throw new IllegalArgumentException("Movie stock is null for ID: " + productId);
        }

        if (movie.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Not enough stock for movie. ID: " + productId
                            + ", available: " + movie.getStock()
                            + ", requested: " + quantity
            );
        }

        movie.setStock((short) (movie.getStock() - quantity));
        movieRepository.save(movie);
    }

    private void decreaseMusicStock(String productId, Short quantity) {
        Long musicId = parseProductIdAsLong(productId, "Music");

        MusicEntity music = musicRepository.findById(musicId).orElse(null);

        if (music == null) {
            throw new IllegalArgumentException("Music not found with ID: " + productId);
        }

        if (music.getStock() == null) {
            throw new IllegalArgumentException("Music stock is null for ID: " + productId);
        }

        if (music.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Not enough stock for music. ID: " + productId
                            + ", available: " + music.getStock()
                            + ", requested: " + quantity
            );
        }

        music.setStock((short) (music.getStock() - quantity));
        musicRepository.save(music);
    }

    private Long parseProductIdAsLong(String productId, String productName) {
        try {
            return Long.valueOf(productId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(productName + " product ID must be an integer: " + productId);
        }
    }
}