package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private final String testName = "Тестов Продукт";
    private final double testPurchasePrice = 10.5;
    private final ProductCategory testCategory = ProductCategory.FOOD;
    private final LocalDate futureDate = LocalDate.now().plusDays(30);
    private final LocalDate pastDate = LocalDate.now().minusDays(10);
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(testName, testPurchasePrice, testCategory, futureDate);
    }

    @Test
    void getId() {
        assertEquals(0, product.getId());


        product.setId(1);
        assertEquals(1, product.getId());
    }

    @Test
    void setId() {
        product.setId(5);
        assertEquals(5, product.getId());

        product.setId(10);
        assertEquals(10, product.getId());
    }

    @Test
    void getName() {
        assertEquals(testName, product.getName());
    }

    @Test
    void setName() {
        String newName = "Нов продукт";
        product.setName(newName);
        assertEquals(newName, product.getName());
    }

    @Test
    void getUnitPurchasePrice() {
        assertEquals(testPurchasePrice, product.getUnitPurchasePrice(), 0.001);
    }

    @Test
    void setUnitPurchasePrice() {
        double newPrice = 15.75;
        product.setUnitPurchasePrice(newPrice);
        assertEquals(newPrice, product.getUnitPurchasePrice(), 0.001);
    }

    @Test
    void getCategory() {
        assertEquals(testCategory, product.getCategory());
    }

    @Test
    void setCategory() {
        ProductCategory newCategory = ProductCategory.NON_FOOD;
        product.setCategory(newCategory);
        assertEquals(newCategory, product.getCategory());
    }

    @Test
    void getExpirationDate() {
        assertEquals(futureDate, product.getExpirationDate());
    }

    @Test
    void setExpirationDate() {
        LocalDate newDate = LocalDate.now().plusMonths(3);
        product.setExpirationDate(newDate);
        assertEquals(newDate, product.getExpirationDate());
    }

    @Test
    void isProductSellable() {
        assertTrue(product.isProductSellable());

        Product todayProduct = new Product(testName, testPurchasePrice, testCategory, LocalDate.now());
        assertTrue(todayProduct.isProductSellable());

        Product expiredProduct = new Product(testName, testPurchasePrice, testCategory, pastDate);
        assertFalse(expiredProduct.isProductSellable());
    }

    @Test
    void getUnitSalePrice() {
        assertEquals(0.0, product.getUnitSalePrice(), 0.001);


        double salePrice = 20.0;
        product.setUnitSalePrice(salePrice);
        assertEquals(salePrice, product.getUnitSalePrice(), 0.001);
    }

    @Test
    void setUnitSalePrice() {
        double salePrice = 15.5;
        product.setUnitSalePrice(salePrice);
        assertEquals(salePrice, product.getUnitSalePrice(), 0.001);

        double newSalePrice = 18.75;
        product.setUnitSalePrice(newSalePrice);
        assertEquals(newSalePrice, product.getUnitSalePrice(), 0.001);
    }

    @Test
    void isProductExpired() {
        assertFalse(product.isProductExpired());

        Product expiredProduct = new Product(testName, testPurchasePrice, testCategory, pastDate);
        assertTrue(expiredProduct.isProductExpired());

        Product todayProduct = new Product(testName, testPurchasePrice, testCategory, LocalDate.now());
        assertFalse(todayProduct.isProductExpired());
    }
}

