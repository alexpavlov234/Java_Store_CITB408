package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Представлява касова бележка.
 */
public class Receipt implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private int clientId;
    private int cashierId;
    private LocalDateTime dateTime;
    private double totalPrice;
    private Map<Product, Integer> purchasedProducts;

    /**
     * Конструктор за създаване на касова бележка.
     *
     * @param clientId          ID на клиента.
     * @param cashierId         ID на касиера.
     * @param dateTime          Дата и час на издаване.
     * @param purchasedProducts Речник с хеш-таблица със закупените продукти и техните количества.
     */
    public Receipt(int clientId, int cashierId, LocalDateTime dateTime, Map<Product, Integer> purchasedProducts) {
        this.clientId = clientId;
        this.cashierId = cashierId;
        this.dateTime = dateTime;
        this.purchasedProducts = purchasedProducts;
        calculateTotalPrice();
    }

    /**
     * Връща ID на касовата бележка.
     *
     * @return ID на касовата бележка.
     */
    public int getId() {
        return id;
    }

    /**
     * Задава ID на касовата бележка.
     *
     * @param id Ново ID на касовата бележка.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Връща ID на клиента.
     *
     * @return ID на клиента.
     */
    public int getClient() {
        return clientId;
    }

    /**
     * Задава ID на клиента.
     *
     * @param clientId Ново ID на клиента.
     */
    public void setClient(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Връща ID на касиера.
     *
     * @return ID на касиера.
     */
    public int getCashier() {
        return cashierId;
    }

    /**
     * Задава ID на касиера.
     *
     * @param cashierId Ново ID на касиера.
     */
    public void setCashier(int cashierId) {
        this.cashierId = cashierId;
    }

    /**
     * Връща датата и часа на издаване на касовата бележка.
     *
     * @return Дата и час на издаване.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Задава датата и часа на издаване на касовата бележка.
     *
     * @param dateTime Нова дата и час на издаване.
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Връща общата сума на касовата бележка.
     *
     * @return Обща сума.
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Изчислява общата сума на касовата бележка на базата на закупените продукти.
     */
    public void calculateTotalPrice() {
        totalPrice = 0.0; // Reset total price
        for (Map.Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            totalPrice += product.getUnitSalePrice() * quantity; // Assuming unitSalePrice is the price at which the product is sold
        }
    }

    /**
     * Връща речник с хеш-таблица със закупените продукти и техните количества.
     *
     * @return Речник с хеш-таблица със закупените продукти.
     */
    public Map<Product, Integer> getPurchasedProducts() {
        return purchasedProducts;
    }

    /**
     * Задава речник с хеш-таблица със закупените продукти и техните количества.
     *
     * @param purchasedProducts Нов речник с хеш-таблица със закупените продукти.
     */
    public void setPurchasedProducts(Map<Product, Integer> purchasedProducts) {
        this.purchasedProducts = purchasedProducts;
    }


}
