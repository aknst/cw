package ru.mirea.cw.controller;

import ru.mirea.cw.dto.ProductDTO;
import ru.mirea.cw.dto.UserDto;
import ru.mirea.cw.entity.Category;
import ru.mirea.cw.entity.Order;
import ru.mirea.cw.entity.Product;
import ru.mirea.cw.service.CategoryService;
import ru.mirea.cw.service.OrderService;
import ru.mirea.cw.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.cw.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    public String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/admin")
    public String adminHome(){
        return "views/admin/panel";
    }
    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders()); return "views/admin/orders";
    }
    @GetMapping("/admin/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers(); model.addAttribute("users", users); return "views/admin/users";
    }
    @GetMapping("/admin/products")
    public String products(Model model){
        model.addAttribute("products", productService.getAllProducts()); return "views/admin/products";
    }
    @PostMapping("/admin/products/add")
    public String postProductAdd(@ModelAttribute(name = "productDTO") ProductDTO productDTO, @RequestParam("productImage") MultipartFile file, @RequestParam("imgName") String imgName) throws IOException {
        Product product = new Product(); product.setId(productDTO.getId()); product.setName(productDTO.getName());
        product.setCategory(categoryService.getCategoryById(productDTO.getCategory_id().intValue()).get());
        product.setDescription(productDTO.getDescription()); product.setPrice(productDTO.getPrice());
        String imageUUID;
        if(!file.isEmpty()){
            imageUUID = file.getOriginalFilename(); Path fileNameAndPath = Paths.get(uploadDir, imageUUID); Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }
        product.setImageName(imageUUID); productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/categories")
    public String getCategories(Model model){
        model.addAttribute("categories", categoryService.getAllCategory());
        return "views/admin/categories";
    }

    @PostMapping("/admin/categories/add")
    public String postCategoriesAdd(@ModelAttribute("category") Category category){
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/orders/status/update")
    public String postOrdersAdd(@ModelAttribute("order") Order order){
        orderService.updateOrderStatusById(order.getId(), order.getStatus());
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders/update/{id}")
    public String updateOrderById(@PathVariable(name ="id") long order_id, Model model){
        Order order = orderService.getOrderById(order_id);
        if (order != null){
            model.addAttribute("order", order);
            return "views/admin/ordersUpdate";
        } else {
            return "redirect:/error/404";
        }
    }

    @GetMapping("/admin/orders/delete/{id}")
    public String deleteOrderById(@PathVariable(name = "id") long order_id){
        orderService.removeOrderById(order_id);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/categories/add")
    public String getCategoriesAdd(Model model){
        model.addAttribute("category", new Category());
        return "views/admin/categoriesAdd";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCategoryById(@PathVariable(name = "id") int category_id){

        categoryService.removeCategoryById(category_id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCategoryById(@PathVariable(name ="id") int category_id, Model model){
        Optional<Category> category = categoryService.getCategoryById(category_id);
        if(category.isPresent()){
            model.addAttribute("category", category.get());
            return "admin/categoriesAdd";
        } else {
            return "redirect:/error/404";
        }
    }

    @GetMapping("/admin/products/add")
    public String getProductsAdd(Model model){
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "views/admin/productsAdd";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String deleteProductById(@PathVariable(name = "id") int product_id){
        productService.removeProductById(product_id);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/product/update/{id}")
    public String updateProductById(@PathVariable(name = "id") int product_id, Model model){
        Product product = productService.getProductById(product_id).orElse(null);
        if(product != null && product.getId() != null){
            ProductDTO productDTO = new ProductDTO();

            productDTO.setId(product.getId());
            productDTO.setCategory_id(product.getCategory().getId());
            productDTO.setDescription(product.getDescription());
            productDTO.setImageName(product.getImageName());
            productDTO.setName(product.getName());
            productDTO.setPrice(product.getPrice());

            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", categoryService.getAllCategory());
            return "views/admin/productsAdd";
        } else {
            return "redirect:/error/404";
        }
    }


}