package ru.mirea.cw.controller;

import ru.mirea.cw.entity.Product;
import ru.mirea.cw.global.GlobalData;
import ru.mirea.cw.service.CategoryService;
import ru.mirea.cw.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ShopController {
    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping("/error/403")
    public String accessDenied() {
        return "views/errors/403";
    }

    @RequestMapping("/error/404")
    public String notFound() {
        return "views/errors/404";
    }

    @GetMapping({"/", "/home"})
    public String home(Model model){
        model.addAttribute("cartCount", GlobalData.getCount());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "index";
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(name = "search", required = false) String search, Model model){
        model.addAttribute("cartCount", GlobalData.getCount());
        model.addAttribute("categories", categoryService.getAllCategory());
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProductsByName(search);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "views/shop";
    }

    @GetMapping("/shop/category/{id}")
    public String shopByCategory(@PathVariable Integer id, Model model){
        model.addAttribute("cartCount", GlobalData.getCount());
        model.addAttribute("products", productService.getAllProductsByCategoryId(id));
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("activeCategoryId", id);
        return "views/shop";
    }

    @GetMapping("/shop/product/{id}")
    public String viewProduct(@PathVariable Integer id, Model model){
        Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/error/404";
        }
        model.addAttribute("cartCount", GlobalData.getCount());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("product", product);
        return "views/viewProduct";
    }
}
