package com.bookstore.Service;

import com.bookstore.Entity.*;
import com.bookstore.Enum.ProductType;
import com.bookstore.POJOs.*;
import com.bookstore.Repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final MovieRepository movieRepository;
    private final MusicRepository musicRepository;
    private final BookGenreRepository bookGenreRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MusicGenreRepository musicGenreRepository;

    public CartService(
            CartRepository cartRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            MovieRepository movieRepository,
            MusicRepository musicRepository, BookGenreRepository bookGenreRepository, MovieGenreRepository movieGenreRepository, MusicGenreRepository musicGenreRepository
    ) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.movieRepository = movieRepository;
        this.musicRepository = musicRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.movieGenreRepository = movieGenreRepository;
        this.musicGenreRepository = musicGenreRepository;
    }

    private GenrePOJO mapGenreToPOJO(GenreEntity genre) {
        if (genre == null) {
            return null;
        }

        Integer mainGenreId = genre.getMainGenre() == null
                ? null
                : genre.getMainGenre().getId();

        return new GenrePOJO(
                genre.getId(),
                genre.getGenreName(),
                mainGenreId
        );
    }

    private BookPOJO mapBookToPOJO(BookEntity book) {
        List<GenrePOJO> genres = bookGenreRepository.findByBookIsbn(book)
                .stream()
                .map(BookgenreEntity::getGenre)
                .map(this::mapGenreToPOJO)
                .toList();

        return new BookPOJO(
                book.getIsbn(),
                book.getTitle(),
                book.getPrice(),
                book.getEdition(),
                book.getCover().name(),
                book.getPageNum(),
                book.getPublisher(),
                book.getPhysicalSize(),
                book.getAuthors(),
                book.getStock(),
                genres
        );
    }

    private MoviePOJO mapMovieToPOJO(MovieEntity movie) {
        List<GenrePOJO> genres = movieGenreRepository.findByMovie(movie)
                .stream()
                .map(MoviegenreEntity::getGenre)
                .map(this::mapGenreToPOJO)
                .toList();

        return new MoviePOJO(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear(),
                movie.getPrice(),
                movie.getLengthMin(),
                movie.getStock(),
                genres
        );
    }

    private MusicPOJO mapMusicToPOJO(MusicEntity music) {
        List<GenrePOJO> genres = musicGenreRepository.findByMusic(music)
                .stream()
                .map(MusicgenreEntity::getGenre)
                .map(this::mapGenreToPOJO)
                .toList();

        return new MusicPOJO(
                music.getId(),
                music.getTitle(),
                music.getReleaseYear(),
                music.getPrice(),
                music.getStock(),
                music.getArtist(),
                genres
        );
    }

    public ResponseEntity<?> getUserCart(Integer userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
            }

            UserCartPOJO userCart = buildUserCartPOJO(userId);

            return new ResponseEntity<>(userCart, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Getting user cart failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Transactional
    public ResponseEntity<String> addToUserCart(Integer userId, CartItemPOJO cartItem) {
        try {
            ResponseEntity<String> validationResponse = validateCartItem(cartItem);

            if (validationResponse != null) {
                return validationResponse;
            }

            UserEntity user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
            }

            if (!productExists(cartItem.getProductId(), cartItem.getProdType())) {
                return new ResponseEntity<>("Product does not exist", HttpStatus.NOT_FOUND);
            }

            CartId cartId = new CartId(
                    userId,
                    cartItem.getProductId(),
                    cartItem.getProdType()
            );

            CartEntity existingCartItem = cartRepository.findById(cartId).orElse(null);

            if (existingCartItem != null) {
                short newQuantity = (short) (existingCartItem.getQuantity() + cartItem.getQuantity());
                existingCartItem.setQuantity(newQuantity);
                cartRepository.save(existingCartItem);
            } else {
                CartEntity newCartItem = new CartEntity();
                newCartItem.setId(cartId);
                newCartItem.setUser(user);
                newCartItem.setQuantity(cartItem.getQuantity());

                cartRepository.save(newCartItem);
            }

            return new ResponseEntity<>("Adding item to cart was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Adding item to cart failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public UserCartPOJO buildUserCartPOJO(Integer userId) {
        List<CartEntity> cartEntities = cartRepository.findByUserId(userId);

        List<CartBookItemPOJO> books = new ArrayList<>();
        List<CartMovieItemPOJO> movies = new ArrayList<>();
        List<CartMusicItemPOJO> music = new ArrayList<>();

        for (CartEntity cartEntity : cartEntities) {
            String productId = cartEntity.getId().getProductId();
            ProductType prodType = cartEntity.getId().getProdType();
            Short quantity = cartEntity.getQuantity();

            switch (prodType) {
                case BOOK -> {
                    BookEntity book = bookRepository.findBookEntityByIsbn(productId);

                    if (book != null) {
                        books.add(new CartBookItemPOJO(
                                quantity,
                                mapBookToPOJO(book)
                        ));
                    }
                }

                case MOVIE -> {
                    try {
                        Long movieId = Long.valueOf(productId);

                        MovieEntity movie = movieRepository.findById(movieId).orElse(null);

                        if (movie != null) {
                            movies.add(new CartMovieItemPOJO(
                                    quantity,
                                    mapMovieToPOJO(movie)
                            ));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }

                case MUSIC -> {
                    try {
                        Long musicId = Long.valueOf(productId);

                        MusicEntity musicEntity = musicRepository.findById(musicId).orElse(null);

                        if (musicEntity != null) {
                            music.add(new CartMusicItemPOJO(
                                    quantity,
                                    mapMusicToPOJO(musicEntity)
                            ));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return new UserCartPOJO(
                books,
                movies,
                music
        );
    }

    @Transactional
    public ResponseEntity<String> updateCartItemQuantity(Integer userId, CartItemPOJO cartItem) {
        try {
            ResponseEntity<String> validationResponse = validateCartItem(cartItem);

            if (validationResponse != null) {
                return validationResponse;
            }

            if (!userRepository.existsById(userId)) {
                return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
            }

            CartId cartId = new CartId(
                    userId,
                    cartItem.getProductId(),
                    cartItem.getProdType()
            );

            CartEntity existingCartItem = cartRepository.findById(cartId).orElse(null);

            if (existingCartItem == null) {
                return new ResponseEntity<>("Cart item not found", HttpStatus.NOT_FOUND);
            }

            existingCartItem.setQuantity(cartItem.getQuantity());
            cartRepository.save(existingCartItem);

            return new ResponseEntity<>("Updating cart item quantity was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Updating cart item quantity failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Transactional
    public ResponseEntity<String> deleteFromUserCart(
            Integer userId,
            String productId,
            ProductType prodType
    ) {
        try {
            if (!userRepository.existsById(userId)) {
                return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
            }

            if (productId == null || productId.isBlank()) {
                return new ResponseEntity<>("Product ID cannot be empty", HttpStatus.BAD_REQUEST);
            }

            if (prodType == null) {
                return new ResponseEntity<>("Product type cannot be empty", HttpStatus.BAD_REQUEST);
            }

            CartId cartId = new CartId(
                    userId,
                    productId,
                    prodType
            );

            CartEntity existingCartItem = cartRepository.findById(cartId).orElse(null);

            if (existingCartItem == null) {
                return new ResponseEntity<>("Cart item not found", HttpStatus.NOT_FOUND);
            }

            cartRepository.delete(existingCartItem);

            return new ResponseEntity<>("Deleting cart item was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Deleting cart item failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private ResponseEntity<String> validateCartItem(CartItemPOJO cartItem) {
        if (cartItem == null) {
            return new ResponseEntity<>("Cart item cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (cartItem.getProductId() == null || cartItem.getProductId().isBlank()) {
            return new ResponseEntity<>("Product ID cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (cartItem.getProdType() == null) {
            return new ResponseEntity<>("Product type cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (cartItem.getQuantity() == null) {
            return new ResponseEntity<>("Quantity cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (cartItem.getQuantity() <= 0) {
            return new ResponseEntity<>("Quantity must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    private boolean productExists(String productId, ProductType productType) {
        return switch (productType) {
            case BOOK -> bookRepository.findBookEntityByIsbn(productId) != null;

            case MOVIE -> {
                try {
                    Long movieId = Long.valueOf(productId);
                    yield movieRepository.existsById(movieId);
                } catch (NumberFormatException e) {
                    yield false;
                }
            }

            case MUSIC -> {
                try {
                    Long musicId = Long.valueOf(productId);
                    yield musicRepository.existsById(musicId);
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
        };
    }
}