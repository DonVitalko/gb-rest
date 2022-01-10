package ru.gb.gbrest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.gbrest.dao.ProductDao;
import ru.gb.gbrest.dto.ProductDto;
import ru.gb.gbrest.entity.Product;
import ru.gb.gbrest.entity.enums.Status;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductDao productDao;

    public ProductDto save(ProductDto productDto) {
        Product savingProduct;
        if (productDto.getId() != null) {
            Optional<Product> productFromDbOptional = productDao.findById(productDto.getId());
            savingProduct = productFromDbOptional.orElseGet(Product::new);
        }else {
            savingProduct = new Product();
        }
        savingProduct.setTitle(productDto.getTitle());
        savingProduct.setManufactureDate(productDto.getManufactureDate());
        savingProduct.setStatus(productDto.getStatus());
        savingProduct.setCost(productDto.getCost());
        savingProduct = productDao.save(savingProduct);
        productDto.setId(savingProduct.getId());
        return productDto;
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productDao.findById(id).orElse(null);
    }

    public List<Product> findAll() {
        return productDao.findAll();
    }

    public void deleteById(Long id) {
        try {
            productDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("No such id in DB: " + e.getMessage());
        }
    }


    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public long count() {
        System.out.println(productDao.count());
        return productDao.count();
    }

    public List<Product> findAll(int page, int size) {
        return productDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size));
    }

    public List<Product> findAllSortedById() {
        return productDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Product> findAllSortedById(int page, int size) {
        return productDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    public List<Product> findAllActive() {
        return productDao.findAllByStatus(Status.ACTIVE);
    }

}
