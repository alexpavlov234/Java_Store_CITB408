package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double unitPurchasePrice;
    private double unitSalePrice;
    private ProductCategory category;
    private LocalDate expirationDate;

    public Product(String name, double unitPurchasePrice, ProductCategory category, LocalDate expirationDate) {
        this.name = name;
        this.unitPurchasePrice = unitPurchasePrice;
        this.category = category;
        this.expirationDate = expirationDate;
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

    public double getUnitPurchasePrice() {
        return unitPurchasePrice;
    }

    public void setUnitPurchasePrice(double unitPurchasePrice) {
        this.unitPurchasePrice = unitPurchasePrice;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isProductSellable(){
        return expirationDate.isAfter(LocalDate.now()) ||
                expirationDate.isEqual(LocalDate.now());
    }

    public double getUnitSalePrice() {
        return unitSalePrice;
    }

    public void setUnitSalePrice(double unitSalePrice) {
        this.unitSalePrice = unitSalePrice;
    }

}
