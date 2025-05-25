package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class Receipt implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private int clientId;
    private int cashierId;
    private LocalDateTime dateTime;
    private double totalPrice;
    private Map<Product, Integer> purchasedProducts;

    public Receipt(int clientId, int cashierId, LocalDateTime dateTime, double totalPrice, Map<Product, Integer> purchasedProducts) {
        this.clientId = clientId;
        this.cashierId = cashierId;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
        this.purchasedProducts = purchasedProducts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient() {
        return clientId;
    }

    public void setClient(int clientId) {
        this.clientId = clientId;
    }

    public int getCashier() {
        return cashierId;
    }

    public void setCashier(int cashierId) {
        this.cashierId = cashierId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void calculateTotalPrice() {
        totalPrice = 0.0; // Reset total price
        for (Map.Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            totalPrice += product.getUnitSalePrice() * quantity; // Assuming unitSalePrice is the price at which the product is sold
        }
    }

    // Add a method to add products to the receipt
    public void addProduct(Product product, int quantity, double unitPrice) {
        if (purchasedProducts.containsKey(product)) {
            purchasedProducts.compute(product, (k, currentQty) -> currentQty + quantity);
        } else {
            purchasedProducts.put(product, quantity);
        }

        // Update total price
        this.totalPrice += unitPrice * quantity;
    }

    public Map<Product, Integer> getPurchasedProducts() {
        return purchasedProducts;
    }

    public void setPurchasedProducts(Map<Product, Integer> purchasedProducts) {
        this.purchasedProducts = purchasedProducts;
    }


}
