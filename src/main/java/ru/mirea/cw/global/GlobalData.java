package ru.mirea.cw.global;

import ru.mirea.cw.entity.Product;

import java.util.HashMap;
import java.util.Map;

public class GlobalData {
    public static Map<Product, Integer> cart;

    static {
        cart = new HashMap<>();
    }

    public static int getCount() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static double getSum() {
        return cart.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }
}