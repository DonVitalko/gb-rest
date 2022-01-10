package ru.gb.gbrest.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.gbrest.dao.ProductDao;
import ru.gb.gbrest.entity.Cart;
import ru.gb.gbrest.entity.Product;
import ru.gb.gbrest.service.CartService;
import ru.gb.gbrest.service.ProductService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartController {

    private final CartService cartService;
    private final Cart cart = Cart.builder().build();
    private final ProductService productService;

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartList (@PathVariable("cartId") Long id){
        Cart cart;
        if (id != null) {
            cart = cartService.findCartsById(id);
            if (cart != null) {
                return new ResponseEntity<>(cart, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<?> handlePost(@Validated @RequestBody ProductDao productDto) {
        List<Product> productList;
        Product product = productService.findById(productDto.getById());
        if (cart.getId() == null) {
            productList = new ArrayList<>();
            productList.add(product);
            cart.setProducts(productList);
            Cart cartFromDb = cartService.save(cart);
            cart.setId(cartFromDb.getId());
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        } else {
            Cart cartFromDb = cartService.findCartsById(cart.getId());
            productList = cartFromDb.getProducts();
            productList.removeIf(product1 -> product1.getId().equals(productDto.getById()));
            productList.add(product);
            cart.setProducts(productList);
            cartService.save(cart);
            return new ResponseEntity<>(cart,HttpStatus.OK);
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> handleUpdate(@PathVariable("cartId") Long id,
                                          @Validated @RequestBody ProductDao productDto) {
        Optional<Cart> cartOptional = Optional.ofNullable(cartService.findCartsById(id));
        if (cartOptional.isPresent()) {
            cart.setProducts(cartOptional.get().getProducts());
            cart.getProducts().removeIf(product -> product.getId().equals(productDto.getById()));
            cartService.save(cart);
            return new ResponseEntity<>(cart,HttpStatus.OK);
        }else {
            return  new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }
}