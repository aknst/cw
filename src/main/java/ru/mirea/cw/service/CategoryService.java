package ru.mirea.cw.service;

import ru.mirea.cw.entity.Category;
import ru.mirea.cw.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public void addCategory(Category category){
        categoryRepository.save(category);
    }

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    public void removeCategoryById(int category_id){
        categoryRepository.deleteById(category_id);
    }

    public Category updateCategoryById(Category category){
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(int category_id){
        return categoryRepository.findById(category_id);
    }
}