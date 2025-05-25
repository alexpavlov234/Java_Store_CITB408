package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ReceiptTest {

    private final int testClientId = 1;
    private final int testCashierId = 2;
    private final LocalDateTime testDateTime = LocalDateTime.now();
    private Receipt receipt;
    private Map<Product, Integer> testPurchasedProducts;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product("Тестов продукт 1", 10.0, ProductCategory.FOOD, LocalDate.now().plusDays(30));
        testProduct1.setUnitSalePrice(15.0);

        testProduct2 = new Product("Тестов продукт 2", 20.0, ProductCategory.NON_FOOD, LocalDate.now().plusDays(60));
        testProduct2.setUnitSalePrice(30.0);

        testPurchasedProducts = new HashMap<>();
        testPurchasedProducts.put(testProduct1, 2); // 2 броя от продукт 1
        testPurchasedProducts.put(testProduct2, 1); // 1 брой от продукт 2

        receipt = new Receipt(testClientId, testCashierId, testDateTime, testPurchasedProducts);
    }

    @Test
    void getId() {
        assertEquals(0, receipt.getId());

        receipt.setId(1);
        assertEquals(1, receipt.getId());
    }

    @Test
    void setId() {
        receipt.setId(5);
        assertEquals(5, receipt.getId());

        receipt.setId(10);
        assertEquals(10, receipt.getId());
    }

    @Test
    void getClient() {
        assertEquals(testClientId, receipt.getClient());
    }

    @Test
    void setClient() {
        int newClientId = 3;
        receipt.setClient(newClientId);
        assertEquals(newClientId, receipt.getClient());
    }

    @Test
    void getCashier() {
        assertEquals(testCashierId, receipt.getCashier());
    }

    @Test
    void setCashier() {
        int newCashierId = 4;
        receipt.setCashier(newCashierId);
        assertEquals(newCashierId, receipt.getCashier());
    }

    @Test
    void getDateTime() {
        assertEquals(testDateTime, receipt.getDateTime());
    }

    @Test
    void setDateTime() {
        LocalDateTime newDateTime = LocalDateTime.now().plusHours(1);
        receipt.setDateTime(newDateTime);
        assertEquals(newDateTime, receipt.getDateTime());
    }

    @Test
    void getTotalPrice() {
        assertEquals(60.0, receipt.getTotalPrice(), 0.001);
    }

    @Test
    void calculateTotalPrice() {
        assertEquals(60.0, receipt.getTotalPrice(), 0.001);

        testProduct1.setUnitSalePrice(20.0);
        receipt.calculateTotalPrice();
        assertEquals(70.0, receipt.getTotalPrice(), 0.001);

        testPurchasedProducts.put(testProduct1, 3);
        receipt.calculateTotalPrice();

        assertEquals(90.0, receipt.getTotalPrice(), 0.001);
    }

    @Test
    void getPurchasedProducts() {
        Map<Product, Integer> products = receipt.getPurchasedProducts();

        assertSame(testPurchasedProducts, products);

        assertEquals(2, products.size());
        assertEquals(2, products.get(testProduct1));
        assertEquals(1, products.get(testProduct2));
    }

    @Test
    void setPurchasedProducts() {
        Map<Product, Integer> newProducts = new HashMap<>();
        Product newProduct = new Product("Нов продукт", 5.0, ProductCategory.FOOD, LocalDate.now().plusDays(10));
        newProduct.setUnitSalePrice(8.0);
        newProducts.put(newProduct, 4);

        receipt.setPurchasedProducts(newProducts);

        assertSame(newProducts, receipt.getPurchasedProducts());
        assertEquals(1, receipt.getPurchasedProducts().size());
        assertEquals(4, receipt.getPurchasedProducts().get(newProduct));

        receipt.calculateTotalPrice();
        assertEquals(32.0, receipt.getTotalPrice(), 0.001); // 8.0 * 4 = 32.0
    }
}

