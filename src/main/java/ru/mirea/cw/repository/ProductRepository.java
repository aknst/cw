package ru.mirea.cw.repository;

import ru.mirea.cw.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllProductByCategoryId(int id);

    List<Product> findByNameContainingIgnoreCase(String name);
}