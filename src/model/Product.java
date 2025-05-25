package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Представлява продукт в магазин.
 */
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double unitPurchasePrice;
    private double unitSalePrice;
    private ProductCategory category;
    private LocalDate expirationDate;

    /**
     * Конструктор за създаване на продукт с име, покупна цена, категория и срок на годност.
     * ID-то и продажната цена се генерират автоматично.
     *
     * @param name              Име на продукта.
     * @param unitPurchasePrice Покупна цена на продукта.
     * @param category          Категория на продукта.
     * @param expirationDate    Срок на годност на продукта.
     */
    public Product(String name, double unitPurchasePrice, ProductCategory category, LocalDate expirationDate) {
        this.name = name;
        this.unitPurchasePrice = unitPurchasePrice;
        this.category = category;
        this.expirationDate = expirationDate;
    }

    /**
     * Връща ID на продукта.
     *
     * @return ID на продукта.
     */
    public int getId() {
        return id;
    }

    /**
     * Задава ID на продукта.
     *
     * @param id Ново ID на продукта.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Връща името на продукта.
     *
     * @return Име на продукта.
     */
    public String getName() {
        return name;
    }

    /**
     * Задава името на продукта.
     *
     * @param name Ново име на продукта.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Връща покупната цена на продукта.
     *
     * @return Покупна цена на продукта.
     */
    public double getUnitPurchasePrice() {
        return unitPurchasePrice;
    }

    /**
     * Задава покупната цена на продукта.
     *
     * @param unitPurchasePrice Нова покупна цена на продукта.
     */
    public void setUnitPurchasePrice(double unitPurchasePrice) {
        this.unitPurchasePrice = unitPurchasePrice;
    }

    /**
     * Връща категорията на продукта.
     *
     * @return Категория на продукта.
     */
    public ProductCategory getCategory() {
        return category;
    }

    /**
     * Задава категорията на продукта.
     *
     * @param category Нова категория на продукта.
     */
    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    /**
     * Връща срока на годност на продукта.
     *
     * @return Срок на годност на продукта.
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Задава срока на годност на продукта.
     *
     * @param expirationDate Нов срок на годност на продукта.
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Проверява дали продуктът е годен за продажба (срокът на годност не е изтекъл).
     *
     * @return true, ако продуктът е годен за продажба, false в противен случай.
     */
    public boolean isProductSellable() {
        return expirationDate.isAfter(LocalDate.now()) ||
                expirationDate.isEqual(LocalDate.now());
    }

    /**
     * Връща продажната цена на продукта.
     *
     * @return Продажна цена на продукта.
     */
    public double getUnitSalePrice() {
        return unitSalePrice;
    }

    /**
     * Задава продажната цена на продукта.
     *
     * @param unitSalePrice Нова продажна цена на продукта.
     */
    public void setUnitSalePrice(double unitSalePrice) {
        this.unitSalePrice = unitSalePrice;
    }

    /**
     * Проверява дали срокът на годност на продукта е изтекъл.
     *
     * @return true, ако срокът на годност е изтекъл, false в противен случай.
     */
    public boolean isProductExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }


}
