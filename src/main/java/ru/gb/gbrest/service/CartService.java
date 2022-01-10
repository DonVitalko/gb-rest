package ru.gb.gbrest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.gb.gbrest.dao.CartDao;
import ru.gb.gbrest.entity.Cart;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartDao cartDao;

    public Cart findCartsById(Long id) {
        return cartDao.findCartsById(id);
    }

    public Cart save(Cart cart) {
        if (cart.getId() != null) {
            Optional<Cart> cartOptional = cartDao.findById(cart.getId());
            if (cartOptional.isPresent()) {
                Cart cartFromDB = cartOptional.get();
                cartFromDB.setProducts(cart.getProducts());
                return cartDao.save(cartFromDB);
            }
        }
        return cartDao.save(cart);
    }

    public void deleteById(Long id) {
        try {
            cartDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("No such id in DB: " + e.getMessage());
        }
    }
}

