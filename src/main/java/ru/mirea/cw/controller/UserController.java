package ru.mirea.cw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ru.mirea.cw.dto.UserDto;
import ru.mirea.cw.entity.Order;
import ru.mirea.cw.entity.User;
import ru.mirea.cw.global.GlobalData;
import ru.mirea.cw.service.CategoryService;
import ru.mirea.cw.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.mirea.cw.service.UserService;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/index")
    public String home() {
        return "index";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result,
                               Model model, HttpServletRequest request) throws ServletException {
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "Уже есть учетная запись, зарегистрированная с тем же адресом электронной почты");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }
        userService.saveUser(userDto);
        request.login(userDto.getEmail(), userDto.getPassword());
        return "redirect:/shop";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        List<Order> orders = orderService.getOrdersByUser(user);

        orders.forEach(order -> {
            double totalPrice = order.getOrderItems().stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();
            order.setTotalPrice(totalPrice);
        });

        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("cartCount", GlobalData.getCount());
        model.addAttribute("orders", orders);
        return "views/profile";
    }
}