package ru.mirea.cw.service;

import ru.mirea.cw.entity.Product;
import ru.mirea.cw.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public void addProduct(Product product){
        productRepository.save(product);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public void removeProductById(long product_id){
        productRepository.deleteById(product_id);
    }

    public Optional<Product> getProductById(long product_id){
        return productRepository.findById(product_id);
    }

    public void updateProductById(Product product){
        productRepository.save(product);
    }

    public List<Product> getAllProductsByCategoryId(int id){
        return productRepository.findAllProductByCategoryId(id);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}