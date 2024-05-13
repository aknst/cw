package ru.mirea.cw.service;

import ru.mirea.cw.entity.Order;
import ru.mirea.cw.entity.User;
import ru.mirea.cw.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findAllByUser(user);
    }
    public void updateOrderStatusById(long id, String status) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(value -> value.setStatus(status));
    }
    public void removeOrderById(long product_id){
        orderRepository.deleteById(product_id);
    }
}
