package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Receipt implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private Cashier cashier;
    private LocalDateTime dateTime;
    private List<Product> products;
    private double totalPrice;

    public Receipt(int id, Cashier cashier, LocalDateTime dateTime, List<Product> products, double totalPrice) {
        this.id = id;
        this.cashier = cashier;
        this.dateTime = dateTime;
        this.products = products;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
