package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreTest {

    private final String testName = "Тестов Магазин";
    private final Map<ProductCategory, Double> testMarkupPercentages = new HashMap<>();
    private final int testDaysBeforeExpirationThreshold = 5;
    private final double testDiscountPercentNearExpiration = 20.0;
    private Store store;
    private Product testProduct1;
    private Product testProduct2;
    private Product testExpiringProduct;
    private Cashier testCashier;
    private Receipt testReceipt;

    @BeforeEach
    void setUp() {

        testMarkupPercentages.put(ProductCategory.FOOD, 30.0);
        testMarkupPercentages.put(ProductCategory.NON_FOOD, 40.0);

        store = new Store(testName, testMarkupPercentages, testDaysBeforeExpirationThreshold, testDiscountPercentNearExpiration);

        testProduct1 = new Product("Тестов продукт 1", 10.0, ProductCategory.FOOD, LocalDate.now().plusDays(30));
        testProduct1.setId(1);
        testProduct1.setUnitSalePrice(13.0);

        testProduct2 = new Product("Тестов продукт 2", 20.0, ProductCategory.NON_FOOD, LocalDate.now().plusDays(60));
        testProduct2.setId(2);
        testProduct2.setUnitSalePrice(28.0);

        testExpiringProduct = new Product("Изтичащ продукт", 15.0, ProductCategory.FOOD, LocalDate.now().plusDays(3));
        testExpiringProduct.setId(3);
        testExpiringProduct.setUnitSalePrice(19.5);

        testCashier = new Cashier("Тестов Касиер", 1500.0);
        testCashier.setId(1);

        store.addCashier(testCashier.getId());
        store.setProductStock(testProduct1.getId(), 10);
        store.setProductStock(testProduct2.getId(), 5);
        store.setProductStock(testExpiringProduct.getId(), 3);
        store.addProductSold(testProduct1, 2);

        Map<Product, Integer> purchasedProducts = new HashMap<>();
        purchasedProducts.put(testProduct1, 2);
        testReceipt = new Receipt(1, testCashier.getId(), LocalDateTime.now(), purchasedProducts);
        testReceipt.setId(1);
        store.addReceipt(testReceipt.getId());
    }

    @Test
    void getProductFinalPrice() {

        double expectedPrice1 = 10.0 + (10.0 * 30.0 / 100); // 13.0
        assertEquals(expectedPrice1, store.getProductFinalPrice(testProduct1), 0.001);

        double expectedPrice2 = 20.0 + (20.0 * 40.0 / 100); // 28.0
        assertEquals(expectedPrice2, store.getProductFinalPrice(testProduct2), 0.001);

        double basePrice = 15.0 + (15.0 * 30.0 / 100); // 19.5
        double expectedDiscountedPrice = basePrice - (basePrice * 20.0 / 100); // 15.6
        assertEquals(expectedDiscountedPrice, store.getProductFinalPrice(testExpiringProduct), 0.001);
    }

    @Test
    void isProductExpirationDiscountable() {

        assertFalse(store.isProductExpirationDiscountable(testProduct1));

        assertTrue(store.isProductExpirationDiscountable(testExpiringProduct));

        store.setDaysBeforeExpirationThreshold(60);
        assertTrue(store.isProductExpirationDiscountable(testProduct1));
    }

    @Test
    void getId() {

        assertEquals(0, store.getId());

        store.setId(1);
        assertEquals(1, store.getId());
    }

    @Test
    void setId() {
        store.setId(5);
        assertEquals(5, store.getId());

        store.setId(10);
        assertEquals(10, store.getId());
    }

    @Test
    void getName() {
        assertEquals(testName, store.getName());
    }

    @Test
    void setName() {
        String newName = "Нов магазин";
        store.setName(newName);
        assertEquals(newName, store.getName());
    }

    @Test
    void getCashiersIds() {
        Set<Integer> cashiers = store.getCashiersIds();
        assertEquals(1, cashiers.size());
        assertTrue(cashiers.contains(testCashier.getId()));
    }

    @Test
    void setCashiersIds() {
        Set<Integer> newCashiers = new HashSet<>();
        newCashiers.add(2);
        newCashiers.add(3);

        store.setCashiersIds(newCashiers);

        Set<Integer> cashiers = store.getCashiersIds();
        assertEquals(2, cashiers.size());
        assertTrue(cashiers.contains(2));
        assertTrue(cashiers.contains(3));
        assertFalse(cashiers.contains(testCashier.getId()));
    }

    @Test
    void addCashier() {
        int newCashierId = 2;
        store.addCashier(newCashierId);

        Set<Integer> cashiers = store.getCashiersIds();
        assertEquals(2, cashiers.size());
        assertTrue(cashiers.contains(testCashier.getId()));
        assertTrue(cashiers.contains(newCashierId));
    }

    @Test
    void removeCashier() {
        store.removeCashier(testCashier.getId());

        Set<Integer> cashiers = store.getCashiersIds();
        assertEquals(0, cashiers.size());
        assertFalse(cashiers.contains(testCashier.getId()));
    }

    @Test
    void getReceiptsIds() {
        Set<Integer> receipts = store.getReceiptsIds();
        assertEquals(1, receipts.size());
        assertTrue(receipts.contains(testReceipt.getId()));
    }

    @Test
    void setReceiptsIds() {
        Set<Integer> newReceipts = new HashSet<>();
        newReceipts.add(2);
        newReceipts.add(3);

        store.setReceiptsIds(newReceipts);

        Set<Integer> receipts = store.getReceiptsIds();
        assertEquals(2, receipts.size());
        assertTrue(receipts.contains(2));
        assertTrue(receipts.contains(3));
        assertFalse(receipts.contains(testReceipt.getId()));
    }

    @Test
    void addReceipt() {
        int newReceiptId = 2;
        store.addReceipt(newReceiptId);

        Set<Integer> receipts = store.getReceiptsIds();
        assertEquals(2, receipts.size());
        assertTrue(receipts.contains(testReceipt.getId()));
        assertTrue(receipts.contains(newReceiptId));
    }

    @Test
    void removeReceipt() {
        store.removeReceipt(testReceipt.getId());

        Set<Integer> receipts = store.getReceiptsIds();
        assertEquals(0, receipts.size());
        assertFalse(receipts.contains(testReceipt.getId()));
    }

    @Test
    void addProductStock() {
        store.addProductStock(testProduct1, 5);
        assertEquals(15, store.getProductStock(testProduct1.getId()));

        Product newProduct = new Product("Нов продукт", 5.0, ProductCategory.FOOD, LocalDate.now().plusDays(30));
        newProduct.setId(4);

        store.addProductStock(newProduct, 7);
        assertEquals(7, store.getProductStock(newProduct.getId()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.addProductStock(testProduct1, -1);
        });

        String expectedMessage = "Количеството на продукт с ID " + testProduct1.getId() + " не може да бъде отрицателно";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void removeProductStock() {
        assertTrue(store.removeProductStock(testProduct1, 5));
        assertEquals(5, store.getProductStock(testProduct1.getId()));

        assertFalse(store.removeProductStock(testProduct1, 10));
        assertEquals(5, store.getProductStock(testProduct1.getId()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.removeProductStock(testProduct1, -1);
        });

        String expectedMessage = "Количеството на продукт с ID " + testProduct1.getId() + " не може да бъде отрицателно";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addProductSold() {
        store.addProductSold(testProduct1, 3);
        assertEquals(5, store.getProductsSold().get(testProduct1.getId()));

        store.addProductSold(testProduct2, 2);
        assertEquals(2, store.getProductsSold().get(testProduct2.getId()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.addProductSold(testProduct1, -1);
        });

        String expectedMessage = "Количеството на продукт с ID " + testProduct1.getId() + " не може да бъде отрицателно";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void removeProductSold() {

        assertTrue(store.removeProductSold(testProduct1, 1));
        assertEquals(1, store.getProductsSold().get(testProduct1.getId()));

        assertFalse(store.removeProductSold(testProduct1, 2));
        assertEquals(1, store.getProductsSold().get(testProduct1.getId()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.removeProductSold(testProduct1, -1);
        });

        String expectedMessage = "Количеството на продукт с ID " + testProduct1.getId() + " не може да бъде отрицателно";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getProductStock() {
        assertEquals(10, store.getProductStock(testProduct1.getId()));
        assertEquals(5, store.getProductStock(testProduct2.getId()));
        assertEquals(0, store.getProductStock(999)); // Несъществуващ продукт
    }

    @Test
    void getProductsInStock() {
        Map<Integer, Integer> stock = store.getProductsInStock();
        assertEquals(3, stock.size());
        assertEquals(10, stock.get(testProduct1.getId()));
        assertEquals(5, stock.get(testProduct2.getId()));
        assertEquals(3, stock.get(testExpiringProduct.getId()));
    }

    @Test
    void setProductsInStock() {
        Map<Integer, Integer> newStock = new HashMap<>();
        newStock.put(testProduct1.getId(), 20);
        newStock.put(4, 15);

        store.setProductsInStock(newStock);

        Map<Integer, Integer> stock = store.getProductsInStock();
        assertEquals(2, stock.size());
        assertEquals(20, stock.get(testProduct1.getId()));
        assertEquals(15, stock.get(4));
        assertNull(stock.get(testProduct2.getId()));
    }

    @Test
    void getProductsSold() {
        Map<Integer, Integer> sold = store.getProductsSold();
        assertEquals(1, sold.size());
        assertEquals(2, sold.get(testProduct1.getId()));
    }

    @Test
    void setProductsSold() {
        Map<Integer, Integer> newSold = new HashMap<>();
        newSold.put(testProduct1.getId(), 5);
        newSold.put(testProduct2.getId(), 3);

        store.setProductsSold(newSold);

        Map<Integer, Integer> sold = store.getProductsSold();
        assertEquals(2, sold.size());
        assertEquals(5, sold.get(testProduct1.getId()));
        assertEquals(3, sold.get(testProduct2.getId()));
    }

    @Test
    void getMarkupPercentages() {
        Map<ProductCategory, Double> markups = store.getMarkupPercentages();
        assertEquals(2, markups.size());
        assertEquals(30.0, markups.get(ProductCategory.FOOD), 0.001);
        assertEquals(40.0, markups.get(ProductCategory.NON_FOOD), 0.001);
    }

    @Test
    void setMarkupPercentages() {
        Map<ProductCategory, Double> newMarkups = new HashMap<>();
        newMarkups.put(ProductCategory.FOOD, 25.0);
        newMarkups.put(ProductCategory.NON_FOOD, 35.0);

        store.setMarkupPercentages(newMarkups);

        Map<ProductCategory, Double> markups = store.getMarkupPercentages();
        assertEquals(2, markups.size());
        assertEquals(25.0, markups.get(ProductCategory.FOOD), 0.001);
        assertEquals(35.0, markups.get(ProductCategory.NON_FOOD), 0.001);
    }

    @Test
    void getDaysBeforeExpirationThreshold() {
        assertEquals(testDaysBeforeExpirationThreshold, store.getDaysBeforeExpirationThreshold());
    }

    @Test
    void setDaysBeforeExpirationThreshold() {
        int newThreshold = 10;
        store.setDaysBeforeExpirationThreshold(newThreshold);
        assertEquals(newThreshold, store.getDaysBeforeExpirationThreshold());
    }

    @Test
    void getDiscountPercentNearExpiration() {
        assertEquals(testDiscountPercentNearExpiration, store.getDiscountPercentNearExpiration(), 0.001);
    }

    @Test
    void setDiscountPercentNearExpiration() {
        double newDiscount = 25.0;
        store.setDiscountPercentNearExpiration(newDiscount);
        assertEquals(newDiscount, store.getDiscountPercentNearExpiration(), 0.001);
    }

    @Test
    void calculateTotalSalariesExpense() {

        CashierService mockCashierService = mock(CashierService.class);

        try (MockedStatic<ServiceFactory> mockedFactory = Mockito.mockStatic(ServiceFactory.class)) {

            mockedFactory.when(ServiceFactory::getCashierService).thenReturn(mockCashierService);
            when(mockCashierService.findEntityById(testCashier.getId())).thenReturn(Optional.of(testCashier));

            double totalSalaries = store.calculateTotalSalariesExpense();

            assertEquals(1500.0, totalSalaries, 0.001);

            Cashier cashier2 = new Cashier("Втори касиер", 2000.0);
            cashier2.setId(2);
            store.addCashier(cashier2.getId());
            when(mockCashierService.findEntityById(cashier2.getId())).thenReturn(Optional.of(cashier2));

            totalSalaries = store.calculateTotalSalariesExpense();

            assertEquals(3500.0, totalSalaries, 0.001);

            when(mockCashierService.findEntityById(999)).thenReturn(Optional.empty());
            store.addCashier(999);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                store.calculateTotalSalariesExpense();
            });

            String expectedMessage = "Касиер с ID 999 не е намерен при изчисляване на заплати.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Test
    void calculateDeliveredGoodsExpense() {

        ProductService mockProductService = mock(ProductService.class);

        try (MockedStatic<ServiceFactory> mockedFactory = Mockito.mockStatic(ServiceFactory.class)) {

            mockedFactory.when(ServiceFactory::getProductService).thenReturn(mockProductService);
            when(mockProductService.findEntityById(testProduct1.getId())).thenReturn(Optional.of(testProduct1));
            when(mockProductService.findEntityById(testProduct2.getId())).thenReturn(Optional.of(testProduct2));
            when(mockProductService.findEntityById(testExpiringProduct.getId())).thenReturn(Optional.of(testExpiringProduct));

            double expectedStockExpense = (10.0 * 10) + (20.0 * 5) + (15.0 * 3); // 245.0
            double expectedSoldExpense = 10.0 * 2; // 20.0
            double expectedTotalExpense = expectedStockExpense + expectedSoldExpense; // 265.0

            double totalExpense = store.calculateDeliveredGoodsExpense();

            assertEquals(expectedTotalExpense, totalExpense, 0.001);

            store.setProductStock(999, 5);
            when(mockProductService.findEntityById(999)).thenReturn(Optional.empty());

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                store.calculateDeliveredGoodsExpense();
            });

            String expectedMessage = "Продукт с ID 999 не е намерен при изчисляване на разходи за налични стоки.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Test
    void calculateTotalIncome() {

        ReceiptService mockReceiptService = mock(ReceiptService.class);

        try (MockedStatic<ServiceFactory> mockedFactory = Mockito.mockStatic(ServiceFactory.class)) {

            mockedFactory.when(ServiceFactory::getReceiptService).thenReturn(mockReceiptService);
            when(mockReceiptService.findEntityById(testReceipt.getId())).thenReturn(Optional.of(testReceipt));

            double totalIncome = store.calculateTotalIncome();

            assertEquals(26.0, totalIncome, 0.001);

            Receipt receipt2 = new Receipt(1, testCashier.getId(), LocalDateTime.now(), Collections.singletonMap(testProduct2, 1));
            receipt2.setId(2);
            store.addReceipt(receipt2.getId());
            when(mockReceiptService.findEntityById(receipt2.getId())).thenReturn(Optional.of(receipt2));

            totalIncome = store.calculateTotalIncome();

            assertEquals(54.0, totalIncome, 0.001);

            when(mockReceiptService.findEntityById(999)).thenReturn(Optional.empty());
            store.addReceipt(999);

            totalIncome = store.calculateTotalIncome();
            assertEquals(54.0, totalIncome, 0.001);
        }
    }

    @Test
    void calculateProfit() {

        Store testStore = new Store("Тестов магазин", testMarkupPercentages, testDaysBeforeExpirationThreshold, testDiscountPercentNearExpiration);

        CashierService mockCashierService = mock(CashierService.class);
        ReceiptService mockReceiptService = mock(ReceiptService.class);
        ProductService mockProductService = mock(ProductService.class);

        try (MockedStatic<ServiceFactory> mockedFactory = Mockito.mockStatic(ServiceFactory.class)) {

            mockedFactory.when(ServiceFactory::getCashierService).thenReturn(mockCashierService);
            mockedFactory.when(ServiceFactory::getReceiptService).thenReturn(mockReceiptService);
            mockedFactory.when(ServiceFactory::getProductService).thenReturn(mockProductService);

            Receipt mockReceipt = mock(Receipt.class);
            when(mockReceipt.getTotalPrice()).thenReturn(100.0);
            when(mockReceiptService.findEntityById(anyInt())).thenReturn(Optional.of(mockReceipt));
            testStore.addReceipt(1);

            Cashier mockCashier = mock(Cashier.class);
            when(mockCashier.getSalary()).thenReturn(30.0);
            when(mockCashierService.findEntityById(anyInt())).thenReturn(Optional.of(mockCashier));
            testStore.addCashier(1);

            Product mockProduct = mock(Product.class);
            when(mockProduct.getUnitPurchasePrice()).thenReturn(50.0);
            when(mockProductService.findEntityById(anyInt())).thenReturn(Optional.of(mockProduct));
            testStore.setProductStock(1, 1);

            double profit = testStore.calculateProfit();

            assertEquals(20.0, profit, 0.001);

            when(mockReceipt.getTotalPrice()).thenReturn(60.0);

            profit = testStore.calculateProfit();

            assertEquals(-20.0, profit, 0.001);
        }
    }

    @Test
    void getCashDesks() {

        CashDeskService mockCashDeskService = mock(CashDeskService.class);

        try (MockedStatic<ServiceFactory> mockedFactory = Mockito.mockStatic(ServiceFactory.class)) {

            mockedFactory.when(ServiceFactory::getCashDeskService).thenReturn(mockCashDeskService);

            CashDesk cashDesk = new CashDesk(store.getId(), testCashier.getId());
            cashDesk.setId(1);

            ArrayList<CashDesk> allCashDesks = new ArrayList<>();
            allCashDesks.add(cashDesk);

            when(mockCashDeskService.getAllEntities()).thenReturn(allCashDesks);
            when(mockCashDeskService.findEntityByFilter(any())).thenReturn(Optional.of(cashDesk));

            ArrayList<CashDesk> cashDesks = store.getCashDesks();

            assertEquals(1, cashDesks.size());
            assertEquals(cashDesk, cashDesks.get(0));

            store.addCashier(2);
            when(mockCashDeskService.findEntityByFilter(any())).thenReturn(Optional.empty());

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                store.getCashDesks();
            });

            String expectedMessage = "Каса с касиер с ID 1 не съществува";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage),
                    "Expected message to contain '" + expectedMessage + "' but was '" + actualMessage + "'");

            when(mockCashDeskService.getAllEntities()).thenReturn(new ArrayList<>());

            exception = assertThrows(IllegalArgumentException.class, () -> {
                store.getCashDesks();
            });

            expectedMessage = "Няма налични каси в системата";
            actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Test
    void setProductStock() {

        int newProductId = 5;
        store.setProductStock(newProductId, 15);
        assertEquals(15, store.getProductStock(newProductId));

        store.setProductStock(testProduct1.getId(), 20);
        assertEquals(20, store.getProductStock(testProduct1.getId()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.setProductStock(testProduct1.getId(), -1);
        });

        String expectedMessage = "Количеството на продукт с ID " + testProduct1.getId() + " не може да бъде отрицателно";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}

