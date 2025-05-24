package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private List<Integer> cashiersIds;
    private List<Integer> receiptsIds;
    private Map<Integer, Integer> productsInStock = new HashMap<>();
    private Map<Integer, Integer> productsSold = new HashMap<>();

    private Map<ProductCategory, Double> markupPercentages;

    private int daysBeforeExpirationThreshold;
    private double discountPercentNearExpiration;

    public double getProductFinalPrice(Product product) {
        double markupPercentage = markupPercentages.get(product.getCategory());

        double productFinalPrice = product.getUnitPurchasePrice() + (product.getUnitPurchasePrice() * markupPercentage/100);

        if(isProductExpirationDiscountable(product)){
            return productFinalPrice - (productFinalPrice * discountPercentNearExpiration/100);
        } else {
            return productFinalPrice;
        }
    }

    public boolean isProductExpirationDiscountable(Product product){
        if(daysBeforeExpirationThreshold >= ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate())){
            return true;
        } else {
            return false;
        }
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getCashiersIds() {
        return cashiersIds;
    }

    public void setCashiersIds(List<Integer> cashiersIds) {
        this.cashiersIds = cashiersIds;
    }

    public void addCashierId(int cashierId) {
        if (!cashiersIds.contains(cashierId)) {
            cashiersIds.add(cashierId);
        }
    }

    public void removeCashierId(int cashierId) {
        cashiersIds.remove(Integer.valueOf(cashierId));
    }

    public List<Integer> getReceiptsIds() {
        return receiptsIds;
    }

    public void setReceiptsIds(List<Integer> receiptsIds) {
        this.receiptsIds = receiptsIds;
    }

    public void addReceiptId(int receiptId) {
        if (!receiptsIds.contains(receiptId)) {
            receiptsIds.add(receiptId);
        }
    }

    public void removeReceiptId(int receiptId) {
        receiptsIds.remove(Integer.valueOf(receiptId));
    }

    // Add methods to manage inventory
    public void addProductStock(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }
        int currentStock = productsInStock.getOrDefault(product.getId(), 0);
        productsInStock.put(product.getId(), currentStock + quantity);
    }

    public boolean removeProductStock(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }

        int currentStock = productsInStock.getOrDefault(product.getId(), 0);
        if (currentStock < quantity) {
            return false; // Not enough stock
        }

        productsInStock.put(product.getId(), currentStock - quantity);
        return true;
    }

    public void addProductSold(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }
        int currentSold = productsSold.getOrDefault(product.getId(), 0);
        productsSold.put(product.getId(), currentSold + quantity);
    }

    public boolean removeProductSold(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }

        int currentSold = productsSold.getOrDefault(product.getId(), 0);
        if (currentSold < quantity) {
            return false; // Not enough sold
        }

        productsSold.put(product.getId(), currentSold - quantity);
        return true;
    }

    public int getProductStock(int productId) {
        return productsInStock.getOrDefault(productId, 0);
    }

    public Map<Integer, Integer> getProductsInStock() {
        return productsInStock;
    }

    public void setProductsInStock(Map<Integer, Integer> productsInStock) {
        this.productsInStock = productsInStock;
    }

    public Map<Integer, Integer> getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(Map<Integer, Integer> productsSold) {
        this.productsSold = productsSold;
    }

    public Map<ProductCategory, Double> getMarkupPercentages() {
        return markupPercentages;
    }

    public void setMarkupPercentages(Map<ProductCategory, Double> markupPercentages) {
        this.markupPercentages = markupPercentages;
    }

    public int getDaysBeforeExpirationThreshold() {
        return daysBeforeExpirationThreshold;
    }

    public void setDaysBeforeExpirationThreshold(int daysBeforeExpirationThreshold) {
        this.daysBeforeExpirationThreshold = daysBeforeExpirationThreshold;
    }

    public double getDiscountPercentNearExpiration() {
        return discountPercentNearExpiration;
    }

    public void setDiscountPercentNearExpiration(double discountPercentNearExpiration) {
        this.discountPercentNearExpiration = discountPercentNearExpiration;
    }


}
