package ru.mirea.cw.controller;


import jakarta.servlet.http.HttpServletRequest;
import ru.mirea.cw.entity.Order;
import ru.mirea.cw.entity.OrderItem;
import ru.mirea.cw.entity.User;
import ru.mirea.cw.global.GlobalData;
import ru.mirea.cw.service.CategoryService;
import ru.mirea.cw.service.OrderService;
import ru.mirea.cw.service.ProductService;
import ru.mirea.cw.entity.Product;

import ru.mirea.cw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable long id, @RequestParam(required = false, defaultValue = "1") int quantity, HttpServletRequest request){
        productService.getProductById(id).ifPresent(
                product -> {
                    for (int i = 0; i < quantity; i++) {
                        GlobalData.cart.put(product, GlobalData.cart.getOrDefault(product, 0) + 1);
                    }
                }
        );
        String referer = request.getHeader("Referer");
        if (referer != null) {
            return "redirect:" + referer.split("\\?")[0];
        } else {
            return "redirect:/shop";
        }
    }
    @GetMapping("/cart")
    public String getCart(Model model){
        Map<Product, Integer> cartItems = new HashMap<>();
        GlobalData.cart.forEach((product, quantity) -> cartItems.put(product, quantity));
        model.addAttribute("cartCount", GlobalData.getCount());
        model.addAttribute("total", GlobalData.getSum()); // Use calcSum method for total sum
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("categories", categoryService.getAllCategory());
        return "views/cart";
    }
    @GetMapping("/cart/removeItem/{productId}")
    public String removeItem(@PathVariable long productId){
        Product productToRemove = productService.getProductById(productId).orElse(null);

        if (productToRemove != null) {
            GlobalData.cart.remove(productToRemove);
        }

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        if (user != null && GlobalData.cart.size() > 0) {
            Order order = new Order();
            order.setStatus("В обработке");
            order.setUser(user);
            order.setOrderDate(new Date());
            order.setOrderItems(new ArrayList<>());
            for (Map.Entry<Product, Integer> entry : GlobalData.cart.entrySet()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(entry.getKey());
                orderItem.setQuantity(entry.getValue());
                order.getOrderItems().add(orderItem);
            }
            System.out.println(order);
            Order savedOrder = orderService.createOrder(order);
            GlobalData.cart.clear();
            model.addAttribute("order", savedOrder);
            return "views/newOrder";
        } else {
            return "views/newOrder";
        }
    }
}
