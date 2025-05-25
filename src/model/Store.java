package model;

import service.CashDeskService;
import service.CashierService;
import service.ProductService;
import service.ServiceFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private Set<Integer> cashiersIds = new HashSet<>();
    private Set<Integer> receiptsIds = new HashSet<>();
    private Map<Integer, Integer> productsInStock = new HashMap<>();
    private Map<Integer, Integer> productsSold = new HashMap<>();

    private Map<ProductCategory, Double> markupPercentages = new HashMap<>();

    private int daysBeforeExpirationThreshold;
    private double discountPercentNearExpiration;

    public Store(String name, Map<ProductCategory, Double> markupPercentages, int daysBeforeExpirationThreshold, double discountPercentNearExpiration) {
        this.name = name;
        this.markupPercentages = markupPercentages;
        this.daysBeforeExpirationThreshold = daysBeforeExpirationThreshold;
        this.discountPercentNearExpiration = discountPercentNearExpiration;
    }

    public double getProductFinalPrice(Product product) {
        double markupPercentage = markupPercentages.get(product.getCategory());

        double productFinalPrice = product.getUnitPurchasePrice() + (product.getUnitPurchasePrice() * markupPercentage / 100);

        if (isProductExpirationDiscountable(product)) {
            return productFinalPrice - (productFinalPrice * discountPercentNearExpiration / 100);
        } else {
            return productFinalPrice;
        }
    }

    public boolean isProductExpirationDiscountable(Product product) {
        return daysBeforeExpirationThreshold >= ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
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

    public Set<Integer> getCashiersIds() {
        return cashiersIds;
    }

    public void setCashiersIds(Set<Integer> cashiersIds) {
        this.cashiersIds = cashiersIds;
    }

    public void addCashier(int cashierId) {
        cashiersIds.add(cashierId);
    }

    public void removeCashier(int cashierId) {
        cashiersIds.remove(Integer.valueOf(cashierId));
    }

    public Set<Integer> getReceiptsIds() {
        return receiptsIds;
    }

    public void setReceiptsIds(Set<Integer> receiptsIds) {
        this.receiptsIds = receiptsIds;
    }

    public void addReceipt(int receiptId) {
        receiptsIds.add(receiptId);
    }

    public void removeReceipt(int receiptId) {
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


    public ArrayList<Product> getAvailableProducts() {
        ArrayList<Product> availableProducts = new ArrayList<>();
        ProductService productService = ServiceFactory.getProductService();
        for (Map.Entry<Integer, Integer> entry : productsInStock.entrySet()) {
            int productId = entry.getKey();
            int stockQuantity = entry.getValue();

            if (stockQuantity > 0) {
                Optional<Product> productOpt = productService.findEntityById(productId);
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    if (!product.isProductExpired()) {
                        availableProducts.add(product);
                    }
                }
            }
        }
        return availableProducts;
    }

    public void updateProductPrices() {
        ProductService productService = ServiceFactory.getProductService();
        for (Map.Entry<Integer, Integer> entry : productsInStock.entrySet()) {
            int productId = entry.getKey();
            Optional<Product> productOpt = productService.findEntityById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                double finalPrice = getProductFinalPrice(product);
                product.setUnitSalePrice(finalPrice);
                productService.updateEntity(product);
            }
        }
    }

    public ArrayList<CashDesk> getCashDesks() {
        ArrayList<CashDesk> cashDesks = new ArrayList<>();
        CashDeskService cashDeskService = ServiceFactory.getCashDeskService();

        if (cashDeskService.getAllEntities().isEmpty()) {
            throw new IllegalArgumentException("Няма налични каси в системата");
        }

        for (Integer cashierId : cashiersIds) {
            Predicate<CashDesk> filter = cashDesk -> cashDesk.getCashier() == cashierId;

            Optional<CashDesk> cashDeskOpt = cashDeskService.findEntityByFilter(filter);
            if (cashDeskOpt.isPresent()) {
                CashDesk cashDesk = cashDeskOpt.get();
                cashDesks.add(cashDesk);
            } else {
                throw new IllegalArgumentException("Каса с касиер с ID " + cashierId + " не съществува");
            }

        }

        return cashDesks;
    }


    public void setProductStock(int id, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + id + " не може да бъде отрицателно");
        }
        productsInStock.put(id, i);
    }
}
